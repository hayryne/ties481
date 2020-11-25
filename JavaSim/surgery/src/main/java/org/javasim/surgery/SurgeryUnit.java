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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.javasim.Semaphore;
import org.javasim.Simulation;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;
import org.javasim.streams.UniformStream;

public class SurgeryUnit extends SimulationProcess
{
	// file names used to store old simulation results
	public static final String BLOCK_RESULTS_FILE = "last_block_results.txt";
	public static final String QUEUE_RESULTS_FILE = "last_queue_results.txt";
	
	// static constants for simulation process times are defined here
	// these are used to create ExponentialStreams with the constant as the mean value
	private static final int INTERARRIVAL_TIME = 25;
	private static final int PREPARATION_TIME = 40;
	private static final int OPERATION_TIME = 20;
	private static final int RECOVERY_TIME = 40;
	
	// constants for numbers of services are defined here
	private static final int PREPARATION_ROOMS = 3;
	private static final int OPERATION_ROOMS = 1;
	private static final int RECOVERY_ROOMS = 4;
		
	public static int NUMBER_OF_PATIENTS;	
	public static int NUMBER_OF_PARTITIONS = 10;
	
	public static double COMPLICATION_PROBABILITY = 0;
	
    public SurgeryUnit(double complication_probability, int patients)
    {
    	NUMBER_OF_PATIENTS = patients;
    	
    	InterArrivalTime = new UniformStream(INTERARRIVAL_TIME - 5, INTERARRIVAL_TIME + 5);
    	PreparationTime = new UniformStream(PREPARATION_TIME - 10, PREPARATION_TIME + 10);
    	OperationTime = new ExponentialStream(OPERATION_TIME);
    	RecoveryTime = new UniformStream(RECOVERY_TIME - 10, RECOVERY_TIME + 10);
    	
    	complicationRNG = new UniformStream(0,1);
    	
    	PreparationSemaphore = new Semaphore(PREPARATION_ROOMS);
    	OperationSemaphore = new Semaphore(OPERATION_ROOMS);
    	RecoverySemaphore = new Semaphore(RECOVERY_ROOMS);
    	
    	PreparationQLengths = new ArrayList<Double>();
    	OperationQLengths = new ArrayList<Double>();
    	RecoveryQLengths = new ArrayList<Double>();
    	
    	partitionQLengths = new ArrayList<ArrayList<Double>>(NUMBER_OF_PARTITIONS);
    	
    	for (int i = 0; i < NUMBER_OF_PARTITIONS; i++) {
    		partitionQLengths.add(new ArrayList<Double>());
    	}
    	
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

            while (SurgeryUnit.ProcessedJobs < NUMBER_OF_PATIENTS)
            	hold(1000);
            
            AverageQueueLength = listAvg(PreparationQLengths);
            OperationRoomBlockingProbability = OperationRoomBlocking / TotalJobs;
            OperationRoomUtilization = OperationRoomActiveTime / currentTime() / OPERATION_ROOMS;
            
            saveSimulationResult(AverageQueueLength, QUEUE_RESULTS_FILE);
            saveSimulationResult(OperationRoomBlockingProbability, BLOCK_RESULTS_FILE);
            
            System.out.println("Total number of jobs initiated: " + TotalJobs);
	        System.out.println("Total number of jobs processed: " + ProcessedJobs);
	        
	        System.out.println("Total response time: " + TotalResponseTime);
	        System.out.println("Average response time: " + TotalResponseTime / ProcessedJobs);

	        System.out.println("Preparation room utilization: " + PreparationRoomActiveTime / currentTime() / PREPARATION_ROOMS);
	        System.out.println("Operation room utilization: " + OperationRoomUtilization);
	        System.out.println("Recovery room utilization: " + RecoveryRoomActiveTime / currentTime() / RECOVERY_ROOMS);
	        
	        System.out.println("Average preparation queue length: " + AverageQueueLength);

	        System.out.println("Probability that operation room is blocking: " + OperationRoomBlockingProbability);
	        
	        if (COMPLICATION_PROBABILITY > 0) System.out.println("Number of complications that doubled the operation time: " + Complications);
	        else System.out.println("Complications were not turned on for the simulation");

	        
	        ArrayList<Double> lengths = new ArrayList<Double>();
	        
	        for (int i = 0; i < partitionQLengths.size(); i++) {
	        	lengths.add(listAvg(partitionQLengths.get(i)));
	        }
	        
	        averagePartitionQLengths.add(lengths);
	        
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
    
    // Write a simulation result (number) to file for later use
	private static void saveSimulationResult(double number, String fileName) {
		try {
		      FileWriter fw = new FileWriter(fileName, true);
		      fw.write(String.valueOf(number) + "\n");
		      fw.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
    
    public static UniformStream InterArrivalTime;
    public static UniformStream PreparationTime;
    public static ExponentialStream OperationTime;
    public static UniformStream RecoveryTime;
    
    public static UniformStream complicationRNG;
    
    public static long TotalJobs = 0;
    public static long ProcessedJobs = 0;

    public static double TotalResponseTime = 0.0;
    
    public static double PreparationRoomActiveTime = 0.0;
	public static double OperationRoomActiveTime = 0.0;
	public static double RecoveryRoomActiveTime = 0.0;
	
	public static double OperationRoomBlocking = 0;

	public static double AverageQueueLength = 0;
	public static double OperationRoomBlockingProbability = 0;
	public static double OperationRoomUtilization = 0;
	
    public static ArrayList<Double> PreparationQLengths = new ArrayList<Double>();
    public static ArrayList<Double> OperationQLengths = new ArrayList<Double>();
    public static ArrayList<Double> RecoveryQLengths = new ArrayList<Double>();
    
    public static ArrayList<ArrayList<Double>> partitionQLengths;
    public static ArrayList<ArrayList<Double>> averagePartitionQLengths = new ArrayList<ArrayList<Double>>();
	
	public static Semaphore PreparationSemaphore = new Semaphore(PREPARATION_ROOMS);
	public static Semaphore OperationSemaphore = new Semaphore(OPERATION_ROOMS);
	public static Semaphore RecoverySemaphore = new Semaphore(RECOVERY_ROOMS);

	public static int Complications = 0;
}
