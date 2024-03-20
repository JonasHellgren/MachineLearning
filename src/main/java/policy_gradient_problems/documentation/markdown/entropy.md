# Entropy

In the following text P(xi) means probability for "case" xi. For example specific action in RL.
TP means true probability, i.e. desired reference value.

## Definitions
Higher entropy means lower chaos. It is slightly different in information theory. The mathematician Claude Shannon
introduced the entropy in information theory in 1948. Entropy increases with increases in uncertainty.

    E(P) = - ∑ P(xi) * log(P(xi))
can also be defined from surprise S, how unexpected an event is

    E(P) = ∑ P(xi) * S;  Surprise=S=log(1/P(xi))

Entropy is high if probable and surprising cases are present.

Cross entropy can be viewed as a measure of the difference between two distributions. Cross-entropy is widely used in Deep Learning 
as a loss function to enable the learning. In that, the true probability distribution is the label and predicted 
distribution is the value from the current model. The cross-entropy loss of two distribution p and q can be defined 
as below:

    CE(P, TP) = - ∑ TP(xi) * log(P(xi))
KL divergence, also known as relative entropy, measures the difference between two probability
distributions P and TP. It quantifies the amount of information loss when P is used to approximate TP.
It is a non-negative. Mathematically, KL divergence is defined as:

    KLD(P, TP) =  ∑ TP(xi) * log(TP(xi) / P(xi))
Cross entropy and KL divergence are related as follows:

    CE(P,TP) = E(TP) + KLD(P,TP)
Cross-entropy is widely used as a loss function in supervised learning, especially in classification tasks. 
KL divergence is more common in measuring the difference between two distributions in statistical models and 
is often used in unsupervised learning or as a regularization term.

## Entropy example
The true probabilities (TP=[TP(x0) TP(x1)]) are

    TP(x0)=0, TP(x1)=1
And the probability for case 1 P(x1) is 

    P(x1)=1-P(x0)
It is now possible to relate E,CE,KLD to an estimated P(x0) as

| P(x0) | E,CE,KLD         | Comment                                 |
|-------|:-----------------|:----------------------------------------|
| 0     | 0,0,0            | P equal to TP, zero entropy/CE          |
| 1/7   | 0.41,0.15,0.15   | P differs from TP, some entropy/CE      |
| 3/7   | 0.68, 0.55, 0.55 | P differs much from TP, much entropy/CE |

Insights: 
        
    One can use CE as a measure on how close P is to TP. The smaller, the more equal.

    One can use E to check how much probabilities varies in a distribution

KLD is equal to CE in this specific example because E(TP) is zero.






