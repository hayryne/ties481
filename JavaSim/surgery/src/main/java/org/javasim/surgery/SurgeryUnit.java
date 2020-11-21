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

import org.javasim.Semaphore;
import org.javasim.Simulation;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
import org.javasim.streams.UniformStream;

public class SurgeryUnit extends SimulationProcess
{
	// static constants for simulation process times are defined here
	// these are used to create ExponentialStreams with the constant as the mean value
	private static final int INTERARRIVAL_TIME = 25;
	private static final int PREPARATION_TIME = 40;
	private static final int OPERATION_TIME = 20;
	private static final int RECOVERY_TIME = 40;
	
	// constants for numbers of services are defined here
	private static final int PREPARATION_ROOMS = 3;
	private static final int OPERATION_ROOMS = 1;
	private static final int RECOVERY_ROOMS = 3;
	
	private final int NUMBER_OF_PATIENTS = 1000;
	
	
	public static double COMPLICATION_PROBABILITY = 0;
	
    public SurgeryUnit(double complication_probability)
    {
    	InterArrivalTime = new ExponentialStream(INTERARRIVAL_TIME);
    	PreparationTime = new ExponentialStream(PREPARATION_TIME);
    	OperationTime = new ExponentialStream(OPERATION_TIME);
    	RecoveryTime = new ExponentialStream(RECOVERY_TIME);
    	
    	complicationRNG = new UniformStream(0,1);
    	
    	PreparationSemaphore = new Semaphore(PREPARATION_ROOMS);
    	OperationSemaphore = new Semaphore(OPERATION_ROOMS);
    	RecoverySemaphore = new Semaphore(RECOVERY_ROOMS);
    	Complications = 0;
    	OperationRoomBlocking = 0;
    	
        TotalResponseTime = 0.0;
        TotalJobs = 0;
        ProcessedJobs = 0;
        PreparationRoomActiveTime = 0.0;
        OperationRoomActiveTime = 0.0;
        RecoveryRoomActiveTime = 0.0;
        
        COMPLICATION_PROBABILITY = complication_probability;
    }

    public void run ()
    {
        try
        {
            Arrivals A = new Arrivals(NUMBER_OF_PATIENTS);      

            A.activate();

            Simulation.start();

            while (SurgeryUnit.ProcessedJobs < 1000)
            	hold(1000);
            
            System.out.println("Total number of jobs initiated: " + TotalJobs);
	        System.out.println("Total number of jobs processed: " + ProcessedJobs);
	        
	        System.out.println("Total response time: " + TotalResponseTime);
	        System.out.println("Average response time: " + TotalResponseTime / ProcessedJobs);

	        System.out.println("Preparation room utilization: " + PreparationRoomActiveTime / currentTime() / PREPARATION_ROOMS);
	        System.out.println("Operation room utilization: " + OperationRoomActiveTime / currentTime() / OPERATION_ROOMS);
	        System.out.println("Recovery room utilization: " + RecoveryRoomActiveTime / currentTime() / RECOVERY_ROOMS);
	        
	        System.out.println("Average preparation queue length: " + listAvg(PreparationQLengths));

	        System.out.println("Probability that operation room is blocking: " + OperationRoomBlocking / TotalJobs);
	        
	        if (COMPLICATION_PROBABILITY > 0) System.out.println("Number of complications that doubled the operation time: " + Complications);
	        else System.out.println("Complications were not turned of for the simulation");

            Simulation.stop();
            SimulationProcess.mainResume();
        } catch (Exception e) {}
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
        

    
    public static ExponentialStream InterArrivalTime;
    public static ExponentialStream PreparationTime;
    public static ExponentialStream OperationTime;
    public static ExponentialStream RecoveryTime;
    
    public static UniformStream complicationRNG;
    
    public static long TotalJobs = 0;
    public static long ProcessedJobs = 0;

    public static double TotalResponseTime = 0.0;
    
    public static double PreparationRoomActiveTime = 0.0;
	public static double OperationRoomActiveTime = 0.0;
	public static double RecoveryRoomActiveTime = 0.0;
	
	public static double OperationRoomBlocking = 0;
	
    public static ArrayList<Double> PreparationQLengths = new ArrayList<Double>();
    public static ArrayList<Double> OperationQLengths = new ArrayList<Double>();
    public static ArrayList<Double> RecoveryQLengths = new ArrayList<Double>();
	
	public static Semaphore PreparationSemaphore = new Semaphore(PREPARATION_ROOMS);
	public static Semaphore OperationSemaphore = new Semaphore(OPERATION_ROOMS);
	public static Semaphore RecoverySemaphore = new Semaphore(RECOVERY_ROOMS);

	public static int Complications = 0;
}
