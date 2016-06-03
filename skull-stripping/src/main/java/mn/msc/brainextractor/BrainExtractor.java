package mn.msc.brainextractor;

import java.awt.Polygon;
import java.util.List;

import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * @author niemar
 *
 */
public class BrainExtractor implements PlugInFilter {
	private ImagePlus image;
	
	@Override
	public int setup(String arg, ImagePlus image) {
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		ManyBlobs allBlobs = new ManyBlobs(image);
		allBlobs.findConnectedComponents();
		allBlobs = allBlobs.filterBlobs(500, Integer.MAX_VALUE,        
			    Blob.GETENCLOSEDAREA); 
		allBlobs.sort( (Blob b1, Blob b2) -> (int )b2.getAreaConvexHull() - (int)b1.getAreaConvexHull());
		ByteProcessor byteProc = new ByteProcessor(ip.getWidth(), ip.getHeight());
		ImagePlus cc = new ImagePlus("cc", byteProc);

		
		//byteProc.setIntArray(a);
		byteProc.invert();
		allBlobs.get(0).draw(byteProc);
		//cc.show();
		ip.setIntArray(byteProc.getIntArray());

		System.out.println();
	}
}