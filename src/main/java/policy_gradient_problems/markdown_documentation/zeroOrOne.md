
# ZeroOrOne


## Environment

reward =  \
rewardActionZero (action==0)\
rewardActionOne (action==1)

## Agent

action = \
1  (r<sigmoid(theta)\
0  (else)

sigmoid(theta)=1.0 / (1.0 + e^-theta)

sigmoid -> 1 (theta -> inf) \
sigmoid -> 0 (theta -> -inf)




## Understanding gradLogPolicy

The gradient of the policy can in this simple environment be derived analytically for each action

logPolicy=ln sigmoid(theta,a)

so for a=action=1

gradLogPolicy(a=1)= d/dTheta logPolicy =
d/dTheta ln ( 1.0 / (1.0 + e^theta) ) =  .... =
1-sigmoid(theta)

above is derived using following relations

f(x)=h(g(x)) -> f(x)'=h'(g(x))*g'(x) \
f(x)=x^(a)  -> f(x)'=a*x^(a-1)\
f(x)=ln(x) -> f(x)'=x'/x

in similar way gradLogPolicy(a=0)=-sigmoid(theta)


The core of the updating algorithm is

    agent.setTheta(agent.theta+learningRate*gradLog*vt);

gradLog above depends on the action only for this simple environment, vt is experience from
taking "randomly" sampled action according to present policy (theta dependant).

The intuition for gradLogPolicy(s(theta),a) is as follows:

positive -> increased theta gives higher probability choosing action in node s \
negative -> decreased theta gives higher probability choosing action in node s
<=>\
increased theta gives lower probability choosing action in node s

naming: gradLogPolicy=glp, vt=return at time t

glp is pos, vt is pos => theta increased because want to increase probability take good action \
glp is pos, vt is neg => theta decreased because want to decrease probability taking bad action \
glp is neg, vt is pos => theta decreased because decreased theta gives higher probability choosing action \
glp is neg, vt is neg => theta increased because increased theta gives lower probability choosing bad action 




