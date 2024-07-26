# Domain class info

### Type parameters

In many classes, for example EnvironmnetI, there are type parameters. These are named <V,A,P>.
The motivation is to make the class methods general. General in this context means possible to 
use for different environments.

### <V,A,P>
V stands for variables in the state class, can for exampe be x and y position.
A is action properties, can for example be the change in y-position for a specific action.
Finally, P represent environment properties. For example the of x-positions in a grid environment.

<V,A,P> is defined in the main calling class, for example RunnerAvoidObstacleTrainAndPlot.
These type classes later propagates down to used classes, for example environment class.









