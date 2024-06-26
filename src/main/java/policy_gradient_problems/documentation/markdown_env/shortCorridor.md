# Short corridor

Following defines the environment:
* There are three terminal states. These have either a +1 or -1 reward.
* There are 7 “real world” states. 
* There are four states that the agent can observe. Observed state 1 maps to two different real world state.
* There are two actions (0 and 1), moving left or right.
* At every step in a trial there is 1% chance of being moved to a -1 terminal state. This is needed to avoid a potential policy giving an infinite loop.

![short_corridor_states_rewards.png](..%2Fpics%2Fshort_corridor_states_rewards.png)

So the agent has one of three possible observed non-terminal states (0,1,2) to decide an action (0,1) from.

The transition model is
* Move to a terminal state with a specific probability, 0.01%.
* Otherwise, let the next real world state be as defined below

       state <- state + dx        
       dx= | -1 (action==0)    
           | 1  (action==1)


The policy is defined from 6 parameters, named theta_i. For a specific observed state, two of these thetas are used for action selection.
Two thetas due to two actions. For the two selected actions the action probabilites are calculated with:

![softmax.png](..%2Fpics%2Fsoftmax.png)

### Reinforce vanilla
  
The problem is firs solved with REINFORCE Vanilla training algorithm, see 
[pseudocode](pseudocode.md).

The gradient of the log policy, gradLog, is defined below. In the relation i corresponds to the index of the "chosen" action (included in a <s,a,r> tuple) and
j corresponds to theta index. The term si is the present action probability of action j.

![grad_log_relation.png](..%2Fpics%2Fgrad_log_relation.png)

So gradLog presents the change in thetas representing the action probabilities in a specific state. For example means gradLog=[0.1,-0.1], increases theta of action 0 and decreases it for action 1.
Thetas for actions in other states are/should not be changed.

Result plots are given below. The optimal policy is to take a random action in observed state 1.
![siimVanillaCorridor.png](..%2Fpics%2FsiimVanillaCorridor.png)

### Reinforce with Baseline
The corresponding code is [pseudocode](../markdown/pseudocode_pgrl.md)

### One step Actor-Critic

One-step actor–critic methods replace the full return of REINFORCE with the one-step return, instead we use a learned state-value function.
REINFORCE with baseline is unbiased and will converge asymptotically to a local minimum, but like all Monte Carlo methods
it tends to learn slowly, produce estimates of high variance. With temporal-difference methods we can eliminate these inconveniences.

The is very similar to the base line case, but method for delta calculation is changed as follows
[pseudocode](../markdown/pseudocode_pgrl.md).
The code, applies a bootstrapping critic. Simulations plots below, especially in state 1 is the lower variance, compared to Vanilla REINFORCE, obvious.

![simACCorridor.png](..%2Fpics%2FsimACCorridor.png)