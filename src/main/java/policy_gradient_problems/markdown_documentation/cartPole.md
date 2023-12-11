



        
    Initialize actor network with random weights θ
    Initialize critic network with random weights ω
    Initialize experience buffer
    Initialize end time T, can potentially be infinite

    While training not complete:
    // Phase 1: Run Episodes
    Initialize state s
    While t<T
        Select action a using the current policy π(a|s, θ)
        Take action a, observe reward r and new state s'
        Store experience (s, a, r, s') in buffer
        If s' is terminal then break
        s ← s'
        t ← t+1
    

    // Phase 2: Update Critic and Actor using experience elements with s' not terminal
    For tau in range(0, T-n+1) 
        //Calulate return G 
       (s, a, r, s') ← experience from buffer element tau 
        G ← discounted sum of rewards for buffer elements in range (tau,end buffer)
        If s' is not a terminal state:
            G ← G+V(s')  

        // Update critic
        Critic loss ← (G - V(s))^2
        Update ω to minimize critic loss

        // Update actor
        Advantage A ← G - V(s)
        Actor loss ← -log π(a|s, θ) * A
        Update θ to minimize actor loss

    Clear the experience buffer