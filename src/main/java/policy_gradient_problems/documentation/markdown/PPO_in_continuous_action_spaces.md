# PPO in continuous action spaces

## Probability distribution function
Defines how likely action a is given mean μ and standard deviation σ

![PDF.png](..%2Fpics%2FPDF.png)

## Probability ratio
In the context of Proximal Policy Optimization (PPO) and specifically when dealing with continuous actions where 
the actor outputs a mean (μ) and standard deviation (σ) for the action's probability distribution, the probability 
ratio (probRatio) is  calculated using the quotient of the new probability density function (PDF) and the old PDF 
for the given action.

    probRatio=PDFnew/PDFold

Thus, the probability ratio compares how likely the action a is under the new policy to how likely it was under 
the old policy.

## Intuition 
In the Proximal Policy Optimization (PPO) framework for continuous action spaces, when you have a positive advantage 
(adv) for a given action a, it indicates that the action performed better than what the policy expected on average. 
Therefore, to improve the policy based on this observation, you'd want to adjust the network so that the mean (μ) of 
the action distribution moves closer to a, and the standard deviation (σ) decreases to make the policy more confident 
in taking similar actions in the future.


When dealing with a negative advantage (adv) in a Proximal Policy Optimization (PPO) setting, especially for a 
continuous action space where the action is determined by parameters like mean (μ) and standard deviation (σ)
of a normal distribution, the implication is that the chosen action a performed worse than what the policy's 
baseline expectation was for that state. In such a scenario, the PPO algorithm will adjust the policy in a way that:
* The mean (μ) could remain relatively unchanged.
* The standard deviation (σ) is likely to increase, indicating a higher degree of exploration around the mean. This adjustment makes intuitive sense because a negative advantage suggests that the current strategy (or action selection in that region of the action space) may not be optimal, and the algorithm should explore more broadly to identify potentially better strategies