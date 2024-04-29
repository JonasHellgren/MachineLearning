# Two armed bandit problem

There are two actions, action 0 is try arm 0, action 1 is try arm 1.  Each arm is assigned with a probability between 0 and 1 of winning a coin
The reward is one if an action leads to a coin
The policy pi= (p0,p1) = probability of taking each action, where p0+p1=1. The term se in the equation below is the sum of exp(theta_i) terms.

![bandit_policy.png](..%2Fpics%2Fbandit_policy.png)


The trainer code is given below. The gradLogVector, for a specific action, is from an 
analytic expression. The important update is
        
    agent.theta <- agent.theta+learningRateReplayBufferCritic*gradLog*vt

![bandit_trainercode.png](..%2Fpics%2Fbandit_trainercode.png)

Example policy evolution below

![bandit_evolution.png](..%2Fpics%2Fbandit_evolution.png)

A snippet of training log is given below. When reward is non zero, the thetas changes so future probability for 
taking that action increases. At the same time probability for other action decreases.  

![train_log_snippet.png](..%2Fpics%2Ftrain_log_snippet.png)