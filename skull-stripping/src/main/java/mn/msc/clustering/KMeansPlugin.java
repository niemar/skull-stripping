package mn.msc.clustering;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.process.StackConverter;

import net.sf.ij_plugins.clustering.KMeans2D;
import net.sf.ij_plugins.clustering.KMeansConfig;

/**
 * @author namer
 *
 */
public class KMeansPlugin implements PlugInFilter {
	private static final int[] CLUSTERS_COLORS = new int[] { 0, 255, 122 };
	private static final int CLUSTERS_N = 3;

	private Map<Integer, Integer> colorsMapping;
	private ImagePlus image;

	@Override
	public int setup(String arg, ImagePlus image) {
		ImageConverter ic = new ImageConverter(image);
		ic.convertToGray8();
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		process(ip);
	}

	public void process(ImageProcessor ip) {
		KMeansConfig config = new KMeansConfig();
		config.setNumberOfClusters(CLUSTERS_N);
		// config.setPrintTraceEnabled(true);
		ImagePlus stack = convertToFloatStack(image);
		final KMeans2D kMeans = new KMeans2D(config);
		final ByteProcessor bp = kMeans.run(stack.getStack());	
		initColorsMap(kMeans.getClusterCenters());
		mapToGrayImage(kMeans.getClusterCenters(), bp);
		image.setProcessor(bp);
	}

	private void initColorsMap(float[][] clusterCenters) {
		Integer[] centers = to1dSortedArray(clusterCenters);
		colorsMapping = new HashMap<>();
		for (int i = 0; i < centers.length; i++) {
			colorsMapping.put(centers[i], CLUSTERS_COLORS[i]);
		}
	}

	private Integer[] to1dSortedArray(float[][] clusterCenters) {
		Integer[] centers = new Integer[CLUSTERS_N];
		for (int i = 0; i < CLUSTERS_N; i++) {
			centers[i] = (int) clusterCenters[i][0];
		}
		Arrays.sort(centers);
		return centers;
	}

	private void mapToGrayImage(float[][] clusterCenters, ByteProcessor bp) {
		int[][] pixels = bp.getIntArray();
		int pixelClusterIndex = 0;
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				pixelClusterIndex = pixels[i][j];
				Integer clusterIntensity = (int) clusterCenters[pixelClusterIndex][0];
				pixels[i][j] = colorsMapping.get(clusterIntensity);
			}
		}
		bp.setIntArray(pixels);
	}

	static ImagePlus convertToFloatStack(final ImagePlus src) {
		final ImagePlus dest = new Duplicator().run(src);
		// Remember scaling setup
		final boolean doScaling = ImageConverter.getDoScaling();
		try {
			// Disable scaling
			ImageConverter.setDoScaling(false);
			if (src.getType() == ImagePlus.COLOR_RGB) {
				if (src.getStackSize() > 1) {
					throw new IllegalArgumentException("Unsupported image type: RGB with more than one slice.");
				}
				final ImageConverter converter = new ImageConverter(dest);
				converter.convertToRGBStack();
			}
			if (dest.getStackSize() > 1) {
				final StackConverter converter = new StackConverter(dest);
				converter.convertToGray32();
			} else {
				final ImageConverter converter = new ImageConverter(dest);
				converter.convertToGray32();
			}
			return dest;
		} finally {
			// Restore original scaling option
			ImageConverter.setDoScaling(doScaling);
		}
	}

	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins
		// menu
		Class<?> clazz = KMeansPlugin.class;
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
