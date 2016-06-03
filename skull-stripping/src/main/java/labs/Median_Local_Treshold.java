package labs;

import java.util.ArrayList;
import java.util.Collections;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * ProcessPixels
 *
 * A template for processing each pixel of either
 * GRAY8, GRAY16, GRAY32 or COLOR_RGB images.
 *
 * @author The Fiji Team
 */
public class Median_Local_Treshold implements PlugInFilter {
	protected ImagePlus imp;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return  DOES_8G;
	}

	/**
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	@Override
	public void run(ImageProcessor ip) {
		int size = (int) IJ.getNumber("Podaj szerokość :", DOES_8G);
		int distance = (size - 1) / 2;
		int countPixels = (int) (size * size);
		double localTreshold = 0;
		double sum = 0;
		
		int [][] oldPixels = ip.getIntArray();
		int [][] newPixels = new int[ip.getWidth()][ip.getHeight()];
		ArrayList<Integer> list = new ArrayList<Integer>(25);
		double weight;
		for(int x = distance; x < ip.getWidth() - distance; ++x) {
			for(int y = distance; y < ip.getHeight() - distance; ++y) {
				list.clear();
				sum = 0;
				for(int i = -distance; i < distance + 1; ++i) {
					for(int j = -distance; j < distance + 1; ++j) {
						list.add(oldPixels[x + i][y + j]);					
					}
				} // 0 1 2 3 4
				Collections.sort(list);
				if(list.size() % 2 == 0)
					localTreshold = list.get(list.size() / 2) + list.get((list.size()/2) - 1) / 2; 
				else
					localTreshold = (int) (list.get(list.size() / 2));
				if(oldPixels[x][y] >= localTreshold)			
					newPixels[x][y] = 255;
				else
					newPixels[x][y] = 0;
			}
		}
		ip.setIntArray(newPixels);
	}


	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads an
	 * image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Median_Local_Treshold.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample
		ImagePlus image = IJ.openImage("http://imagej.net/images/Cell_Colony.jpg");
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
