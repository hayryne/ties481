/*
 * Copyright 1990-2008, Mark Little, University of Newcastle upon Tyne
 * and others contributors as indicated 
 * by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 1990-2008,
 */

package org.javasim.surgery;

import java.lang.Thread.State;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.javasim.Simulation;
import org.javasim.SimulationException;

public class Main {
	public static void main(String[] args) throws SimulationException {

		int n = 20; // simulation rounds
		double critVal = 2.093; // critical value for calculating confidence interval

		Double[] blockedTimes = new Double[n];
		Double[] queueLengths = new Double[n];
		Double[] operationUtilizations = new Double[n];
		// Read last run's results to an array for later comparison
		Double[] lastBlockedTimes = readLastResults(SurgeryUnit.BLOCK_RESULTS_FILE);
		Double[] lastQueueLengths = readLastResults(SurgeryUnit.QUEUE_RESULTS_FILE);

		for (int i = 0; i < n; i++) {
			SurgeryUnit m = new SurgeryUnit(0); // Probability higher than 0 results in patient complications being
													// turned on
			m.await();
			blockedTimes[i] = m.OperationRoomBlockingProbability;
			queueLengths[i] = m.AverageQueueLength;
			operationUtilizations[i] = m.OperationRoomUtilization;
			
			Simulation.reset(); // Scheduler needs to be reset for the next simulation
			
			System.out.println("---");
		}
		
		System.out.println();
		System.out.println("Simulation over. " + n + " Rounds were completed.");

		// Switch to using this if library problems are solved
		/*double mean = MyMath.Mean(blockedTime);
		double confidence = 0.95;
		double CI = MyMath.ConfidenceInterval(blockedTime, confidence);
		double lower = mean - CI;
		double upper = mean + CI;
		System.out.println("The mean time for the opreation room being blocked in this setup is: "
				+ String.format("%.4f", mean));
		System.out.println("The "+ confidence +" confidence interval for this is " + String.format("%.4f", lower) + " - " + String.format("%.4f", upper));*/
		
		double operationRoomUtilization = MyMath.Mean(operationUtilizations);
		System.out.println("The mean operation room utilization: " + String.format("%.4f", operationRoomUtilization));
		
		// Statistics about operation room blocked time
		double blockedMean = MyMath.Mean(blockedTimes);
		double blockedCI = MyMath.ConfidenceIntervalBasic(blockedTimes, critVal);
		double lowerBlocked = blockedMean - blockedCI;
		double upperBlocked = blockedMean + blockedCI;
		System.out.println("Operation room blocked mean: " + String.format("%.4f", blockedMean) + ", standard deviation: " + String.format("%.4f", MyMath.StandardDeviation(blockedTimes)));
		System.out.println("The 95% confidence interval for operation room block time: " + String.format("%.4f", lowerBlocked) + " - " + String.format("%.4f", upperBlocked));
		
		// Statistics about preparation queue length
		double queueLengthMean = MyMath.Mean(queueLengths);
		double queueLengthCI = MyMath.ConfidenceIntervalBasic(queueLengths, critVal);
		double lowerQueueLength = queueLengthMean - queueLengthCI;
		double upperQueueLength = queueLengthMean + queueLengthCI;
		System.out.println("Preparation queue length mean: " + String.format("%.4f", queueLengthMean) + ", standard deviation: " + String.format("%.4f", MyMath.StandardDeviation(queueLengths)));
		System.out.println("The 95% confidence interval for preparation queue length: " + String.format("%.4f", lowerQueueLength) + " - " + String.format("%.4f", upperQueueLength));

		// T-test statistics to see if this run's results were different from last run's
		if (lastBlockedTimes.length > 0) {
			double tValueBlockedTimes = MyMath.PairwiseComparison(blockedTimes, lastBlockedTimes);
			System.out.println("T-test value for difference between this run's and last run's operation blocked time: " + tValueBlockedTimes);
		}
		if (lastQueueLengths.length > 0) {
			double tValueQueueLengths = MyMath.PairwiseComparison(queueLengths, lastQueueLengths);
			System.out.println("T-test value for difference between this run's and last run's queue length: " + tValueQueueLengths);
		}
		
		System.out.print("Round ");
		
		for (int i = 0; i < SurgeryUnit.NUMBER_OF_PARTITIONS; i++) {
			System.out.print(String.format("| part %02d ", i + 1));
		}
		
		System.out.println();
		
		for (int i = 0; i < SurgeryUnit.averagePartitionQLengths.size(); i++) {
			ArrayList<Double> partition = SurgeryUnit.averagePartitionQLengths.get(i);
			
			System.out.print(String.format("   %02d ", i + 1));
			
			for (int j = 0; j < partition.size(); j++) {
				System.out.print(String.format("| %05.2f   ", partition.get(j)));
			}
			System.out.println();
		}

		System.exit(0);
	}
	
	// Read list of numbers from a file to an array
	public static Double[] readLastResults(String fileName) {
		ArrayList<Double> resultList = new ArrayList<Double>();
		
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		        resultList.add(Double.parseDouble(text));
		    }
		    reader.close();
		} catch (IOException e) {}
		file.delete();
		return resultList.toArray(new Double[0]);
	}

}
