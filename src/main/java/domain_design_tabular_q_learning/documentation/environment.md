# Road maze environment

The road maze environment reflects driving on a road with two lanes.
In the upper lane there is an obstacle at position (x,y)=(3,1).
The challenge is to develop a policy to avoid hitting the obstacle.

## States

The x-position can vary between 0 and 3, there 3 represents a terminal state.
There are two different y-positions, 0 and 1. In the figure, T represent terminal state.
TF stands for the state that is both terminal and fail.

![roadMazeEnv.png](pics%2FroadMazeEnv.png)


## Actions

action space = {N,E,S}

## Reward model

The reward model is formulated in such a way that entering position (x,y)=(3,1) is heavily penalized.
It is also penalized to select a move up or down action.

    r=  |-100 (x,y)=(3,1)
        |-1 (action is not E)
        |0  (else)

## Transition model
The basic idea with the transition model is that every step results in an x-position increase.
The new y-position depends on the selected action, but is bounded to [0,1].
The consequence of the bound is that an N action in y-position 1 will give no change in y-position. 

    sNext:  x <- x+1
            y <- clip(0,1,yNext)

    yNext = | y+1 (a=N) 
            | y-1 (a=S)
            | y (E)

## Training evolution

![trainingsumRewards.png](pics%2FtrainingsumRewards.png)

![trainingtdError.png](pics%2FtrainingtdError.png)

## Charts of trained agent

The policy is to always move E unless in state (x,y)=(2,1). In this state the best action is to move S.
The motivation is to avoid the poor reward fail state (x,y)=(3,1).

![agentState action values.png](pics%2FagentState%20action%20values.png)

![agentState values.png](pics%2FagentState%20values.png)

![agentActions.png](pics%2FagentActions.png)