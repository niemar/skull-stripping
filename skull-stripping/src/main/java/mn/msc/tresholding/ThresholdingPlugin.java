package mn.msc.tresholding;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * @author niemar
 *
 */
public class ThresholdingPlugin implements PlugInFilter {
	private ImagePlus image;
	
	@Override
	public int setup(String arg, ImagePlus image) {
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		//ip.autoThreshold();
	}

}
