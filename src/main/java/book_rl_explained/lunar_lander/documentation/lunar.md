
# Lunar lander

![lunar.png](pics%2Flunar.png)

### State variables
y,v	 

Height over ground and speed relative ground
### Action set
-foreMax<action<foreMax

Applied force F
### Terminal states
isLanded(y) or isToHighPosition(y)

Touching ground
### Fail states
isLanded(y)  and isToHighSpeed(speed) or isToHighPosition(y)

Hitting ground to hard or outside of upper bound


### Physics

F-m*g=m*a

dv/dt=a

dy/dt=v