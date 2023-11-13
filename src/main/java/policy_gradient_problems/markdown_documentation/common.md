# Policy gradient methods

Policy gradient methods does not use value function to choose action, there is an explicit action selection function.


![pg_types.png](pics%2Fpg_types.png)


The update of policy function is expressed by relation below. The gradient of the probability of taking the action actually taken divided by the probability of taking that action.
Intuition: Gt is large, pi is small -> change theta so probability of action again increases. Do more of what is good.


![pg_theorem.png](pics%2Fpg_theorem.png)


Pros and cons are given below

![pros_cons.png](pics%2Fpros_cons.png)

Applying relation above, with log trick (GRAD log(x)=GRADx/x), gives REINFORCE. The most basic PG algorithm.


![reinforce.png](pics%2Freinforce.png)

Soft max is a common PG policy. See below.


![softmax.png](pics%2Fsoftmax.png)