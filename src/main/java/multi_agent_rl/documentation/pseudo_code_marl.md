# Marl algorithms

Advantage A is derived from

    Advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s)

For all algorithms following initialization is performed

    Initialize critic memory. Maps state s to value V. 
    Initialize actor memories (θi). Maps state s to action probabilities.
    Initialize experience buffer
    Initialize end time T, a finite number
    Initialize n, nof steps ahead for critic state value backup


## Independent learning
This pseudocode outlines the structure for implementing independent learning in MARL. Each agent independently learns 
its policy and value function based on its own experiences, without sharing information with other agents. 
This approach is suitable for environments where agents do not need to coordinate closely and can operate independently.

    Repeat (for each episode):
    //Phase 1: Run Episode
    Initialize state s, t ← 0
    While t<T and s is not terminal
        ai ← πi(obsi, θi)  //Select actions for all agents 
        step environment from all actions a1,a2..., observe reward r, new state s' etc
        Store experience (s, ai, r, s',isTerminal,isFail) in buffer, one buffer per agent
        s ← s'
        t ← t+1

    //Phase 2: Update Critics
    For every agent i:
    Create critic batch, (s,G) elements created from buffer for agent, using n steps    
    Update critic memory for agent i based on batch 

    //Phase 3: Update actors
    For every agent i:
    For t in range(0, T)
    A ← r+γ*V(s')-V(s)  
    actorLoss ← -∇θilog πi(ai|s,θi) * A
    Update θi to minimize actorLoss


## One centralized critic

Below pseudocode outlines the structure for using a centralized critic with decentralized actors in a MARL setup. 
The centralized critic evaluates the joint state and actions, providing stable value estimates, while each actor 
independently updates its policy based on local observations and the advantage calculated from the critic's value estimates.
State in code below means local observation for an agent.


    Repeat (for each episode):
    //Phase 1: Run Episode
    Initialize state s, t ← 0
    While t<T and s is not terminal
        ai ← πi(obsi, θi)  //Select actions for all agents 
        step environment from all actions a1,a2..., observe reward r, new state s' etc
        Store experience (s, actions, r, s',isTerminal,isFail) in criticBuffer
        Store experience (obsi, ai, r, obsi',isTerminal,isFail) in every actorBufferi
        s ← s'
        t ← t+1

    //Phase 2: Update Critic  
    Create critic batch, (s,G) elements created from buffer using n steps    
    Update critic memory based on batch //Typically criticLoss ← (G - V(s))^2. 

    //Phase 3: Update actors
    For t in range(0, T)
        A ← r+γ*V(s')-V(s)  //Advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s)
        For every agent i
            Extract obsi from actorBufferi
            actorLoss ← -∇θilog πi(ai|obsi,θi) * A
            Update θi to minimize actorLoss




Common Critic: Use when agents need to tightly coordinate their actions and share a common goal. It simplifies the learning process and improves stability.
Separate Critics: Use in competitive or mixed environments where agents might have different goals or roles. It allows for more personalized and diverse learning strategies.