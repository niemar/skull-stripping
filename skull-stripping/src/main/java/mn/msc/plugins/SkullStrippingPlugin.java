package mn.msc.plugins;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class SkullStrippingPlugin implements PlugInFilter {
	protected ImagePlus image;

	// image property members
	private int width;
	private int height;
	
	
	public SkullStrippingPlugin() {
		
	}
	
	@Override
	public int setup(String arg, ImagePlus image) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
        ImageConverter ic = new ImageConverter(image);
        ic.convertToGray8();
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		// get width and height
		width = ip.getWidth();
		height = ip.getHeight();
		process(ip);
		
		FileSaver fileSaver = new FileSaver(image);
		fileSaver.saveAsJpeg("src/main/resources/output/file.jpg");
		//
	}

	// Select processing method depending on image type
	public void process(ImageProcessor ip) {
		
	}

	public void showAbout() {
		IJ.showMessage("Skull striping",
			"implementation of skull stripping algorithm"
		);
	}
	
	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = SkullStrippingPlugin.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample
		ImagePlus image = IJ.openImage("src/main/resources/images/2gora.jpg");
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}

}
