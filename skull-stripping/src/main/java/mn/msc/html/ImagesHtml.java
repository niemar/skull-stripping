package mn.msc.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

import mn.msc.utils.FilesUtils;

/**
 * @author niemar
 *
 */
public class ImagesHtml {
	private static final String[] IMG_FORMATS = new String[] { "jpg", "png" };
	private static final HtmlAttributes P_ATTRIBUTES = new HtmlAttributes("padding-left", "20px");
	private HtmlCanvas html;

	public ImagesHtml() {
		html = new HtmlCanvas();
	}

	public void generate(String baseDir, List<String> imagesDirs, List<String> columnsTitles) {
		generate(baseDir, imagesDirs, columnsTitles, baseDir + "/images.html");
	}

	public void generate(String baseDir, List<String> imagesDirs, List<String> columnsTitles, String htmlFilename) {
		try {
			html.html()
				.head()
				.title()
				.content("Results for images in " + baseDir + " /input")
				._head()
				.body()
				.table()
				.tr();
				buildColumntTitles(columnsTitles);
		    	html._tr();
		    
			buidTableColumn(baseDir, imagesDirs);
			html._table()._body()._html();
			FileUtils.write(new File(htmlFilename), html.toHtml(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildColumntTitles(List<String> columnsTitles) throws IOException {
		for (String title : columnsTitles) {
			html.th().content(title);
		}
	}

	private void buidTableColumn(String baseDir, List<String> imagesDirs) throws IOException {
		List<List<File>> imagesDirsList = get2dImagesList(imagesDirs, IMG_FORMATS);
		List<File> jaccardFiles = getJaccardFiles(imagesDirs.get(imagesDirs.size() - 1));
		
		for (int i = 0; i < imagesDirsList.get(0).size(); ++i) {
			html.tr();
			for (int j = 0; j < imagesDirsList.size(); j++) {
				File image = imagesDirsList.get(j).get(i);
				String src = FilesUtils.getRelativePathToFile(baseDir, image.getPath());
				html.td().render(new IconImage(src, "100%"))._td();
			}
			if(!jaccardFiles.isEmpty()) {
				List<String> jacardParameters = getDataFromFile(jaccardFiles.get(i));
				if(jacardParameters != null & !jacardParameters.isEmpty())
					generateJaccard(jacardParameters);			
			}
			html._tr();
		}
	}

	private void generateJaccard(List<String> jacardParameters) throws IOException {	
		html.td(P_ATTRIBUTES);	
		for (String string : jacardParameters) {
			html.p().h2().content(string)._p();
		}
		html._td();
	}

	private List<List<File>> get2dImagesList(List<String> imagesDirs, String [] formats) {
		List<List<File>> imagesDirsList = new ArrayList<>();
		for (String dir : imagesDirs) {
			Collection<File> images = FileUtils.listFiles(new File(dir), formats, false);
			List<File> array = new ArrayList<>(images);
			Collections.sort(array);
			imagesDirsList.add(array);
		}
		return imagesDirsList;
	}

	public static void main(String[] args) {
		String baseDir = "src/main/resources/images3";
		String inputDir = baseDir + "/input";
		String outputDir = baseDir + "/output";
		ImagesHtml imagesHtml = new ImagesHtml();
		imagesHtml.generate(baseDir, Arrays.asList(new String[] { inputDir, outputDir }),
				Arrays.asList(new String[] { "input", "output" }));
	}
	
	private List<File> getJaccardFiles(String dir) {
		Collection<File> images = FileUtils.listFiles(new File(dir), new String[] {"txt"}, false);
		List<File> array = new ArrayList<>(images);
		Collections.sort(array);
		return array;
	}
	
	private List<String> getDataFromFile(File file) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(file));
		List<String> lines = new ArrayList<>();
		String line;
		while( (line = bf.readLine() ) != null) {
			lines.add(line);
		}
		return lines;
	}
}
