# Tunnels

The road maze environment reflects driving on a road with multiple splits.
There are three terminal states, with specific reward.
The challenge is to develop a policy that navigates to the most rewarding terminal state.

## States


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