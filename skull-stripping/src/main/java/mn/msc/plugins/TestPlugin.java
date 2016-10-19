package mn.msc.plugins;

import fiji.threshold.Auto_Local_Threshold;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * @author niemar
 *
 */
public class TestPlugin implements PlugInFilter {

	@Override
	public int setup(String arg, ImagePlus imp) {
		return 0;
	}

	@Override
	public void run(ImageProcessor ip) {
		Auto_Local_Threshold thresholdPlugin = new Auto_Local_Threshold();
		
	}

}
