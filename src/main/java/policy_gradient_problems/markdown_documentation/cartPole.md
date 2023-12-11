



        
    Initialize actor network with random weights θ
    Initialize critic network with random weights ω
    Initialize experience buffer
    Initialize end time T, can potentially be infinite

    While training not complete:
    // Phase 1: Run Episode
    Initialize state s
    While t<T
        a ← π(a|s, θ)  //Select action a using the current policy
        Take action a, observe reward r and new state s'
        Store experience (s, a, r, s') in buffer
        If s' is terminal then break
        s ← s'
        t ← t+1

    // Phase 2: Update Critic and Actor using experience elements with s' not terminal
    For tau in range(0, T-n+1) 
        //Calulate return G 
        (s, a, r, s') ← experience from buffer element tau 
        sn' ← nest state n step ahead from s
        G ← discounted sum of rewards for buffer elements in range (tau,end buffer)
        If sn' is present : //s n step ahead from s not is terminal
            G ← G+γ^n*V(sn')  

        // Update critic
        criticLoss ← (G - V(s))^2
        Update ω to minimize criticLoss

        // Update actor
        A ← G - V(s)  //Advantage
        actorLoss ← -grad theta log π(a|s, θ) * A
        Update θ to minimize actorLoss

    Clear the experience buffer