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
import java.util.Arrays;

import org.javasim.Simulation;
import org.javasim.SimulationException;

public class Main {
	public static void main(String[] args) throws SimulationException {

		int n = 20; // simulation rounds

		Double[] blockedTime = new Double[n];

		for (int i = 0; i < n; i++) {
			SurgeryUnit m = new SurgeryUnit(0.2); // Probability higher than 0 results in patient complications being
													// turned on
			m.await();
			blockedTime[i] = (m.OperationRoomBlocking / m.TotalJobs);
			Simulation.reset(); // Scheduler needs to be reset for the next simulation
			
			System.out.println("---");
		}
		
		System.out.println();
		System.out.println("Simulation over. " + n + " Rounds were completed.");

		double mean = MyMath.Mean(blockedTime);
		double confidence = 0.95;
		double CI = MyMath.ConfidenceInterval(blockedTime, confidence);
		double lower = mean - CI;
		double upper = mean + CI;
		System.out.println("The mean time for the opreation room being blocked in this setup is: "
				+ String.format("%.4f", mean));
		System.out.println("The "+ confidence +" confidence interval for this is " + String.format("%.4f", lower) + " - " + String.format("%.4f", upper));

		System.exit(0);
	}
}
