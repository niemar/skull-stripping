package mn.msc.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import ij.ImagePlus;
import ij.io.FileSaver;

public class FilesUtils {
	public static InputDirFilter INPUT_DIR_FILTER = new InputDirFilter();
	
	public static String getRelativePathToFile(String basePath, String filePath) {
	    Path pathAbsolute = Paths.get(filePath);
        Path pathBase = Paths.get(basePath);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
	}
	
	public static void saveAsJpg(ImagePlus image, String imagePath ) {
		FileSaver fileSaver = new FileSaver(image);
		fileSaver.saveAsJpeg(imagePath);
	}
	
	public static void createDirIfNotExist(String dirPath) {
		new File(dirPath).mkdirs();
	}
	
	static class InputDirFilter extends DirectoryFileFilter {
		private static final long serialVersionUID = 2441990928585389175L;

		@Override
	    public boolean accept(File file) {
	        return file.isDirectory() && !file.getPath().contains("Output")
	        		&& !file.getPath().contains("manual");
	    }
	}
}
