package mn.msc;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import fiji.threshold.Auto_Local_Threshold;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import mn.msc.brainextractor.BrainExtractor;
import mn.msc.clustering.KMeansPlugin;
import mn.msc.html.ImagesHtml;
import mn.msc.morphological.MorphologicalOpsPlugin;
import mn.msc.utils.FilesUtils;
import mn.msc.utils.PluginProperties;

public class Main {

	private static final String[] IMG_FORMATS = new String[] { "jpg", "png" };

	public static void main(String[] args) throws InterruptedException {
		Class<?> clazz = KMeansPlugin.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		String baseDir = "src/main/resources/new_images";
		//String inputDirName = "/788_6_rawmri_jpg";
		String inputDirName = "/0";
		String manualSegDirName = inputDirName +"_manual_segmentation";
		
		//runOneOutputDir(baseDir, inputDirName, null);
		runOneOutputDir(baseDir, inputDirName, manualSegDirName);
		//runInSubdirsD(baseDir);
	}

	private static void runInSubdirsD(String baseDir) {
		List<File> imagesDirs = Arrays.asList(new File(baseDir).listFiles((FileFilter) FilesUtils.INPUT_DIR_FILTER));
		for (File dir : imagesDirs) {
			runOneOutputDir(baseDir, "/" + FilesUtils.getRelativePathToFile(baseDir, dir.getPath()), null);
		}
	}

	private static void runOneOutputDir(String baseDir, String inputDir, String manualSegDirName) {
		
		PluginProperties kmeans = new PluginProperties(KMeansPlugin.class, baseDir + "/kmeansOutputDir" 
				+ inputDir.replace('/', '_'), null, "Kmeans clustering", false);
		PluginProperties niblack = new PluginProperties(Auto_Local_Threshold.class, baseDir + "/niblackOutputDir" 
				+ inputDir.replace('/', '_'), "Niblack", "Niblack thresholding", true);
		PluginProperties otsu = new PluginProperties(Auto_Local_Threshold.class, baseDir + "/otsuOutputDir"
				+ inputDir.replace('/', '_'), "Otsu", "Otsu thresholding", true);
		PluginProperties morpho = new PluginProperties(MorphologicalOpsPlugin.class, baseDir + "/morphoOutputDir"
				+ inputDir.replace('/', '_'), null, "Morphological", true);
		PluginProperties brainExtractor = new PluginProperties(BrainExtractor.class, baseDir + "/brainExtractorOutputDir"
				+ inputDir.replace('/', '_'), null, "Extracted brain", true);
				
		PluginProperties thresholding = otsu;
		List<PluginProperties> plugins = Arrays.asList(new PluginProperties[] { kmeans, thresholding, morpho, brainExtractor});
		
		runManyImages(plugins, baseDir, baseDir + inputDir, manualSegDirName == null ? null : baseDir + manualSegDirName);
	}

	private static void runManyImages(List<PluginProperties> plugins, String baseDir,
			String inputDir, String manualSegDirName) {
		for (PluginProperties pluginProperties : plugins) {
			FilesUtils.createDirIfNotExist(pluginProperties.getOutputDir());
		}
		FilesUtils.createDirIfNotExist(baseDir + manualSegDirName);
		
		
		ImageJ imageJ = new ImageJ();
		
		List<File> imagesFiles = new ArrayList<>(FileUtils.listFiles(new File(inputDir), IMG_FORMATS, false));

		for (File file : imagesFiles) {
			ImagePlus image = IJ.openImage(file.getPath());
			ImagePlus originalImage = image.duplicate();
			image.show();
			// run plugins
			plugins.get(plugins.size() - 1).setArgs(file.getPath());
			ImagePlus img;
			for (PluginProperties plugin : plugins) {
				//if(plugin.duplicateImage())
				//	img = image.duplicate();
			//	else
				//	img = image;
				IJ.runPlugIn(image, plugin.getClazz().getName(), plugin.getArgs());
				FilesUtils.saveAsJpg(image,
						plugin.getOutputDir() + "/" + FilesUtils.getRelativePathToFile(inputDir, file.getPath()));

			}
			image.hide();
		}

		imageJ.dispose();

		List<String> imagesDirs = new ArrayList<>();
		imagesDirs.add(inputDir);
		imagesDirs.addAll(PluginProperties.getDirs(plugins));
		
		List<String> columnTitles = new ArrayList<>();
		columnTitles.add(FilesUtils.getRelativePathToFile(baseDir, inputDir));
		columnTitles.addAll(PluginProperties.getColumnsDisplayedNames(plugins));
		
		// manual segmentation
		if(manualSegDirName != null) {
			imagesDirs.add(manualSegDirName);
			columnTitles.add("Manual segmentation");
		}
		
		ImagesHtml imagesHtml = new ImagesHtml();
		imagesHtml.generate(baseDir, imagesDirs, columnTitles, inputDir + ".html");
	}

}
