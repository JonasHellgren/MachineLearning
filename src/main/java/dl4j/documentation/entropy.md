# Entropy

In the following text P(x) means probabilty for "case" x. For example specific action in RL.
TP means true probability, i.e. desired reference value.

## Definitions
Higher entropy means lower chaos. It is slightly different in information theory. The mathematician Claude Shannon
introduced the entropy in information theory in 1948. Entropy increases with increases in uncertainty.

    E(P) = - ∑ P(x) * log(P(x))
can also be defined from surprise, how unexpected an event is

    E(P) = ∑ P(x) * S;  Surprise=S=log(1/P(xi))
Cross entropy can be viewed as a measure of the difference between two distributions. Cross-entropy is widely used in Deep Learning 
as a loss function to enable the learning. In that, the true probability distribution is the label and predicted 
distribution is the value from the current model. The cross-entropy loss of two distribution p and q can be defined 
as below:

    CE(P, TP) = - ∑ TP(x) * log(P(x))
KL divergence, also known as relative entropy, measures the difference between two probability
distributions P and TP. It quantifies the amount of information loss when P is used to approximate TP.
It is a non-negative. Mathematically, KL divergence is defined as:

    KLD(P, TP) =  ∑ TP(x) * log(TP(x) / P(x))
Cross entropy and KL divergence are related as follows:

    CE(P,TP) = E(TP) + KLD(P,TP)
Cross-entropy is widely used as a loss function in supervised learning, especially in classification tasks. 
KL divergence is more common in measuring the difference between two distributions in statistical models and 
is often used in unsupervised learning or as a regularization term.

## Entropy example
The true probabilities (TP=[tp0 tp1]) are

    tp0=0, tp1=1
And the probability for case 1 (p1) is 

    p1=1-p0
It is now possible to relate E,CE,KLD to an estimated p0 as

| p0  | E,CE,KLD         | Comment         |
|-----|:-----------------|:----------------|
| 0   | 0,0,0            | Zero entropy/CE |
| 1/7 | 0.41,0.15,0.15   | Some entropy/CE |
| 3/7 | 0.68, 0.55, 0.55 | Much entropy/CE |

Insights: 
        
    One can use CE as a measure on how close P is to TP. The smaller, the more equal.

    One can use E to check how much probabilities varies in a distribution

KLD is equal to CE in this specific example because E(TP) is zero.






