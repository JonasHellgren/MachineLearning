# Safe action selection

## Experience list creation from safe action learning

    1. Observe state and get action a according to policy
    2. Check action against safety constraints, any constraint violated?
         (yes) -> - apply QP to find corrected and safe action, aSafe
                  - execute aSafe, observe reward rSafe and next state s'Safe
         (no)  -> - execute a, observe reward r and next state s'
    4. Create experience item <s,a,r,s',<Optional(ExperienceSafe>>. Optional(ExperienceSafe> is empty 
    if no constraint violation, else filled with (aSafe,rSafe,s'Safe). The terms r,s' are relevant (non empty)
    only if a is safe (a is executed).


## Training using safe action learning    

    Compute returns G_t for each time step t in the episode using multi-step returns
    For each step, 
        ExperienceSafe is present?
        (yes) -> create triplet(aSafe,adv,probOld(aSafe)) and triplet(aNonSafe,advNonSafe,probOld(aNonSafe)). 
                 Advantage advNonSafe is some negative value, to penalize aNonSafe
        (no) ->  create triplet(a,adv,probOld(a)). advantage adv using G_t and V(s_t, ϕ), probOld(a)
        add triplet(s) to buffer
    end for
    for each epoch do:
    Define mini-batch from B
    for each mini-batch do:
        Update actor network 
        Update critic network 
    end for
    end for

