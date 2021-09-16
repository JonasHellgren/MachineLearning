# Java open AI gym
The [Open AI gym](https://gym.openai.com/envs/#classic_control) is an excellent toolkit for developing and comparing reinforcement learning algorithms. 
Some example environments are Atari games such as Pong. Classical control problems as inverted pendulum are also included. These environments are coded in Python.
The package described in this document is coded in Java, a Java version of Open AI gym.  
## Scope
The ambition is to cover a sub set of the environments in "original" Open AI gym, the focus is on problems with discrete actions. 


## Major classes 
The major clases in the package are the abstract classes: 1) Environment and 2) AgentNeuralNetwork.
As the class diagram below shows subclasses of Environment implements different environments. These are described later in the document.
![node](..\java_ai_gym\mdpics\classes_environment.png)

All agents uses the interface/behavior Learable. This interface specifies, for example, that an agent shall implement the method chooseBestAction().
The abstract class AgentNeuralNetwork, is the working horse in the package. It has methods enabling using deep Q-learning.
Subclasses of AgentNeuralNetwork are agents specialized to solve a specific environment. In this subclasses are for example the network architecture and hyper parameters such as learning rate specified.

![node](..\java_ai_gym\mdpics\classes_agent.png)

Additional comments and descriptions:
* Every environment has a variable parameters defining data in the class. For cart pole it can for example be the pole mass.
* The class EnvironmentParametersAbstract "forces" the constructor of an environment to define the names of the states in the environment.
* The step method, defined in all environments, is very critical. It defines the consequence of taking an action in a specific state.

## Environments
### Six rooms

![node](..\java_ai_gym\mdpics\6room_environment.png)

### Mountain car

### Cart pole

## Agents
### Six rooms agent

### Mountain car agent

### Cart pole agent

## Example training




