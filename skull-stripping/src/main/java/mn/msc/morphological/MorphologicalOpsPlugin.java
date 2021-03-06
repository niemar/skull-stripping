package mn.msc.morphological;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * @author niemar
 *
 */
public class MorphologicalOpsPlugin implements PlugInFilter {
	private ImagePlus image;
	
	@Override
	public int setup(String arg, ImagePlus image) {
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		ByteProcessor byteProc = (ByteProcessor) ip;	
		byteProc.erode(2, 0);
		byteProc.dilate(2, 0);
	}

	
}
