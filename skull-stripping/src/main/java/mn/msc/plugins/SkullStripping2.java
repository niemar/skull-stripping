package mn.msc.plugins;
import ij.*;
import ij.plugin.*;

public class SkullStripping2 implements PlugIn {

	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		IJ.run(imp, "8-bit", "");
		IJ.runPlugIn(imp, "net.sf.ij_plugins.clustering.KMeansClusteringPlugin", "number_of_clusters=3 cluster_center_tolerance=0.00010000 enable_randomization_seed randomization_seed=48");
		imp.close();
		IJ.setAutoThreshold(imp, "Otsu");
		Prefs.blackBackground = false;
		IJ.run(imp, "Convert to Mask", "");
		IJ.run(imp, "Close-", "");
	}

}
