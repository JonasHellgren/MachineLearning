# Sequential linear regression update

Sequential linear regression update is an iterative procedure used in machine learning, specifically in the context of linear regression or similar algorithms 
using gradient descent for optimization. Let's go through each step in detail:

The process below is repeated for each new data point (y,x)


### 1 Prediction calculation

In this step, the model makes a prediction (Pred) based on the current values of the parameters (θ)
and the input features (x)

        yPred=θ0*x0+θ1*x1..+θn*xn

where xn is 1, because θn is bias/intercept term (θn=m in the basic relation y=kx+m)

### 2 Error calculation

After making a prediction, the next step is to calculate the error (e) in the prediction.
    
        e=y-yPred

### 3. Parameter updating
This is the crucial step where the model learns from the data by updating its parameters based on the error
calculated in the previous step.

    
        θi <- θi+αLearn*e*xi



