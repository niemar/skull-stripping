package mn.msc.plugins;

import ij.*;
import ij.plugin.*;

public class SkullStripping1 implements PlugIn {
	private static int clustersNumbers = 0; 

	public void run(String arg) {	
		ImagePlus imp = IJ.getImage();
		IJ.run(imp, "8-bit", "");
		IJ.runPlugIn(imp, "net.sf.ij_plugins.clustering.KMeansClusteringPlugin", "number_of_clusters=3 cluster_center_tolerance=0.00010000 enable_randomization_seed randomization_seed=48");
		
		IJ.selectWindow("Clusters" + clustersNumbers++);
		imp = IJ.getImage();
		
		IJ.setAutoThreshold(imp, "Otsu");
		Prefs.blackBackground = false;
		IJ.run(imp, "Convert to Mask", "");
		IJ.run(imp, "Close-", "");
		System.out.println(clustersNumbers);
		
	}

}