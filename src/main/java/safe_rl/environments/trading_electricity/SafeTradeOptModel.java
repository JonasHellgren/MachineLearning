package safe_rl.environments.trading_electricity;

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
import lombok.NonNull;
import lombok.extern.java.Log;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.OptModelI;
import safe_rl.domain.abstract_classes.StateI;
import java.util.Arrays;
import java.util.List;

/**
 * min f=1/2*(power-powerProposed)^2  w.r.t.
  constraints expressed below (jsut above method constraints)

 * f=1/2*x*M*x+k*x+r=1/2*(power^2-2*powerProposed)+powerProposed^2=
 * 1/2*power^2-1*powerProposed+1/2*powerProposed^2
 */

@Builder
@Log
public class SafeTradeOptModel<V> implements OptModelI<V> {
    public static final int N_VARIABLES = 1;
    public static final int MAX_ITERATION = 10_000;
    public static final int N_CONSTRAINTS = 5;

    @NonNull Double powerProposed;
    @NonNull Double powerMin;
    @NonNull Double powerMax;
    @Builder.Default
    Double powerInit=1e-25;
    @NonNull SettingsTrading settings;
    @Builder.Default
    Double socMin = 0d;
    @Builder.Default
    Double socMax = 1d;
    @NonNull Double socTerminalMin;
    @NonNull Double soc;
    @NonNull Double timeNew;
    @Builder.Default
    Double toleranceOptimization = 1e-5;

    final OptimizationRequest or = new OptimizationRequest();
    final JOptimizer optimizer = new JOptimizer();

    public ConvexMultivariateRealFunction costFunction() {
        double[][] pMatrix = {{1}};
        double[] kVector = new double[]{-powerProposed};
        double r = 1d / 2d * powerProposed * powerProposed;
        return new PDQuadraticMultivariateRealFunction(pMatrix, kVector, r);
    }

    @Override
    public void setModel(StateI<V> state0, Action action) {
        log.warning("setModel, state0="+state0 );
        StateTrading state= (StateTrading) state0;
        this.powerProposed = action.asDouble();
        this.soc = state.soc();
        this.timeNew=state.time()+settings.dt();
    }

    @Override
    public boolean isAnyViolation() {
        double constraintMax = ListUtils.findMax(getConstraintValues()).orElseThrow();
        return constraintMax > 0;
    }


    @Override
    public double correctedPower() throws JOptimizerException {
        double[] initialPoint = {powerInit};
        var response = getOptimizationResponse(initialPoint);
        return response.getSolution()[0];
    }

    public List<Double> getConstraintValues() {
        var constraints = constraints();
        var vector = new DenseDoubleMatrix1D(N_VARIABLES);
        vector.set(0, powerProposed);
        return Arrays.stream(constraints).map(f -> f.value(vector)).toList();
    }

    /**
     *   powerFcr can be both neg and pos, hence "worst sign" considered in each constraint
     *  [0] power>powerMin+powerFcr
     *  [1] power<powerMax-powerFcr
     *  [2] soc+g*(power-powerFcr)>socMin  =>
     *              power-powerFcr>(socMin-soc)/g => power>(socMin-soc)/g+powerFcr
     *  [3] soc+g*(power+powerFcr)<socMax  =>
     *              power+powerFcr>(socMax-soc)/g => power>(socMin-soc)/g-powerFcr
     *  [4] soc+dSocMax+g*(power-powerFcr)>socTerminalMin =>
                    power-powerFcr>(socTerminalMin-soc-dSocMax)/g => power>....+powerFcr
     */

    ConvexMultivariateRealFunction[] constraints() {
        var s=settings;
        double powerFcr=s.powerAvgFcrExtreme();
        var inequalities = new ConvexMultivariateRealFunction[N_CONSTRAINTS];
        inequalities[0] = LowerBoundConstraint.ofSingle(powerMin+powerFcr);
        inequalities[1] = UpperBoundConstraint.ofSingle(powerMax-powerFcr);
        inequalities[2] = LowerBoundConstraint.ofSingle(powerToHitSocLimit(socMin)+powerFcr);
        inequalities[3] = UpperBoundConstraint.ofSingle(powerToHitSocLimit(socMax)-powerFcr);
        double powerMinSoCTerminal=(socTerminalMin-soc-s.dSocMax(timeNew))/s.gFunction()+powerFcr;
        log.info("timeNew="+timeNew+", soc = " + soc);
        log.info("powerMinSoCTerminal = " + powerMinSoCTerminal+", dSocMax = "+s.dSocMax(timeNew));
        inequalities[4] = LowerBoundConstraint.ofSingle(powerMinSoCTerminal);
        return inequalities;
    }

    OptimizationResponse getOptimizationResponse(double[] initialPoint) throws JOptimizerException {
        defineRequest(or, initialPoint);
        optimizer.setOptimizationRequest(or);
        optimizer.optimize();
        return optimizer.getOptimizationResponse();
    }

    void defineRequest(OptimizationRequest or,double[] initialPoint) {
        or.setMaxIteration(MAX_ITERATION);
        or.setF0(costFunction()); // Set the objective function
        or.setFi(constraints());
        or.setInitialPoint(initialPoint); // Optional: initial guess
        or.setToleranceFeas(toleranceOptimization); // Tolerance on feasibility
        or.setTolerance(toleranceOptimization); // Tolerance on optimization
    }


    private double powerToHitSocLimit(Double socMinOrMax) {
        return (socMinOrMax - soc) / settings.gFunction();
    }


}
