# Assignment 4 - Regression Model

Parameters of the model
-------------------------
interarrival time = x1

preparation time = x2

rec time = x3

prep units = x4

rec units = x5

complications (twist) = x6

---
Exponential Distribution = 0, Uniform Distribution = 1

4 Rooms = 0, 5 Rooms = 1

Complications Off = 0, Complications On = 1

So exponential distribution is always 0, 4 rooms is also 0 and complications off is 0. 
Other possibilility of the parameter is 1.

2^6-3 experiment design
-------------------------

x1 |x2 |x3 |x4=x1*x2|x5=x1*x3|x6=x2*x3|
---|---|---|--------|--------|--------|
\+  |+  |+  |	+   |     +  |     +  |
\+  |+  |-  |	+   |     -  |     -  |
\+  |-  |+  |	-   |     +  |     -  |
\-  |+  |+  |	-   |     -  |     +  |
\-  |-  |+  |	+   |     -  |     -  |
\-  |+  |-  |	-   |     +  |     -  |
\+  |-  |-  |	-   |     -  |     +  |
\-  |-  |-  |	+   |     +  |     +  |

So 8 different configurations chosen were:

1 1 1 1 1 1
 
1 1 0 1 0 0 

1 0 1 0 1 0 

0 1 1 0 0 1 

0 0 1 1 0 0 

0 1 0 0 1 0 

1 0 0 0 0 1 

0 0 0 1 1 1    

Results
---------

| configuration      | Queue length |
| -------------------|--------------|
| 1 1 1 1 1 1        | 0,2466       |
| 1 1 0 1 0 0        | 0,2997       |
| 1 0 1 0 1 0        | 0,5907       |
| 0 1 1 0 0 1        | 2,6992       |
| 0 0 1 1 0 0        | 2,1986       |
| 0 1 0 0 1 0        | 2,6423       |
| 1 0 0 0 0 1        | 0,6916       |
| 0 0 0 1 1 1        | 1,9528       |


Regression model
-----------------

Regression model calculated from results with R:

Queue length = 2.61 - 1.916\*x1 + 0.114\*x2 + 0.037\*x3 - 0.481\*x4 - 0.114\*x5 -0.035\*x6


Result analysis
-------------

| configuration      | Queue length | Model expectation  |
| -------------------|--------------|--------------------|
| 1 1 1 1 1 1        | 0,2466       | 0,215              |
| 1 1 0 1 0 0        | 0,2997       | 0,327              |
| 1 0 1 0 1 0        | 0,5907       | 0,617              |
| 0 1 1 0 0 1        | 2,6992       | 2,726              |
| 0 0 1 1 0 0        | 2,1986       | 2,166              |
| 0 1 0 0 1 0        | 2,6423       | 2,610              |
| 1 0 0 0 0 1        | 0,6916       | 0,659              |
| 0 0 0 1 1 1        | 1,9528       | 1,980              |

Here results seem to be quite accurate.

Some different configurations for validation of model:

| configuration      | Queue length | Model expectation  |
| -------------------|--------------|--------------------|
| 0 0 0 0 0 0        | 3,3348       | 2,610              |
| 0 1 0 1 0 1        | 2,3199       | 2,208              |
| 1 1 0 0 0 1        | 0,5012       | 0,773              |

Here we can see larger differences between model expectation and actual value from simulation

Conclusions
------------

Queue length seems to depend mostly on distribution of patient arrival times and preparation room
capacity. 
However there are quite big differences in queue length and model expectation in validation
configurations so model is not very accurate. 
Joint effects would have to be taken into consideration for a better model.