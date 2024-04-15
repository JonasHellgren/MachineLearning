# Sequential linear regression update

The process below is repeated for each new data point (y,x)


1 Prediction calculation

    yPred=θ0*x0+θ1*x1..+θn*xn

where x0 is 1, because θ0 is bias/intercept term

2. Error calculation

    
    e=y-yPred

3. Parameter updating

    
    θi=θi-αLearn*e*xi



