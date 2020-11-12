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

public class Patient
{
    public Patient()
    {
        boolean emptyPrep = false;

        ResponseTime = 0.0;
        ArrivalTime = Scheduler.currentTime();

        emptyPrep = SurgeryUnit.PreparationQ.isEmpty();
        
        SurgeryUnit.PreparationQ.enqueue(this);
        SurgeryUnit.TotalJobs++;
        
        try
        {
        	if (emptyPrep && !SurgeryUnit.Prep.processing()) {
        		SurgeryUnit.Prep.activate();
            }
        }
        catch (Exception e) {}
    }

    public void finished ()
    {
        ResponseTime = Scheduler.currentTime() - ArrivalTime;
        SurgeryUnit.TotalResponseTime += ResponseTime;
    }

    private double ResponseTime;

    private double ArrivalTime;
    
    public double PreparationTime = 0.0;
    public double OperationTime = 0.0;
    public double RecoveryTime = 0.0;
}
