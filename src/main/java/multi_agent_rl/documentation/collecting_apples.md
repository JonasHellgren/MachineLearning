# Collecting apples

A 5x5 grid where each cell can be empty, contain an apple, or be occupied by an agent. One cell contains an apple 
that the agents need to collect. The apple is collected only if the agents are on opposite sides of the apple. 
One can imagine a very heavy apple needing joint lifting. 
The positions of the apple is randomly assigned at the beginning of each episode.
An episode terminates when the apple is collected.

![apple_grid.png](pics%2Fapple_grid.png)


## Actions

each agent has following actions: [N,E,S,W,STOP]

STOP means do nothing

## Reward
The reward is based on the number of apples collected in each timestep. For example:

* +10 for each apple collected
* -1 if agents are at same position
* -0.1 if no apple is collected.

To satisfy apple is collected only if the agents are on opposite sides of the apple, it is required that the apple 
is on the midpoint between the agents' positions. 

## Global State Representation:

Positions of all agents.
Position of apple.

## Local State Representation

Distance in x and y (dx,dy) to other agent and apple.


## Function approximator

To speed up computation, neural networks has not been used a function approximator for the memories, instead....


