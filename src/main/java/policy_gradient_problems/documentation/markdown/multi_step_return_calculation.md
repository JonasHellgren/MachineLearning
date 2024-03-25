# Multi step return calculation


    T = length(episode_history)

    # Determine the episode outcome and set tEnd accordingly
    If terminal state is a failure:
        tEnd = T - 1
    Else:
        tEnd = max(0, T - N) # Skip the last N states for non-failures

    # Calculate n-step returns and update for steps up to tEnd
    for each step t in episode from start to tEnd do:
        Initialize return G = 0
        Initialize discount factor γ_current = 1
        for k from 0 to N-1 do:
            # Ensure k steps ahead is within the episode
            if t+k < T:
                G += γ_current * episode_history[t+k][2] # Add discounted reward
                γ_current *= γ
            else:
                break # Exit the loop if exceeding the episode's bounds

        # Add value of the state at t+N if within the episode's bounds
        if t+N < T:
            G += γ_current * V(episode_history[t+N][0]) # V(s_{t+N})

        # Store G as the n-step return for step t