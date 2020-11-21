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

	
	public static double Sum(Double[] numbers) {
		double sum = 0;
		for (int i = 0; i < numbers.length; i++) {
			sum += numbers[i];
		}
		return sum;
	}

}
