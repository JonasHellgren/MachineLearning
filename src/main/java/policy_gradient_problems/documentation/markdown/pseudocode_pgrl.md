# Pseudocode policy gradient reinforcement learning

## General reinforcement learning algorithm

    Initialize environment and agent
    Repeat (for each episode):
    Initialize s, the starting state of the environment
    Repeat:
        a ← π(a|s)  //Select action a using the current policy
        Take action a, observe reward r and new state s'  //apply action on environment
        Updates agent based on experience(s), one experience is (s,a,r,s')
    Until episode ends (e.g., terminal state reached or max steps exceeded)


## Reinforce vanilla

    Initialize policy parameters θ
    Repeat (for each episode):
    Generate an episode following the current policy π(·|θ), store experiences
    For each step t of the episode:
        Gt = Return from step t  //sum of rewards from experience at time t until end of episode
        Update actor: θ = θ + αActor * ∇θ log π(a|s, θ) *Gt


## Reinforce with baseline

    Initialize policy parameters θ
    Initialize value function parameters w
    Repeat (for each episode):
    Generate an episode following the current policy π(·|θ), store experiences
    For each step t of the episode:
        s ← exper(t)   
        Gt = Return from step t  
        A = Gt - V(s) //advantage using value of s
        Update actor: θ = θ + αActor * ∇θ log π(a|s, θ)*A 
        Update value function: w = w + αValue * A * ∇w Vw(St)

## One step Actor-Critic

    Initialize policy parameters θ
    Initialize value function parameters w
    Repeat (for each episode):
    Initialize s //initial state
    Repeat (for each step of the episode):
        a ← action given by current policy π(·|θ) in state s
        Take action a, observe reward r and next state s'
        δ ← r + γ * V(s') - V(s)  //TD error
        Store s, estimated Gt, δ for later actor and critic updating
        s ← s'  
        Until s is terminal

    Update actor: θ ← θ + αActor * ∇θ log π(a|s, θ) * δ
    Update critic:  w ← w + αCritic * ∇w V(s) * δ 



## Multi step actor critic
This algorithm makes no critic or actor updates during an episode. Hence, suited for environments with 
finite episodes. Thanks to the multi-step critic training, a balance between variance and bias can be achieved.
Many steps (large n) gives high variance, while few steps gives higher bias.

        
    Initialize critic memory with random weights ω. Maps state s to value V. 
    Initialize actor memory with random weights θ. Maps state s to action probabilities.
    Initialize experience buffer
    Initialize end time T, a finite number
    Initialize n, nof steps ahead for critic state value backup

    Repeat (for each episode):
    //Phase 1: Run Episode
    Initialize state s, t ← 0
    While t<T and s is not fail
        a ← π(a|s, θ)  //Select action a using the current policy
        Take action a, observe reward r, new state s' and if s' isFail
        Store experience (s, a, r, s',isFail) in buffer
        s ← s'
        t ← t+1

    //Phase 2: Update Critic  
    tEnd = if end experience in buffer isFail then T else T - n + 1
    Initialize critic training batch
    For t in range(0, tEnd)
        (s, a, r, s') ← experience from buffer element t 
        sn' ← nest state n step ahead from s
        G ← discounted sum of rewards for buffer elements in range (tau,end buffer)
        If sn' is present : //s n step ahead from s not is terminal
            G ← G+γ^n*V(sn')  
        Add (s,G) elements to batch   
    Update critic memory weights ω based on batch //Typically criticLoss ← (G - V(s))^2. 

    //Phase 3: Update actor
    For t in range(0, T)
        A ← r+γ*V(s')-V(s)  //Advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s)
        actorLoss ← -∇θlog π(a|s,θ) * A
        Update θ to minimize actorLoss


## Proximal Policy Optimization (PPO)

    Repeat (for each iteration):
    Collect set of trajectories D_k by running policy π_θ_old in the environment
    Estimate advantages Â_t using the current value function V_w
    Optimize surrogate L wrt θ, with K epochs and minibatch size M ≤ |D_k|:
    For each epoch:
    For each minibatch:
    θ ← θ + α * ∇_θ L(θ_old, θ)
    where L(θ_old, θ) is defined as:
    L(θ_old, θ) = ̂E_t[min(r_t(θ) Â_t, clip(r_t(θ), 1-ε, 1+ε) Â_t)]
    with r_t(θ) = π_θ(a_t|s_t) / π_θ_old(a_t|s_t)
    Update the value function by fitting V_w to the observed returns:
        w ← w - α_v * ∇_w MSE(V_w(s_t), G_t)
    Update policy parameters: θ_old ← θ
    Until convergence or maximum iterations reached

## PPO - One fit per episode

    Repeat (for each iteration):
    Initialize s (initial state)
    Repeat (for each episode):
    Collect a trajectory D by running policy π_θ_old in the environment from initial state s to termination
    For each step in D:
    Calculate advantage Â_t using the current value function V_w for each s_t in the trajectory
    Optimize surrogate L wrt θ for one epoch using trajectory D:
    θ ← θ + α * ∇_θ L(θ_old, θ)
    where L(θ_old, θ) is defined as:
    L(θ_old, θ) = min(r_t(θ) Â_t, clip(r_t(θ), 1-ε, 1+ε) Â_t)
    with r_t(θ) = π_θ(a_t|s_t) / π_θ_old(a_t|s_t)

    Update the value function by fitting V_w to the observed returns from D:
            w ← w - α_v * ∇_w MSE(V_w(s_t), G_t)
    Update policy parameters: θ_old ← θ

    Until convergence or maximum iterations reached

