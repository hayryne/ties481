package org.javasim.surgery;

import java.io.IOException;
import java.util.ArrayList;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

public class RecoveryRoom extends SimulationProcess
{
    public RecoveryRoom(double mean)
    {
        STime = new ExponentialStream(mean);
        working = false;
        patient = null;
    }

    public void run ()
    {
        double ActiveStart, ActiveEnd;

        while (!terminated())
        {
            working = true;

            while (!SurgeryUnit.RecoveryQ.isEmpty())
            {
                ActiveStart = currentTime();

                patient = SurgeryUnit.RecoveryQ.dequeue();

                try
                {
                    hold(STime.getNumber());
                }
                catch (Exception e) {}

                ActiveEnd = currentTime();
                SurgeryUnit.RecoveryRoomActiveTime += ActiveEnd - ActiveStart;
                
                patient.RecoveryTime = ActiveEnd - ActiveStart;
                
                recoveryTimes.add(patient.RecoveryTime);
                
                SurgeryUnit.ProcessedJobs++;

                /*
                 * Introduce this new method because we usually rely upon the
                 * destructor of the object to do the work in C++.
                 */

                patient.finished();
            }

            working = false;

            try
            {
                cancel();
            }
            catch (RestartException e)
            {
            }
        }
    }

    public boolean processing ()
    {
        return working;
    }

    public ArrayList<Double> recoveryTimes = new ArrayList<Double>(); 
    
    private ExponentialStream STime;

    private boolean working;

    private Patient patient;
}
