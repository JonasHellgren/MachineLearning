package java_ai_gym.models_sixrooms;

import java_ai_gym.models_common.*;
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
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.logging.Logger;

public class SixRoomsAgentNeuralNetwork extends AgentNeuralNetwork {

    private static final Logger logger = Logger.getLogger(SixRoomsAgentNeuralNetwork.class.getName());

    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters

    public  final double RB_EPS=0.1;
    public  final double RB_ALP=0.0;  //0 <=> uniform distribution from bellman error for mini batch selection
    public  final double BETA0=0.1;
    public final double  BE_ERROR_INIT=0;

    public SixRoomsAgentNeuralNetwork(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createDiscreteVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        this.REPLAY_BUFFER_SIZE = 100;
        replayBuffer = new ReplayBuffer(RB_EPS,RB_ALP,BETA0,REPLAY_BUFFER_SIZE);

        this.NOF_OUTPUTS = 6;
        this.NOF_FEATURES = 1;
        this.NOF_NEURONS_HIDDEN=20;
        if (isAnyNetworkSizeFieldNull())
            logger.warning("Some network size field is not set, i.e. null");
        network= createNetwork();
        networkTarget= createNetwork();

        this.MINI_BATCH_SIZE=10;
        this.L2_REGULATION=0.000000;
        this.LEARNING_RATE =0.0001;
        this.MOMENTUM=0.1;

        this.GAMMA = 0.0; //1.0;  // gamma discount factor
        this.ALPHA = 0.9;  // learning rate
        this.PROBABILITY_RANDOM_ACTION_START = 0.9;  //probability choosing random action
        this.PROBABILITY_RANDOM_ACTION_END = 0.1;
        this.NUM_OF_EPISODES = 1500; // number of iterations
        this.NUM_OF_EPOCHS=10;  //nof fits per mini batch
        this.NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE =10;
        this.NOF_STEPS_BETWEEN_FITS=1;

        if (isAnyFieldNull())
            logger.warning("Some field in AgentNeuralNetwork is not set, i.e. null");
        else
            logger.info("Neural network based six rooms agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);

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
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .build())
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
    protected INDArray setNetworkInput(State state, EnvironmentParametersAbstract envParams) {
        int nofRooms=envParams.discreteActionsSpace.size();
        double[] varValuesAsArray = {
                (double) state.getDiscreteVariable("roomNumber")/(double) nofRooms
        };

        if (varValuesAsArray.length!=NOF_FEATURES)
            logger.warning("Wrong number of network inputs");


        return Nd4j.create(varValuesAsArray, 1, NOF_FEATURES);
    }

}
