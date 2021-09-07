package java_ai_gym.models_mountaincar;

import java_ai_gym.models_common.AgentNeuralNetwork;
import java_ai_gym.models_common.EnvironmentParametersAbstract;
import java_ai_gym.models_common.ReplayBuffer;
import java_ai_gym.models_common.State;
import java_ai_gym.models_sixrooms.SixRoomsAgentNeuralNetwork;
import java_ai_gym.swing.ScaleLinear;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.logging.Logger;

//https://sudonull.com/post/31369-Mountain-Car-we-solve-the-classic-problem-with-reinforcement-training-Petersburg-Tower-Blog

/***
 * The parameter PRECISION defines how extensive/precise the training shall be. The higher, the more precise
 * , but more time demanding, training. Recommended values are {1,10}
 */

public class MountainCarAgentNeuralNetwork extends AgentNeuralNetwork {

    private static final Logger logger = Logger.getLogger(SixRoomsAgentNeuralNetwork.class.getName());

    public final MountainCar.EnvironmentParameters envParams;  //reference to environment parameters

    ScaleLinear posScaler;
    ScaleLinear velScaler;

    final double  PRECISION=1;

    public MountainCarAgentNeuralNetwork(MountainCar.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        state.createDiscreteVariable("nofSteps", 0);
        state.createContinuousVariable("position", envParams.POSITION_AT_MIN_HEIGHT);
        state.createContinuousVariable("velocity", 0.0);

        createReplayBuffer();
        createInputNormalizers(envParams);
        createNetworks(envParams);
        defineLearningParameters();
        showConstructorLogMessage();

    }

    private void createInputNormalizers(MountainCar.EnvironmentParameters envParams) {
        posScaler=new ScaleLinear(envParams.MIN_POSITION, envParams.MAX_POSITION,
                -1,1,false,0);

        velScaler=new ScaleLinear(-envParams.MAX_SPEED, envParams.MAX_SPEED,
                -1,1,false,0);
    }

    private void createReplayBuffer() {
        this.REPLAY_BUFFER_SIZE = 2000;
        this.MINI_BATCH_SIZE = (int) (30*Math.sqrt(PRECISION));
        this.RB_EPS = 0.1;
        this.RB_ALP = 0.3;  //0 <=> uniform distribution from bellman error for mini batch selection
        this.BETA0 = 0.1;
        this.BE_ERROR_INIT=0;  //do not favor new comers
        replayBuffer = new ReplayBuffer(RB_EPS, RB_ALP, BETA0, REPLAY_BUFFER_SIZE);
    }


    private void defineLearningParameters() {
        this.GAMMA = 0.99;  // gamma discount factor
        this.PROBABILITY_RANDOM_ACTION_START = 0.5;
        this.PROBABILITY_RANDOM_ACTION_END = 0.1;
        this.NUM_OF_EPISODES = (int) (100*PRECISION);
        this.NUM_OF_EPOCHS=1;
        this.NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE = (int) (100*Math.sqrt(PRECISION));
        this.NOF_STEPS_BETWEEN_FITS = 1;
    }

    private void createNetworks(MountainCar.EnvironmentParameters envParams) {
        this.NOF_OUTPUTS = envParams.NOF_ACTIONS;
        this.NOF_FEATURES = state.nofContinuousVariables();
        this.NOF_NEURONS_HIDDEN = 50;
        this.L2_REGULATION = 1e-8;
        this.LEARNING_RATE_START =1e-2/PRECISION;
        this.LEARNING_RATE_END =1e-4/PRECISION;
        this.MOMENTUM = 0.8;

        if (isAnyNetworkSizeFieldNull())
            logger.warning("Some network size field is not set, i.e. null");
        network = createNetwork();
        networkTarget = createNetwork();
    }


    private MultiLayerNetwork createNetwork() {

        logger.info("Creating network, initital learning rate:"+this.LEARNING_RATE_START+
                ", L2_REGULATION:"+L2_REGULATION+", NOF_NEURONS_HIDDEN:"+NOF_NEURONS_HIDDEN);

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .weightInit(WeightInit.XAVIER)
                .l2(L2_REGULATION)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                //.updater(new Sgd(LEARNING_RATE_START))
                .updater(new Nesterovs(LEARNING_RATE_START, MOMENTUM))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .build())
               // .layer(2, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                //        .activation(Activation.TANH)
                 //       .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(NOF_NEURONS_HIDDEN).nOut(NOF_OUTPUTS).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();
        //network.setListeners(new PerformanceListener(100));
        //network.setListeners(new ScoreIterationListener(NOF_ITERATIONS_BETWEENOUTPUTS));

        return network;
    }

    @Override
    public INDArray setNetworkInput(State state, EnvironmentParametersAbstract envParams) {
        double[] varValuesAsArray = {
                // state.getContinuousVariable("position"),
                posScaler.calcOutDouble(state.getContinuousVariable("position")),
               //  state.getContinuousVariable("velocity")*10
                velScaler.calcOutDouble(state.getContinuousVariable("velocity"))
        };


        if (varValuesAsArray.length != NOF_FEATURES)
            logger.warning("Wrong number of network inputs");

        return Nd4j.create(varValuesAsArray, 1, NOF_FEATURES);
    }

    public void printPositionAndVelocity() {
        System.out.println(
                "position:" + state.getContinuousVariable("position") +
                        ", velocity:" + state.getContinuousVariable("velocity"));
    }

    public void printQsa(EnvironmentParametersAbstract envParams) {
        for (int aChosen : envParams.discreteActionsSpace)
            System.out.print("aChosen: " + aChosen + ", Q:" + readMemory(state, aChosen, envParams) + "; ");
        System.out.println();
    }


}
