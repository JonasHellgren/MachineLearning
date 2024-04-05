# Grid Environment Structure
Grid Size: The environment is structured as a 4x3 grid, meaning there are 4 columns and 3 rows, making up a total of 12 spaces or states the agent can occupy.
Coordinate System: The grid's coordinate system starts at the bottom left corner, with this position labeled as x=0,y=0.
x values increase as you move to the right, and y values increase as you move upwards.

![maze.png](..%2Fpics%2Fmaze.png)
# Agent's Movement
The agent can move in four directions: up, down, left, and right.

There's an 80% (0.8 probability) chance the agent moves in the direction intended.
There's a 20% (0.2 probability) chance the agent moves at right angles to the intended direction. This implies if the agent intends to move up, there's a 10% chance it could move left and a 10% chance it could move right, but never in the opposite direction of the intended move.

There is a wall located at the position x=1,y=1. If the agent attempts to move into the wall, it stays in its current position instead. This rule applies to any movement attempt into any grid boundary or wall.
# Terminal States
A terminal state with a reward of +1 is located at x=3,y=2. Reaching this state ends the episode.
Another terminal state with a reward of -1 is at x=3,y=1. Reaching this state also concludes the episode.

# Rewards System
Movement Penalty: Each move that does not result in reaching a terminal state incurs a -0.04 reward, encouraging the agent to reach a terminal state efficiently.
No Movement: If the agent hits a wall or boundary, resulting in no movement, the same -0.04 penalty applies, as it is considered a step.