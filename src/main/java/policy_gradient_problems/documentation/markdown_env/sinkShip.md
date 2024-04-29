# Sink the ship

A cannon shots a projectile with the angle δ, see figure below. The objective is to sink a ship located with a distance d<sub>0</sub> 
or d<sub>1</sub> from the cannon. The angle δ results in the projectile flying d meters. The states are 0 and 1.

![shooting_at_ship.png](..%2Fpics%2Fshooting_at_ship.png)


## Transition model, reward model and action definition
The transition model is very simple, the state is kept during a transition.
The reward is 1 for hitting a ship else 0.
The action a is the normalized angle, a value between 0 and 1, or mathematically: angle=a*angle<sub>max</sub>.

The shooting distance is calculated by
![shootingDistance.png](..%2Fpics%2FshootingDistance.png)

It is derived from combining below equations and setting height h as zero.

![forDistanceEq.png](..%2Fpics%2FforDistanceEq.png)

## Trainer algorithm
An episode starts in a random state, hence optimal angle for both states will be learnt.

## Example results
Parameters for a simulation is given below.

| Parameter | Value | Comment                                  |
|-----------|:------|:-----------------------------------------|
| nofEpisodes  | 2000  |                                          |
| nofStepsMax  | 100   | Max nof steps in an episode              |
| gamma | 1.0   |                                          |
| learningRateReplayBufferCritic      | 1e-3  | Unstable learning for large(r) values    |
| MAX_GRAD_ELEMENT | 1.0   | Max value of any item in grad log vector |

One reflections from the results plots is that angles converges to expected values. The standard deviation, for both states, reaches there lower allowed limits.
The right panels is the values estimated by the critic, they indicate hit in approx 30% of times for the
exploratory actor using a relatively high standard deviation.

![shipResults.png](..%2Fpics%2FshipResults.png)
Then MAX_GRAD_ELEMENT is set as very large, training gets unstable, see figure below
![shipResultsUnstable.png](..%2Fpics%2FshipResultsUnstable.png)

## Parameter update relations
This section presents how the agent parameters are updated
![gradLogShip.png](..%2Fpics%2FgradLogShip.png)

Above, in the very bottom of the picture, is the derivate of standard deviation given. Let us call ut gradStd.
But how to get derivate of theta knowing derivate of standard deviation (gradStd)? That is answered below.

![gradStdTheta.png](..%2Fpics%2FgradStdTheta.png)

A critical part is the parameter gradients. The code for this is below. It is a realization of 
the equations given in the above figures. Parameter updates get unstable if std approaches small values, see division for deriving gradMean.
This was handled by setting gradStd as zero if std is below a threshold STD_MIN.
On top of this every element in the grad log vector is clipped to a value between

[-MAX_GRAD_ELEMENT,MAX_GRAD_ELEMENT].


        double denom = secondArgIfSmaller.apply(sqr2.apply(std), SMALLEST_DENOM);
        double gradMean=1/denom*(action-mean);
        double gradStd=zeroIfTrueElseNum.apply(std<STD_MIN,sqr2.apply(action-mean)/sqr3.apply(std)-1d/std);
        double gradStdTheta=scaleToAccountThatStdIsExpTheta.apply(gradStd,1d/std);