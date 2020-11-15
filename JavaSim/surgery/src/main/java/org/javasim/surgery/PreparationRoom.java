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

import java.io.IOException;
import java.util.ArrayList;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

public class PreparationRoom extends SimulationProcess {
	public PreparationRoom(double mean) {
		STime = new ExponentialStream(mean);
		working = false;
		patient = null;
	}

	public void run() {
		double ActiveStart, ActiveEnd;

		while (!terminated()) {
			working = true;

			while (!SurgeryUnit.PreparationQ.isEmpty()) {
				ActiveStart = currentTime();

				patient = SurgeryUnit.PreparationQ.dequeue();

				try {
					hold(STime.getNumber());
				} catch (Exception e) {
				}

				ActiveEnd = currentTime();
				SurgeryUnit.PreparationRoomActiveTime += ActiveEnd - ActiveStart;

				patient.PreparationTime = ActiveEnd - ActiveStart;

				preparationTimes.add(patient.PreparationTime);

				boolean emptyOp = false;
				emptyOp = SurgeryUnit.OperationQ.isEmpty();

				SurgeryUnit.OperationQLengths.add((double) SurgeryUnit.OperationQ.queueSize());

				SurgeryUnit.OperationQ.enqueue(patient);

				try {
					if (emptyOp && !SurgeryUnit.Op.processing()) {
						SurgeryUnit.Op.activate();
					}
				} catch (Exception e) {
				}
			}

			working = false;

			try {
				cancel();
			} catch (RestartException e) {
			}
		}
	}

	public boolean processing() {
		return working;
	}

	public ArrayList<Double> preparationTimes = new ArrayList<Double>();

	private ExponentialStream STime;

	private boolean working;

	private Patient patient;
}
