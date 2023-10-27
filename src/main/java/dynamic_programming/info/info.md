# Dynamic programming solver

The core is 
![img.png](img.png)

Nodes are circles in the figure below and edges are lines in between. 
Some edges are excluded for clarity. Every node is defined by x and y.
So the left lower node has x=y=0. And the right upper node has x=3, y=1.
Only one node can be start and end.
Every edge is mapped to a transition cost, also named reward. For example
In the figure below is the reward beteen node (0,0) and (1,1) 3.

![img_3.png](img_3.png)


The pseudocode for "training" the value function (V) is given below
![img_4.png](img_4.png)

where V(x,y) is the value of the node in position (x,y). V(3,0) is zero and V(2,0) is 3.


A class diagram is shown below. The main domain objects are EdgeDP, NodeDP, DirectedGraphDP and 
ValueMemoryDP. MemoryTrainerDP is executing the pseudocode above.
Both MemoryTrainerDP and OptimalPathFinderDP uses the helper class ActionSelectorDP. 

![img_5.png](img_5.png)