package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_sixrooms;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SixRoomsAgentNeuralNetwork implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(SixRoomsAgentTabular.class);
    public State state;
    public int nofFits=0;

    public final ReplayBuffer replayBuffer = new ReplayBuffer();
    public MultiLayerNetwork network;   //neural network memory
    public MultiLayerNetwork networkTarget;   //neural network memory
    INDArray inputNDSet = Nd4j.zeros(MINI_BATCH_MAXSIZE,NOF_FEATURES);
    INDArray  outPutNDSet = Nd4j.zeros(MINI_BATCH_MAXSIZE,NOF_OUTPUTS);
    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random = new Random();

    public final int REPLAY_BUFFER_MAXSIZE = 1000;
    public static final int MINI_BATCH_MAXSIZE = 10;
    public static final int SEED = 12345;  //Random number generator seed, for reproducibility
    private static final int NOF_OUTPUTS = 6;
    private static final int NOF_FEATURES = 1;
    private static final int  NOF_NEURONS_HIDDEN=10;
    private static final double L2_REGULATION=0.000;
    private static final double LEARNING_RATE =0.1;


    public double GAMMA = 1.0;  // gamma discount factor
    public final double ALPHA = 0.99;  // learning rate
    public final double PROBABILITY_RANDOM_ACTION_START = 0.9;  //probability choosing random action
    public final double PROBABILITY_RANDOM_ACTION_END = 0.1;
    public final int NUM_OF_EPISODES = 900; // number of iterations
    private static final int NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE=50;

    public SixRoomsAgentNeuralNetwork(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        network= createNetwork();
        networkTarget= createNetwork();

        logger.info("Neural network based six rooms agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);
    }

    public State getState() {
        return state;
    }

    @Override
    public int chooseBestAction(State state) {
        INDArray outFromNetwork= calcOutFromNetwork(state, network);
        return outFromNetwork.argMax().getInt();
    }

    @Override
    public double findMaxQ(State state) {
        INDArray outFromNetwork= calcOutFromNetwork(state, network);
         return outFromNetwork.max().getDouble();
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public void writeMemory(State oldState, Integer Action, Double value) {
    //not valid for NN
    }

    @Override
    public double readMemory(State state, int action) {
        INDArray outFromNetwork= calcOutFromNetwork(state, network);
        return outFromNetwork.getDouble(action);
    }

    public double findMaxQTargetNetwork(State state) {
        INDArray outFromNetwork= calcOutFromNetwork(state, networkTarget);
        return outFromNetwork.max().getDouble();
    }

    public  DataSetIterator createTrainingData(List<Experience> miniBatch){

        if (miniBatch.size()> MINI_BATCH_MAXSIZE)
            logger.error("To big mini batch");

        for (int idxSample= 0; idxSample < miniBatch.size(); idxSample++) {
            Experience exp=miniBatch.get(idxSample);
            INDArray outFromNetwork= calcOutFromNetwork(exp.s, network);

            double maxQ = findMaxQTargetNetwork(exp.stepReturn.state)*1;
            double qOld = readMemory(exp.s, exp.action);
            double qNew = qOld + ALPHA * (exp.stepReturn.reward + GAMMA * maxQ - qOld);
            double y=exp.stepReturn.termState ? exp.stepReturn.reward : qNew;
            outFromNetwork.putScalar(0,exp.action,y*1+exp.s.getDiscreteVariable("roomNumber")*0.0);

            INDArray inputNetwork = getStateAsNetworkInput(exp.s);
            inputNDSet.putRow(idxSample,inputNetwork);
            outPutNDSet.putRow(idxSample,outFromNetwork);
            //System.out.println(exp);
        }

        nofFits++;
        if (nofFits % NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE == 0)
            networkTarget.setParams(network.params());


        //System.out.println(inputNDSet);
        //System.out.println(outPutNDSet);

        DataSet dataSet = new DataSet(inputNDSet, outPutNDSet);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs);
        return new ListDataSetIterator<>(listDs);
    }



    public INDArray calcOutFromNetwork(State state,MultiLayerNetwork network) {
        INDArray inputNetwork = getStateAsNetworkInput(state);
        return network.output(inputNetwork, false);
    }

    private INDArray getStateAsNetworkInput(State state) {  //TODO make generic
        double in =  state.getDiscreteVariable("roomNumber");
        in=in*1;  //TODO clean up normalization
        return Nd4j.create(new double[]{in}, 1, NOF_FEATURES);
    }

    private  MultiLayerNetwork createNetwork() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .weightInit(WeightInit.XAVIER)
                .l2(L2_REGULATION)
                //.updater(new Sgd(LEARNING_RATE))
                .updater(new Nesterovs(LEARNING_RATE, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.SIGMOID)
                        //.weightInit(new UniformDistribution(0, 1))
                        //.weightInit(WeightInit.XAVIER)
                        //.biasInit()
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.SIGMOID)
                        //.weightInit(new UniformDistribution(0, 1))
                        //.weightInit(WeightInit.XAVIER)
                        //.biasInit(0)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(NOF_NEURONS_HIDDEN).nOut(NOF_OUTPUTS).build())
                .backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(configuration);
        network.init();
        //network.setListeners(new ScoreIterationListener(NOF_ITERATIONS_BETWEENOUTPUTS));

        return network;
    }


    public void showPolicy(SixRooms env) {
        // we consider every single state as a starting state
        // until we find the terminal state: we walk according to best action

        System.out.println("Policy for every state -----------------------------");
        for(int starRoomNr=0; starRoomNr<envParams.nofStates; starRoomNr++) {
            StepReturn stepReturn;
            state.setVariable("roomNumber", starRoomNr);
            System.out.print("Policy: " + state.getDiscreteVariable("roomNumber"));
            int nofSteps=0;
            while (!env.isTerminalState(state) & nofSteps<10) {
                int bestA = chooseBestAction(state);
                stepReturn=env.step(bestA,state);
                state.copyState(stepReturn.state);
                System.out.print(" -> " + state.getDiscreteVariable("roomNumber"));
                nofSteps++;
            }
            System.out.println();
        }
    }


}
