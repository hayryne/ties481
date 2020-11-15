package org.javasim.surgery;

import java.io.IOException;
import java.util.ArrayList;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
import org.javasim.streams.UniformStream;

public class OperationRoom extends SimulationProcess {
	public OperationRoom(double mean) {
		STime = new ExponentialStream(mean);
		complicationRNG = new UniformStream(0,1);
		working = false;
		patient = null;
	}

	public void run() {
		double ActiveStart, ActiveEnd;

		while (!terminated()) {
			working = true;

			while (!SurgeryUnit.OperationQ.isEmpty()) {
				ActiveStart = currentTime();

				patient = SurgeryUnit.OperationQ.dequeue();

				
				
				Double timeMultiplier = 1.0;
				
				try {
					if(complicationRNG.getNumber() < SurgeryUnit.ComplicationProbability) {    // 20% chance that the operation has a complication
						timeMultiplier = 2.0;
						SurgeryUnit.Complications++;
					}
				} catch (Exception e) {
				}
				

				
				try {
					hold(STime.getNumber() * timeMultiplier);
				} catch (Exception e) {
				}

				ActiveEnd = currentTime();
				SurgeryUnit.OperationRoomActiveTime += ActiveEnd - ActiveStart;

				patient.OperationTime = ActiveEnd - ActiveStart;

				operationTimes.add(patient.OperationTime);

				boolean emptyRec = false;
				emptyRec = SurgeryUnit.RecoveryQ.isEmpty();

				SurgeryUnit.RecoveryQLengths.add((double) SurgeryUnit.RecoveryQ.queueSize());

				SurgeryUnit.RecoveryQ.enqueue(patient);

				try {
					if (emptyRec) {
						for (RecoveryRoom Rec : SurgeryUnit.RecRooms) {
							if (!Rec.processing()) {
								Rec.activate();
							}
						}
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

	public ArrayList<Double> operationTimes = new ArrayList<Double>();

	private ExponentialStream STime;

	private boolean working;

	private Patient patient;
	
	private UniformStream complicationRNG;
	
	
}
