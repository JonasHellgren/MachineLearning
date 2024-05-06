# Energy buying
In this environment the objective is to buy energy (electricity) optimally under varying price conditions. 
This kind of environment is typical in energy management systems, especially those that integrate battery storage technologies.
Here’s a more detailed breakdown of the various components and constraints of such an RL environment:

## 1. State Space:
The state of the environment at any time step could include:
* State of Charge (SoC): The current energy level in the battery as a percentage of its total capacity.
* Time: The current time step or hour of the day.

## Given data
* Battery energy capacity, energyBatt
* Battery power capacity, [-powerBattMax,powerBattMax]
* Electricity Prices: priceAtTime(t)

## Action Space:
Continous power in kW.

## Reward Function:
      The reward function should encourage buying low and selling high. 
   
             r= isTerminal
                   (yes) -> (soc-socStart)*energyCap*priceEnd
                   (no) ->  power*dt*priceAtTime

## Constraints:
   Power Constraints: The action, which is the power level of electricity bought, cannot exceed a known maximum. 
   It must also be non-negative (i.e., no selling).
   Battery Constraints: SoC must remain below a maximum level (socMax).

        c0: power>0
        c1: power<powerBattMax
        c2: soc<socMax
        
##  Environment Dynamics:

        isTerminal = true if time>=timeEnd

        soc <- soc+power*dt/energyBatt



![energy_buying.jpg](..%2Fpics_env%2Fenergy_buying.jpg)