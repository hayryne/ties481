# Assignment 3 - Simulation Results

Basic results
--------------

Results from running simulation with 3 different configurations
and monitoring operation room blocking ratio and preparation queue length.

No twist enabled.

### Preparation rooms: 3 - Recovery rooms: 4

|                    | Mean      | Standard Deviation |Confidence Interval|
| -------------------|-----------|--------------------|-------------------|
| Operation Blocking | 0.0333    |0.0179              |0.0249 - 0.0416    |
| Queue Length       | 0.3224    |0.0354              |0.3058 - 0.3390    |

### Preparation rooms: 3 - Recovery rooms: 5

|                    | Mean      | Standard Deviation |Confidence Interval|
| -------------------|-----------|--------------------|-------------------|
| Operation Blocking | 0.0074    |0.0071              |0.0041 - 0.0106    |
| Queue Length       | 0.3224    |0.0354              |0.3058 - 0.3390    |

### Preparation rooms: 4 - Recovery rooms: 5

|                    | Mean      | Standard Deviation |Confidence Interval|
| -------------------|-----------|--------------------|-------------------|
| Operation Blocking | 0.0068    |0.0068              |0.0037 - 0.0100    |
| Queue Length       | 0.0549    |0.0069              |0.0517 - 0.0582    |

In configuration 1 there is a statistically significant difference in
operation blocking compared to configuration 2 and configuration 3.
Operation blocking difference between configuration 2 and 3 is not statistically
significant (confidence intervals overlaps).

Configuration 3 has a statistically significant difference in queue length
compared to other two configurations. Configurations 1 and 2 have no difference
in queue length.

Additional twist
------------------------
In additional twist there is 20% chance for complications during operation
which will cause the operation to last longer. The goal was to change patient
arrival interval so that operation room would be utilized the same amount with
complication as it is without them. Configuration with 3 preparation rooms and 
4 recovery rooms was used here.

### Without complications:

**patient arrival parameter = 25**

**Operation Room Utilization = 0.789**

|                    | Mean      | Standard Deviation |Confidence Interval|
| -------------------|-----------|--------------------|-------------------|
| Operation Blocking | 0.0333    |0.0179              |0.0249 - 0.0416    |
| Queue Length       | 0.3224    |0.0354              |0.3058 - 0.3390    |


### With complications
**patient arrival parameter = 30**

**Operation Room Utilization = 0.790** (almost the same as above)

|                    | Mean      | Standard Deviation |Confidence Interval|
| -------------------|-----------|--------------------|-------------------|
| Operation Blocking | 0.0212    |0.0112              |0.0159 - 0.0264    |
| Queue Length       | 0.1440    |0.0154              |0.1368 - 0.1512    |


With complications enabled, there must be longer interval between arrivals to
get the same utilization of operation room. In this case queue length is
clearly shorter. Difference with operation blocking is not statistically
significant however.
    
    
T-test for comparing configurations pairwise
--------------------

### T-values for operation blocking difference

|                    | Conf 1    | Conf 2             | Conf 3            |
| -------------------|-----------|--------------------|-------------------|
| Conf 1             |           |-9.731              |-9.834             |
| Conf 2             | 9.731     |                    |-0.809             |
| Conf 3             | 9.834     |0.809               |                   |

Similar results as in earlier comparison. Configuration 1 has significant
difference to others and difference between 2 and 3 is not significant.

### T-values for queue length difference

|                    | Conf 1    | Conf 2             | Conf 3            |
| -------------------|-----------|--------------------|-------------------|
| Conf 1             |           |NaN                 |-41.059            |
| Conf 2             | NaN       |                    |-41.059            |
| Conf 3             | 41.059    |41.059              |                   |

Also same results as earlier. Configuration 3 has significant
difference to others and difference between 1 and 2 is not significant 
(exactly the same so value is not defined).

Example print of simulation program
------------------------------------

**Run with 3 preparation rooms and 4 recovery rooms. T-test values depends on the
configuration that was used in the simulation run before.**


`Simulation over. 20 Rounds were completed.`

`The mean operation room utilization: 0,7895`

`Operation room blocked mean: 0,0068, standard deviation: 0,0068`

`The 95% confidence interval for operation room block time: 0,0037 - 0,0100`

`Preparation queue length mean: 0,0549, standard deviation: 0,0069`

`The 95% confidence interval for preparation queue length: 0,0517 - 0,0582`

`T-test value for difference between this run's and last run's operation blocked
time: -0.8094272134003798`

`T-test value for difference between this run's and last run's queue length:
-41.05938756745182`