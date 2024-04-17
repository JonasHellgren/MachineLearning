package safe_rl.environments.buying_electricity;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;
import common.joptimizer.LowerBoundConstraint;
import common.joptimizer.UpperBoundConstraint;
import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * min f=1/2*(power-powerProposed)^2  w.r.t.
 * power>powerMin
 * power<powerMax
 * soc+power*g<socMax
 * <p>
 * f=1/2*x*M*x+k*x+r=1/2*(power^2-2*powerProposed)+powerProposed^2=
 * 1/2*power^2-1*powerProposed+1/2*powerProposed^2
 */

@Builder
public class SafeChargeOptModel {
    public static final int N_VARIABLES = 1;

    Double powerProposed;
    @Builder.Default
    Double powerMin = 0d;
    Double powerMax;
    @Builder.Default
    Double powerInit=1e-5;
    BuySettings settings;
    @Builder.Default
    Double socMax = 1d;
    Double soc;

    final OptimizationRequest or = new OptimizationRequest();
    final JOptimizer optimizer = new JOptimizer();

    public ConvexMultivariateRealFunction costFunction() {
        double[][] pMatrix = {{1}};
        double[] kVector = new double[]{-powerProposed};
        double r = 1d / 2d * powerProposed * powerProposed;
        return new PDQuadraticMultivariateRealFunction(pMatrix, kVector, r);
    }

    public void setSoCAndPowerProposed(Double soc, Double powerPropose) {
        this.powerProposed = powerPropose;
        this.soc = soc;
    }

    public boolean isAnyViolation(double power) {
        double constraintMax = ListUtils.findMax(getConstraintValues(power)).orElseThrow();
        return constraintMax > 0;
    }

    public List<Double> getConstraintValues(double power) {
        var constraints = constraints();
        var vector = new DenseDoubleMatrix1D(N_VARIABLES);
        vector.set(0, power);
        return Arrays.stream(constraints).map(f -> f.value(vector)).toList();
    }

    public double correctedPower() throws JOptimizerException {
        double[] initialPoint = {powerInit};
        var response = getOptimizationResponse(initialPoint);
        return response.getSolution()[0];
    }


    ConvexMultivariateRealFunction[] constraints() {
        var inequalities = new ConvexMultivariateRealFunction[3];
        double powerMaxSoc = calcPowerMaxSoc();
        inequalities[0] = LowerBoundConstraint.ofSingle(powerMin);
        inequalities[1] = UpperBoundConstraint.ofSingle(powerMax);
        inequalities[2] = UpperBoundConstraint.ofSingle(powerMaxSoc);
        return inequalities;
    }

    OptimizationResponse getOptimizationResponse(double[] initialPoint) throws JOptimizerException {
        defineRequest(or, initialPoint);
        optimizer.setOptimizationRequest(or);
        optimizer.optimize();
        return optimizer.getOptimizationResponse();
    }

    void defineRequest(OptimizationRequest or,double[] initialPoint) {
        or.setMaxIteration(10_000);
        or.setF0(costFunction()); // Set the objective function
        or.setFi(constraints());
        or.setInitialPoint(initialPoint); // Optional: initial guess
        or.setToleranceFeas(1e-3); // Tolerance on feasibility
        or.setTolerance(1e-3); // Tolerance on optimization
    }


    private double calcPowerMaxSoc() {
        double gFunction = settings.dt() / settings.energyBatt();
        return (socMax - soc) / gFunction;
    }


}
