# Multi step return calculation


Time steps between 0 and tEnd are used.
So time t is in [0,tEnd].  An episode ends at time T, see figure below.
A critical part is the target value, it is defined in addValueTarget(sumRewards + valueFut).
The term valueFut makes things a bit tricky, hence a deeper explanation follows.
If isFutureStateOutside() is true, t+tHor is outside the end of the episode, hence valueFut must be zero.

|-----|-----|-----|-----|-----|-----|-----|-----|-----|

0__ t______        tEndep________       t+tHor

If isEndExperienceFail() is true the episode ended in fail state. In this case we want tEnd to cover the entire episode.
The reason is that we want to learn also from steps/states close to T. This is done by setting tEnd as
nofExperiences.
If isEndExperienceFail() is false the episode ended in non fail state.  In this case tEnd must be restricted
so wrong learning not will take place from steps outside T. This is done by setting tEnd as
nofExperiences-n+1.
Or phrased differently, only states present earlier in the episode will be updated if not ending in fail state.
The reason is that future, t+tHorizon, state values are not known for states later in the episode.


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
                G += γ_current * reward[t+k] //Add discounted reward
                γ_current *= γ
            else:
                break # Exit the loop if exceeding the episode's bounds

        # Add value of the state at t+N if within the episode's bounds
        if t+N < T:  G += γ_current * V(t+N]) 

        # Store G as the n-step return for step t