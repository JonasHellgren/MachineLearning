# Safe action selection

## Experience list creation from safe action learning

    1. Observe state and get action a according to policy
    2. Check action against safety constraints
    3. Any constraint violated
         (yes) -> - apply QP to find corrected and safe action, aSafe
                  - execute aSafe, observe reward rSafe and next state s'Safe
         (no)  -> - execute a, observe reward r and next state s'
    4. Create experience item <s,a,r,s',<Optional(ExperienceSafe>>. Optional(ExperienceSafe> is empty 
    if no constraint violation
    




