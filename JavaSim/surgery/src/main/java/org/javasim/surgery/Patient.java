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

import org.javasim.Scheduler;
import org.javasim.SimulationEntity;

public class Patient extends SimulationEntity {
	public Patient() {
		try {
			this.PreparationTime = SurgeryUnit.PreparationTime.getNumber();
			
			this.OperationTime = SurgeryUnit.OperationTime.getNumber();
			
			if(SurgeryUnit.complicationRNG.getNumber() < SurgeryUnit.COMPLICATION_PROBABILITY) {
				this.OperationTime *= 2.0;
				SurgeryUnit.Complications++;
			}
			
			this.RecoveryTime = SurgeryUnit.RecoveryTime.getNumber();
			
			this.activate();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void run() {
		try {
			SurgeryUnit.PreparationQLengths.add((double)SurgeryUnit.PreparationSemaphore.numberWaiting());
			SurgeryUnit.PreparationSemaphore.get(this);
			hold(this.PreparationTime);
			SurgeryUnit.PreparationRoomActiveTime += this.PreparationTime;
			
			SurgeryUnit.OperationQLengths.add((double)SurgeryUnit.OperationSemaphore.numberWaiting());
			SurgeryUnit.OperationSemaphore.get(this);
			
			// preparation room is released once the patient actually leaves it
			SurgeryUnit.PreparationSemaphore.release();
			
			hold(this.OperationTime);
			SurgeryUnit.OperationRoomActiveTime += this.OperationTime;
			
			long recoveryQueue = SurgeryUnit.RecoverySemaphore.numberWaiting();
			SurgeryUnit.RecoveryQLengths.add((double)recoveryQueue);
			
			if (recoveryQueue > 0) SurgeryUnit.OperationRoomBlocking++;
			
			SurgeryUnit.RecoverySemaphore.get(this);
			
			// operation room is released once the patient actually leaves it
			SurgeryUnit.OperationSemaphore.release();
			
			hold(this.RecoveryTime);
			SurgeryUnit.RecoveryRoomActiveTime += this.RecoveryTime;
			SurgeryUnit.RecoverySemaphore.release();
			
			this.terminate();
			
			ResponseTime = Scheduler.currentTime() - ArrivalTime;
			SurgeryUnit.TotalResponseTime += ResponseTime;
			
			SurgeryUnit.ProcessedJobs++;
		} catch (Exception e) {}	
	}

	private double ResponseTime;

	private double ArrivalTime;

	public double PreparationTime = 0.0;
	public double OperationTime = 0.0;
	public double RecoveryTime = 0.0;
}
