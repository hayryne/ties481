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

import java.util.ArrayList;

import org.javasim.RestartException;
import org.javasim.Scheduler;
import org.javasim.Simulation;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;

public class SurgeryUnit extends SimulationProcess
{
	// static constants for simulation process times are defined here
	// these are used to create ExponentialStreams with the constant as the mean value
	
	private static final int INTERARRIVAL_TIME = 25;
	private static final int PREPARATION_TIME = 40;
	private static final int OPERATION_TIME = 20;
	private static final int RECOVERY_TIME = 40;
	
    public SurgeryUnit()
    {
        TotalResponseTime = 0.0;
        TotalJobs = 0;
        ProcessedJobs = 0;
        PreparationRoomActiveTime = 0.0;
        OperationRoomActiveTime = 0.0;
        RecoveryRoomActiveTime = 0.0;
    }

    public void run ()
    {
        try
        {
            Arrivals A = new Arrivals(INTERARRIVAL_TIME);
            SurgeryUnit.Prep = new PreparationRoom(PREPARATION_TIME);
            SurgeryUnit.Op = new OperationRoom(OPERATION_TIME);
            SurgeryUnit.Rec = new RecoveryRoom(RECOVERY_TIME);
            
            Patient J = new Patient();

            A.activate();

            Simulation.start();

            while (SurgeryUnit.ProcessedJobs < 1000)
                hold(1000);

            System.out.println("Current time " + currentTime());
            System.out.println("Total number of jobs present " + TotalJobs);
            System.out.println("Total number of jobs processed " + ProcessedJobs);
            
            System.out.println("Total response time of " + TotalResponseTime);
            System.out.println("Average response time = "
                    + (TotalResponseTime / ProcessedJobs));
            
            System.out.println("Probability that preparation room is working = "
                            + (PreparationRoomActiveTime / currentTime()));
            System.out.println("Probability that operation room is working = "
                    + (OperationRoomActiveTime / currentTime()));
            System.out.println("Probability that recovery room is working = "
                    + (RecoveryRoomActiveTime / currentTime()));
            
            System.out.println("Average patient preparation time: "
            		+ listAvg(SurgeryUnit.Prep.preparationTimes));
            
            System.out.println("Average patient preparation time: "
            		+ listAvg(SurgeryUnit.Op.operationTimes));
            
            System.out.println("Average patient preparation time: "
            		+ listAvg(SurgeryUnit.Rec.recoveryTimes));

            Simulation.stop();

            A.terminate();
            SurgeryUnit.Prep.terminate();
            SurgeryUnit.Op.terminate();

            SimulationProcess.mainResume();
        }
        catch (SimulationException e)
        {
        }
        catch (RestartException e)
        {
        }
    }

    public void await ()
    {
        this.resumeProcess();
        SimulationProcess.mainSuspend();
    }
    
    private static double listAvg(ArrayList<Double> list)
    {
    	return list.stream().mapToDouble(a->a).average().getAsDouble();
    }

    public static PreparationRoom Prep;
    public static OperationRoom Op;
    public static RecoveryRoom Rec;
    
    public static Queue PreparationQ = new Queue();
    public static Queue OperationQ = new Queue();
    public static Queue RecoveryQ = new Queue();
    
    public static double TotalResponseTime = 0.0;

    public static long TotalJobs = 0;

    public static long ProcessedJobs = 0;

    public static double PreparationRoomActiveTime = 0.0;
    public static double OperationRoomActiveTime = 0.0;
    public static double RecoveryRoomActiveTime = 0.0;
}
