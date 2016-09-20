package mn.msc.brainextractor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

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
	private static DecimalFormat FORMAT = new DecimalFormat("0.00");

	private String manualSegmentationUrl;
	private ImagePlus image;

	@Override
	public int setup(String arg, ImagePlus image) {
		this.manualSegmentationUrl = arg.split(",")[1];
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		ManyBlobs allBlobs = new ManyBlobs(image);
		allBlobs.setBackground(0);
		allBlobs.findConnectedComponents();
		allBlobs = allBlobs.filterBlobs(500, Integer.MAX_VALUE, Blob.GETENCLOSEDAREA);
		if (allBlobs.isEmpty())
			return;
		allBlobs.sort((Blob b1, Blob b2) -> (int) b2.getEnclosedArea() - (int) b1.getEnclosedArea());
		Blob brainComponent = allBlobs.get(0);

		ByteProcessor byteProc = new ByteProcessor(ip.getWidth(), ip.getHeight());
		ImagePlus cc = new ImagePlus("cc", byteProc);
		drawGrayBrainWithWhiteHoles(brainComponent, byteProc);
		ip.setIntArray(byteProc.getIntArray());

		if (StringUtils.isEmpty(manualSegmentationUrl))
			return;
		ByteProcessor mByteProc = new ByteProcessor(ip.getWidth(), ip.getHeight());
		ImagePlus manualSegmentation = new ImagePlus(manualSegmentationUrl);
		mByteProc = (ByteProcessor) manualSegmentation.getProcessor();

		// check noise
		// checkNoise(manualSegmentation, mByteProc, "Manual");
		// checkNoise(cc, byteProc, "MY");

		calculateJaccardIndex((int[][]) byteProc.getIntArray(), mByteProc.getIntArray());
	}

	private void checkNoise(ImagePlus manualSegmentation, ByteProcessor mByteProc, String string) {
		System.out.println(string);
		int[][] pixels = mByteProc.getIntArray();
		int counter = 0;
		for (int[] is : pixels) {
			for (int i = 0; i < is.length; i++) {
				if (isPixelNoise(is[i])) {
					counter++;
				}
			}
		}
		System.out.println(counter);
		mByteProc.setIntArray(pixels);
		// manualSegmentation.show("manual");
	}

	private boolean isPixelNoise(int i) {
		return i < 50 && i > 0;
	}

	public void drawGrayBrainWithWhiteHoles(Blob brainComponent, ByteProcessor byteProc) {
		Blob.setDefaultColor(Color.WHITE);
		brainComponent.draw(byteProc, Blob.DRAW_LABEL);
		Blob.setDefaultColor(Color.GRAY);
		brainComponent.draw(byteProc, Blob.DRAW_HOLES);
	}

	int calculateJaccardIndex(int[][] brainSegment, int[][] manSegPixels) {
		int brainSegmentPixelsCount = 0, manSegPixelsCount = 0, andPixels = 0, orPixels = 0;
		for (int i = 0; i < brainSegment.length; i++) {
			for (int j = 0; j < brainSegment[i].length; j++) {

				// noise (wyzerowac zeby nie brac pod uwage)
				if (isPixelNoise(manSegPixels[i][j]))
					manSegPixels[i][j] = 0;
				if (isPixelNoise(brainSegment[i][j]))
					brainSegment[i][j] = 0;

				if (manSegPixels[i][j] != 0)
					manSegPixelsCount++;
				if (brainSegment[i][j] != 0)
					brainSegmentPixelsCount++;
				if (manSegPixels[i][j] != 0 && brainSegment[i][j] != 0)
					andPixels++;
				if (manSegPixels[i][j] != 0 || brainSegment[i][j] != 0)
					orPixels++;
			}
		}
		// System.out.println("brainSegmentPixelsCount = " +
		// brainSegmentPixelsCount);
		// System.out.println("manSegPixelsCount = " + manSegPixelsCount);
		// System.out.println("and = " + andPixels);
		// System.out.println("or = " + orPixels);
		double jaccardIndex = (double) andPixels / (double) orPixels;
		// System.out.println("Jaccard = " + FORMAT.format(jaccardIndex) );
		double dice = (double) (2.0 * (double) andPixels
				/ ((double) brainSegmentPixelsCount + (double) manSegPixelsCount));
		// System.out.println("Dice = " + dice );
		String data = "Jaccard = " + FORMAT.format(jaccardIndex) + "\n" + "Dice = " + FORMAT.format(dice);
		try {
			FileUtils.writeStringToFile(new File(manualSegmentationUrl.replace(".jpg", ".txt")), data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}