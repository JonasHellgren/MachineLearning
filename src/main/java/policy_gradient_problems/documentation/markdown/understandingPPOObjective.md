# Understanding PPO objective


The objective function of PPO takes the minimum value between the original value and the clipped value. Positive advantage
function indicates the action taken by the agent is good. On the other hand, a negative advantage indicated bad action. 
For PPO, in both cases, the clipped operation makes sure it won’t deviate largely by clipping the update in the range.

![ppo_objective.png](..%2Fpics%2Fppo_objective.png)

Let's consider a bandit problem, two actions. The uppper plot is for large eps (1) and the lower for smaller (1e-2).
Clipping, small eps, gives smaller changes in policy.


![largeEpsBandit.png](..%2Fpics%2FlargeEpsBandit.png)

![smallEpsBandit.png](..%2Fpics%2FsmallEpsBandit.png)