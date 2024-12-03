
# Lunar lander

### State variables
y,v	 

Height over ground and speed relative ground
### Action set
-1<action<1

Applied force F
### Terminal states
y<ySurface	

Touching ground
### Fail states
(y<ySurface) and (v>vMax)	

Hitting ground to hard


### Physics

F-m*g=m*a

dv/dt=a

dy/dt=v