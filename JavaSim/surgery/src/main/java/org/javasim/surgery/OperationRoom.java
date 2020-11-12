package org.javasim.surgery;

import java.io.IOException;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

public class OperationRoom extends SimulationProcess
{
    public OperationRoom(double mean)
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

            while (!SurgeryUnit.OperationQ.isEmpty())
            {
                ActiveStart = currentTime();

                patient = SurgeryUnit.OperationQ.dequeue();
                
                // queue client for operation
                SurgeryUnit.OperationQ.enqueue(patient);

                try
                {
                    hold(STime.getNumber());
                }
                catch (Exception e) {}

                ActiveEnd = currentTime();
                SurgeryUnit.OperationRoomActiveTime += ActiveEnd - ActiveStart;

                boolean emptyRec = false;
                emptyRec = SurgeryUnit.RecoveryQ.isEmpty();
                
                SurgeryUnit.RecoveryQ.enqueue(patient);
        		
                try
                {
                	if (emptyRec && !SurgeryUnit.Rec.processing()) {
            			SurgeryUnit.Rec.activate();
            		}
                }
                catch (Exception e) {}
               
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

    private ExponentialStream STime;

    private boolean working;

    private Patient patient;
}
