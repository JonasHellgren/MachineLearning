# Energy trading
In this environment the objective is to buy and sell energy (electricity) optimally under varying price conditions. 
This kind of environment is typical in energy management systems, especially those that integrate battery storage
technologies. Here’s a more detailed breakdown of the various components and constraints of such an RL environment:

## 1. State Space:
The state of the environment at any time step could include:
* State of Charge (SoC): The current energy level in the battery as a percentage of its total capacity.
* Time: The current time step or hour of the day.

## Given data
* Battery energy capacity, energyBatt
* Battery power capacity, [-powerBattMax,powerBattMax]
* Electricity Prices: priceAtTime(t)
* powerCapacityFcr, how much capacity provided to FCR
* priceFCR, compensation per kW and hour to provide powerCapacityFcr
* socTerminalMin, min required end soc

## Action Space:
power, continous power in kW. If positive, energy is bought from energy market

## Reward Function:
      The reward function should encourage buying low and selling high. 
   
             r= income-expense = priceFCR()* powerCapacityFcr+priceEnergy*energySell-
                (priceEnergy*energyBuy+ priceBattery()* abs(dSoh))

            either energySell or energyBuy is positive, at least one is always zero, depends on action power



## Constraints:
   Power Constraints: The action, which is the power level of electricity bought, cannot exceed a known maximum. 
   It must also be non-negative (i.e., no selling).
   Battery Constraints: SoC must remain below a maximum level (socMax).

        c0: power-powerFcr>-powerBattMax
        c1: power+powerFcr<powerBattMax
        c2: soc+g*(power-powerFcr)-dSoCPC>socMin
        c3: soc+g*(power+powerFcr)+dSoCPC<socMax()
        c4: soc+g*(power-powerFcr)+dSocMax(time)>socTerminalMin

where g is dt / energyBatt and powerFcr is the lumped power during time step used fot FCR market. 
        
##  Environment Dynamics:

        isTerminal = true if time>=timeEnd

        soc <- soc+power*dt/energyBatt
        soh <- soh-dSoh



![energy_buying.jpg](..%2Fpics_env%2Fenergy_buying.jpg)