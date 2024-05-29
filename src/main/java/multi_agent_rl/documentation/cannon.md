# Cannon
A cannon has the objective to shoot a projectile to hit a target at position (x,y). The projectile hit position is 
determined from two angles, aYaw and aPitch. The angles are defined in the figures below.
Two agents controls the angles. Agent Y controls the yaw angle of the projectile.  Agent P controls the pitch angle 
of the projectile. Pitch angle can been seen as controlling the shooting length.

Both agents should learn to coordinate their angle adjustments to successfully hit the target.

![cannon_xy.png](pics%2Fcannon_xy.png)

![cannon_xz.png](pics%2Fcannon_xz.png)

Assumptions:
* The initial speed of the projectile is constant, simplifying the dynamics focused on angle adjustments.
* The target is always at a distance within the range of the projectile.
* The projectile is shot when both angle rates are zero.
* The yaw angle is not bounded, it can be any value between zero and 2*pi
* The pitch angle is bounded between zero and pi/4.

## Actions

The action space is change in yaw and pitch angle: (daYaw,daPitch)

Each agent can choose between following actions:
* positive large angle change, daLarge
* positive small angle change, daSmall
* negative large angle change, -daLarge
* negative small angle change, -daSmall
* zero change

## State transitions

Yaw angle is updated by

    aYaw <- aYaw+daYaw        

and pitch angle is updated by

    aPitch <- clip(aPitch+daPitch,aPitchMin,aPitchMax)        

Episode ends when both angles have zero change, when projectile is shoot with current angles and distance from target can be calculated.

## Reward function

The reward R is defined so the cannon should rotate towards the target in shortest possible time.

        R=-ct+rHit

where ct is cost penalty for each step and rHit is the reward for hitting, or being close to hitting.
A radial basis function https://en.wikipedia.org/wiki/Radial_basis_function defines reward for hitting

        rHit=e^[(-eps*dev)^2]

The parameter esp defines how spiky rHit is. The larger the more spiky. If eps is one, rHit is non-zero for 
approximately dev smaller than 1. Deviation is the euclidian distance between hit position and target position.

## Feature exctraction

Radial Basis Function (RBF) network is used as feature extractor with a linear approximator.  
Transforms the input space into a higher-dimensional space where linear separation becomes feasible.

Basis Functions: Centered on specific points with a defined spread, these functions typically have a Gaussian shape, 
transforming each input feature into a feature space based on its distance from the center.

      vrbi=vrb(a,ci,σi)=exp(-((a - ci)^2) / (2 * σi^2))

Parameters:
* Centers (c): Points in the input space where each RBF is centered.
* Spread (σ): Controls the width of the bell-shaped curve, influencing how drastically the output decreases as the input moves away from the center.

RBF centers every 10 degrees within the allowed range. The spread is σ.

Assume a function F is required, can for example be the value for being in state s. When F is approximated by

       F=sum(vrbi*thetai)

where theta is a parameter vector, corresponding to weights in a neural network. State s maps to vector vrb, which non zero elements
are "fired" for centers close to the state.




