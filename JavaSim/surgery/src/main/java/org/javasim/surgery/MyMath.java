package org.javasim.surgery;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class MyMath {

	public static Double Mean(Double[] numbers) {
		return Sum(numbers) / numbers.length;
	}

	
	public static double ConfidenceInterval(Double[] numbers, double confidenceLevel) {

		// if the value doesn't make sense just use 95% confidence level
		if (confidenceLevel > 1 || confidenceLevel < 0)
			confidenceLevel = 0.95; 

		SummaryStatistics stats = new SummaryStatistics();
		for (double number : numbers) {
			stats.addValue(number);
		}

		TDistribution tDist = new TDistribution(stats.getN() - 1); // T distribution

		double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidenceLevel) / 2); // Calculate critical

		
		double confidenceInterval = critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN()); // Calculate confidence interval

		return confidenceInterval;
	}
	
	public static double ConfidenceIntervalBasic(Double[] numbers, double critVal) {
		double confidenceInterval = critVal * (StandardDeviation(numbers) / Math.sqrt(numbers.length));
		return confidenceInterval;
	}
	
	/* Use t-test to check if the difference between 2 samples is statistically significant */
    public static double PairwiseComparison(Double[] numbers1, Double[] numbers2) {
    	if (numbers1.length != numbers2.length) {
    		throw new IllegalArgumentException("Lists must be the same length");
    	}
    	
    	int n = numbers1.length;
    	double sumOfDifferences = 0;
    	Double[] sums = new Double[n];
    	for (int i = 0; i < n; i++) {
    		sums[i] = numbers1[i]-numbers2[i];
    		sumOfDifferences += sums[i];
    	}
    	double meanOfDifferences = sumOfDifferences/n;
    	double stddevOfDifferences = MyMath.StandardDeviation(sums);
    	double tValue = meanOfDifferences/(stddevOfDifferences/Math.sqrt(n));
    	return tValue;
    }

	public static double StandardDeviation(Double[] numbers) {
		double mean = Mean(numbers);
		double sumOfSquareDiffs = 0;
		
		for (double num : numbers) {
			sumOfSquareDiffs += Math.pow(num - mean, 2);
		}
		double meanOfSquareDiffs = sumOfSquareDiffs / (numbers.length - 1);
		double standardDeviation = Math.sqrt(meanOfSquareDiffs);
		
		return standardDeviation;
	}
	
	public static double Sum(Double[] numbers) {
		double sum = 0;
		for (int i = 0; i < numbers.length; i++) {
			sum += numbers[i];
		}
		return sum;
	}

}
