package mn.msc.morphobrainextractor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.io.FileUtils;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.util.StringUtils;

import canny.Canny_Edge_Detector;
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
public class MorphoBrainExtractor implements PlugInFilter {
	private int minBrainPixelsCount;

	private static DecimalFormat FORMAT = new DecimalFormat("0.00");

	private String rawImageUrl;
	private String manualSegmentationUrl;
	private ImagePlus image;

	@Override
	public int setup(String arg, ImagePlus image) {
		String[] args = arg.split(",");
		this.rawImageUrl = args[0];
		if (args.length > 1)
			this.manualSegmentationUrl = args[1];
		this.image = image;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		if(ip != null)
			this.minBrainPixelsCount = ip.getWidth()  * ip.getHeight() / 10;

		image.show();
		
		// 1. Erode
		erode(ip, 2);
		// erode(ip, 2);
		// erode(ip, 2);

		// ip.medianFilter();

		ManyBlobs allBlobs = new ManyBlobs(image);
		allBlobs.setBackground(0);
		allBlobs.findConnectedComponents();
		allBlobs = allBlobs.filterBlobs(500, Integer.MAX_VALUE, Blob.GETENCLOSEDAREA);
		if (allBlobs.isEmpty())
			return;
		allBlobs.sort((Blob b1, Blob b2) -> (int) b2.getEnclosedArea() - (int) b1.getEnclosedArea());

		// 2. Get brain component
		Blob brainComponent = allBlobs.get(0);

		// 3. Dilate
		// dilate(ip, 2);

		ByteProcessor byteProc = new ByteProcessor(ip.getWidth(), ip.getHeight());
		ImagePlus brainWhiteComponent = new ImagePlus("cc", byteProc);
		brainWhiteComponent.show();
		drawWhite(brainComponent, byteProc);
		ip.setIntArray(byteProc.getIntArray());

		ImageProcessor cannyProc = canny(ip);
		byteProc = (ByteProcessor) cannyProc;

		// return;
		if(!StringUtils.isEmpty(manualSegmentationUrl)) {
			ByteProcessor mByteProc = new ByteProcessor(ip.getWidth(), ip.getHeight());
			ImagePlus manualSegmentation = new ImagePlus(manualSegmentationUrl);
			mByteProc = (ByteProcessor) manualSegmentation.getProcessor();
		// check noise
		// checkNoise(manualSegmentation, mByteProc, "Manual");
		// checkNoise(cc, byteProc, "MY");
			if(mByteProc != null)
				calculateJaccardIndex((int[][]) byteProc.getIntArray(), mByteProc.getIntArray());
		}
		// drawGrayBrainWithWhiteHoles(brainComponent, byteProc);
		// cc.show();
		drawExtracted(byteProc, rawImageUrl);
		ip.setIntArray(byteProc.getIntArray());
		
		brainWhiteComponent.hide();
		image.hide();
	}

	private void drawExtracted(ByteProcessor byteProc, String rawImageUrl2) {
		ImagePlus rawImg = new ImagePlus(rawImageUrl2);
		ImageProcessor rawProc = rawImg.getProcessor();
		// rawImg.show();
		int[][] pixels = byteProc.getIntArray();
		int[][] rawPixels = rawProc.getIntArray();
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j] == 255) {
					pixels[i][j] = rawPixels[i][j];
				}
			}
		}
		byteProc.setIntArray(pixels);
	}

	private ImageProcessor canny(ImageProcessor ip) {
		Canny_Edge_Detector canny = new Canny_Edge_Detector();

		ImagePlus img = new ImagePlus("white on the black background", ip);
		img.show();
		canny.setSourceImage(img);
		canny.run("");
		ImageProcessor ip2 = img.getProcessor();
		// img.hide();
		// ImageProcessor iproc = new ByteProcessor(ip.getWidth(),
		// ip.getHeight());
		// iproc.setIntArray(ip.getIntArray());
		// ip2.invert();
		// ip2.dilate();
		// ip2.erode();
		ip2.dilate();
		// ip2.erode();
		// ip2.dilate();
		// iproc.dilate();
		// img.show();
		// img.hide();
		
		fill(img);
		// kiedy nie udalo siê wypelnic obrazu bo kontur obrazu nie jest zamkniety
		if(isEmptyImage(ip2)) {
			img.hide();
			return ip;
		}
		// removeSmallObjects(img);
		ip2.dilate();
		// ip2.invert();
		// img.show();
		img.hide();

		return ip2;
	}

	private boolean isEmptyImage(ImageProcessor ip2) {
		int[][] pixels = ip2.getIntArray();
		int counter = 0;
		for (int[] is : pixels) {
			for (int i = 0; i < is.length; i++) {
				// liczymy czarne pixele
				if (is[i] > 250) {
					counter++;
				}
			}
		}
		return counter < minBrainPixelsCount ? true : false;
	}

	private void fill(ImagePlus img) {
		ManyBlobs allBlobs = new ManyBlobs(img);
		allBlobs.setBackground(0);
		allBlobs.findConnectedComponents();
		allBlobs = allBlobs.filterBlobs(50, Integer.MAX_VALUE, Blob.GETENCLOSEDAREA);
		if (allBlobs.isEmpty())
			return;
		allBlobs.sort((Blob b1, Blob b2) -> (int) b2.getEnclosedArea() - (int) b1.getEnclosedArea());
		Blob brainComponent = allBlobs.get(0);
		img.getProcessor().set(0);
		brainComponent.draw(img.getProcessor(), Blob.DRAW_HOLES);

	}

	private void removeSmallObjects(ImagePlus img) {
		ManyBlobs allBlobs = new ManyBlobs(img);
		allBlobs.setBackground(0);
		allBlobs.findConnectedComponents();
		allBlobs = allBlobs.filterBlobs(50, Integer.MAX_VALUE, Blob.GETENCLOSEDAREA);
		if (allBlobs.isEmpty())
			return;
		allBlobs.sort((Blob b1, Blob b2) -> (int) b2.getEnclosedArea() - (int) b1.getEnclosedArea());
		Blob brainComponent = allBlobs.get(0);
		brainComponent.draw(img.getProcessor(), Blob.DRAW_HOLES);
	}

	private void erode(ImageProcessor ip, int erodeCount) {
		ByteProcessor byteProc = (ByteProcessor) ip;
		byteProc.erode(erodeCount, 0);
	}

	private void dilate(ImageProcessor ip, int erodeCount) {
		ByteProcessor byteProc = (ByteProcessor) ip;
		byteProc.dilate(erodeCount, 0);
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
		// System.out.println(counter);
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

	public void drawWhite(Blob brainComponent, ByteProcessor byteProc) {
		Blob.setDefaultColor(Color.WHITE);
		brainComponent.draw(byteProc, 0);
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

		double jaccardIndex = (double) andPixels / (double) orPixels;
		double dice = (double) (2.0 * (double) andPixels
				/ ((double) brainSegmentPixelsCount + (double) manSegPixelsCount));
		String data = "Jaccard = " + FORMAT.format(jaccardIndex) + "\n" + "Dice = " + FORMAT.format(dice);
		System.out.println("Jaccard = " + jaccardIndex + "    Dice = " + dice);
		try {
			FileUtils.writeStringToFile(new File(manualSegmentationUrl.replace(".jpg", ".txt")), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void loadRawImage(String url) {
		if (StringUtils.isEmpty(url))
			return;
		ImagePlus manualSegmentation = new ImagePlus(url);
		// ByteProcessor mByteProc = new ByteProcessor(ip.getWidth(),
		// ip.getHeight());
		// mByteProc = (ByteProcessor) manualSegmentation.getProcessor();
	}
}
