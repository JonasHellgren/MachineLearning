package java_ai_gym.models_mountaincar;

import java_ai_gym.models_common.AgentNeuralNetwork;
import java_ai_gym.models_common.EnvironmentParametersAbstract;
import java_ai_gym.models_common.ReplayBuffer;
import java_ai_gym.models_common.State;
import java_ai_gym.models_sixrooms.SixRooms;
import java_ai_gym.models_sixrooms.SixRoomsAgentNeuralNetwork;
import org.deeplearning4j.nn.conf.BackpropType;
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

public class MountainCarAgentNeuralNetwork extends AgentNeuralNetwork {

    private static final Logger logger = Logger.getLogger(SixRoomsAgentNeuralNetwork.class.getName());

    public final MountainCar.EnvironmentParameters envParams;  //reference to environment parameters

    public final double RB_EPS = 0.1;
    public final double RB_ALP = 0.0;  //0 <=> uniform distribution from bellman error for mini batch selection
    public final double BETA0 = 0.1;
    public final int REPLAY_BUFFER_SIZE = 100;

    public MountainCarAgentNeuralNetwork(MountainCar.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        state.createDiscreteVariable("nofSteps", 0);
        state.createContinuousVariable("position", envParams.startPosition);
        state.createContinuousVariable("velocity", envParams.startPosition);

        replayBuffer = new ReplayBuffer(RB_EPS, RB_ALP, BETA0, REPLAY_BUFFER_SIZE);

        this.NOF_OUTPUTS = envParams.NOF_ACTIONS;
        this.NOF_FEATURES = state.nofContinuousVariables();
        this.NOF_NEURONS_HIDDEN = 32;
        if (isAnyNetworkSizeFieldNull())
            logger.warning("Some network size field is not set, i.e. null");
        network = createNetwork();
        networkTarget = createNetwork();

        this.MINI_BATCH_SIZE = 5;
        this.L2_REGULATION = 0.00000;
        this.LEARNING_RATE = 0.001;
        this.MOMENTUM = 0.8;

        this.GAMMA = 0.99;  // gamma discount factor
        this.ALPHA = 1.0;  // learning rate
        this.PROBABILITY_RANDOM_ACTION_START = 0.05;  //probability choosing random action
        this.PROBABILITY_RANDOM_ACTION_END = 0.01;
        this.NUM_OF_EPISODES = 200; // number of iterations
        this.NUM_OF_EPOCHS=5;  //nof fits per mini batch
        this.NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE = 10;
        this.NOF_STEPS_BETWEEN_FITS = 5;

        if (isAnyFieldNull())
            logger.warning("Some field in AgentNeuralNetwork is not set, i.e. null");
        else
            logger.info("Neural network based MountainCar agent created. " + "nofStates:" + 3 + ", nofActions:" + envParams.NOF_ACTIONS);

    }



    private MultiLayerNetwork createNetwork() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .weightInit(WeightInit.XAVIER)
                .l2(L2_REGULATION)
                //.updater(new Sgd(LEARNING_RATE))
                .updater(new Nesterovs(LEARNING_RATE, MOMENTUM))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.RELU)
                        .build())
               // .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                 //       .activation(Activation.RELU)
                 //       .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
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
                 state.getContinuousVariable("position"),
                 state.getContinuousVariable("velocity")*10
        };

        if (varValuesAsArray.length != NOF_FEATURES)
            logger.warning("Wrong number of network inputs");

        return Nd4j.create(varValuesAsArray, 1, NOF_FEATURES);
    }

}
