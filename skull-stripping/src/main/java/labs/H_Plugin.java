package labs;

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.util.Arrays;
import ij.gui.Plot;

public class H_Plugin implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL;
	}
 
	public void run(ImageProcessor ip) {
		//HistogramWindow hw = new HistogramWindow(imp);
		int [] hist = ip.getHistogram();
		double [] array = new double[hist.length];
		for(int i = 0 ; i < array.length; ++i) {
			array[i] = (double) hist[i];	
		}
		double [] xArray = new double[hist.length];
		for(int i = 0 ; i < array.length; ++i) {
			array[i] = i;	
		}
		
		//int[] histCopy = Arrays.copyOf(hist, hist.length);
		
		int min = findMinIndex(hist);
		//IJ.log("min index = " + min);
		int max = findMaxIndex(hist);
		IJ.log("max index = " + max);
		int binSize = 1;

		int middle = (max + min) / 2;
		int sumLeft = getSum(0, middle, hist);
		int sumRight = getSum(middle+1, hist.length, hist);
		while(true) {
			if(sumRight > sumLeft) {
				if(max < 0)
					break;
				sumRight -= hist[max--];
				if (((min + max) / 2) < middle) {
                   sumRight += hist[middle];
                   sumLeft -= hist[middle--];
               	}
			}
			else if (sumLeft > sumRight) {
				sumLeft -= hist[min++]; 
               	if (((min + max) / 2) > middle) {
                   sumLeft += hist[middle + 1];
                   sumRight -= hist[middle + 1];
                   middle++;
               	}
			}
			else if (Math.abs(sumLeft - sumRight) < 1)
				break;
			Plot p = new Plot("Histogram", "x", "y", xArray, array);
		}
		IJ.log("middle = " + middle);
		
	}
	
	public int findMinIndex(int [] hist) {
		int i = 0;
		while(hist[i] == 0) {
			++i;
		}
		return i;
	}
	
	public int findMaxIndex(int [] hist) {
		int i = 255;
		while(hist[i] == 0) {
			--i;
		}
		return i;
	}

	public int getSum(int from, int to, int [] hist) {
		int sum = 0;
		for(int i = from; i < to; ++i) {
			sum += hist[i];
		}
		return sum;
	}

	
}
