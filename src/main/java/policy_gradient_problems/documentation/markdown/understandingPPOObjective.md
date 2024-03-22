# Understanding PPO objective


The objective function of PPO takes the minimum value between the original value and the clipped value. Positive advantage
function indicates the action taken by the agent is good. On the other hand, a negative advantage indicated bad action. 
For PPO, in both cases, the clipped operation makes sure it won’t deviate largely by clipping the update in the range.

![ppo_objective.png](..%2Fpics%2Fppo_objective.png)

Let's consider a bandit problem, two actions. Softmax with two thetas, theta1=-theta0.
Large theta0

Below is optimal theta0 for different wps and A0. In all cases theta0old=theta1old=0.
It is clear that eps restricts the new theta0.
![ppoex_epsIi1.png](..%2Fpics%2Fppoex_epsIi1.png)

![ppoex_epsIsDot2.png](..%2Fpics%2Fppoex_epsIsDot2.png)

![ppoex_a0IsNegEpsIs1.png](..%2Fpics%2Fppoex_a0IsNegEpsIs1.png)

![ppoex_a0IsNegEpsIsDot2.png](..%2Fpics%2Fppoex_a0IsNegEpsIsDot2.png)
