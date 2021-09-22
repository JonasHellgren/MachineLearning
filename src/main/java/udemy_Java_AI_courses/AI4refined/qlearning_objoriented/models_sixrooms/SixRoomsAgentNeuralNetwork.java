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
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Experience;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.ReplayBuffer;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/***
 * Following paramaters are especially critical: MINI_BATCH_SIZE, NOF_NEURONS_HIDDEN, LEARNING_RATE, RB_ALP
 */

public class SixRoomsAgentNeuralNetwork implements Agent {

    private static final Logger logger = Logger.getLogger(SixRoomsAgentTabular.class.getName());
    public State state;
    public int nofFits=0;

    public final ReplayBuffer replayBuffer = new ReplayBuffer();
    public MultiLayerNetwork network;   //neural network memory
    public MultiLayerNetwork networkTarget;   //neural network memory

    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random = new Random();
    double bellmanErrorStep;
    public List<Double> bellmanErrorList=new ArrayList<>();

    public final int REPLAY_BUFFER_SIZE = 100;
    public static final int MINI_BATCH_SIZE = 30;
    public static final int SEED = 12345;  //Random number generator seed, for reproducibility
    private static final int NOF_OUTPUTS = 6;
    private static final int NOF_FEATURES = 1;
    private static final int  NOF_NEURONS_HIDDEN=10;
    private static final double L2_REGULATION=0.000001;
    private static final double LEARNING_RATE =0.1;
    private static final double MOMENTUM=0.8;
    public  final double RB_EPS=0.1;
    public  final double RB_ALP=0.9;  //0 <=> uniform distribution from bellman error for minibatch selection
    public  final double BETA0=0.1;

    public double GAMMA = 1.0;  // gamma discount factor
    public final double ALPHA = 1.0;  // learning rate
    public final double PROBABILITY_RANDOM_ACTION_START = 0.9;  //probability choosing random action
    public final double PROBABILITY_RANDOM_ACTION_END = 0.1;
    public final int NUM_OF_EPISODES = 1000; // number of iterations
    private static final int NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE=50;

    public SixRoomsAgentNeuralNetwork(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createDiscreteVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        network= createNetwork();
        networkTarget= createNetwork();

        logger.info("Neural network based six rooms agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);
    }

    public State getState() {
        return state;
    }

    @Override
    public double getRB_EPS() {
        return RB_EPS;
    }

    @Override
    public double getRB_ALP() {
        return RB_ALP;
    }

    @Override
    public double getBETA0() {
        return BETA0;
    }

    @Override
    public int chooseBestAction(State state) {
        INDArray outFromNetwork= calcOutFromNetwork(state, network);
        return outFromNetwork.argMax().getInt();
    }

    @Override
    public double findMaxQ(State state) {
        INDArray inputNetwork = state.getStateVariablesAsNetworkInput(envParams);
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
        return outFromNetwork.max().getDouble();
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public int chooseAction(double fractionEpisodesFinished) {
        double probRandAction=PROBABILITY_RANDOM_ACTION_START+
                (PROBABILITY_RANDOM_ACTION_END-PROBABILITY_RANDOM_ACTION_START)*fractionEpisodesFinished;

        return (Math.random() < probRandAction) ?
                chooseRandomAction(envParams.discreteActionsSpace) :
                chooseBestAction(state);
    }

    @Override
    public void writeMemory(State oldState, Integer Action, Double value) {
        //not valid for NN
    }

    @Override
    public double readMemory(State state, int action) {
        INDArray inputNetwork = state.getStateVariablesAsNetworkInput(envParams);
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
        return outFromNetwork.getDouble(action);
    }

    public double readMemory(INDArray inputNetwork, int action) {
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
        return outFromNetwork.getDouble(action);
    }

    private double findMaxQTargetNetwork(State state) {
        INDArray inputNetwork = state.getStateVariablesAsNetworkInput(envParams);
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, networkTarget);
        return outFromNetwork.max().getDouble();
    }

    public INDArray calcOutFromNetwork(State state,MultiLayerNetwork network) {
        INDArray inputNetwork = state.getStateVariablesAsNetworkInput(envParams);
        return network.output(inputNetwork, false);
    }

    public INDArray calcOutFromNetwork(INDArray inputNetwork,MultiLayerNetwork network) {
        return network.output(inputNetwork, false);
    }


    public DataSetIterator createTrainingData(List<Experience> miniBatch) {

        INDArray inputNDSet = Nd4j.zeros(MINI_BATCH_SIZE,NOF_FEATURES);
        INDArray  outPutNDSet = Nd4j.zeros(MINI_BATCH_SIZE,NOF_OUTPUTS);

        if (miniBatch.size() > MINI_BATCH_SIZE)
            logger.warning("To big mini batch");

        for (int idxSample= 0; idxSample < miniBatch.size(); idxSample++) {
            Experience exp=miniBatch.get(idxSample);
            INDArray inputNetwork = exp.s.getStateVariablesAsNetworkInput(envParams);
            INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
            outFromNetwork = modifyNetworkOut(exp, inputNetwork, outFromNetwork);
            changeBellmanErrorVariableInBufferItem(exp);

            //System.out.println("priority:"+exp.pExpRep.priority+", bellmanErrorStep:"+bellmanErrorStep);

            addTrainingExample(inputNDSet, outPutNDSet, idxSample, inputNetwork, outFromNetwork);
            bellmanErrorList.add(bellmanErrorStep);
        }

        maybeUpdateTargetNetwork();
        DataSet dataSet = new DataSet(inputNDSet, outPutNDSet);
        List<DataSet> listDs = dataSet.asList();

        return new ListDataSetIterator<>(listDs);
    }

    //To enable prioritized experience replay items in full experience buffer are modified
    //possible because mini batch item exp is reference to item in buffer
    private void changeBellmanErrorVariableInBufferItem(Experience exp) {
        exp.pExpRep.beError=Math.abs(bellmanErrorStep);
    }

    private void maybeUpdateTargetNetwork() {
        nofFits++;
        if (nofFits % NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE == 0)
            networkTarget.setParams(network.params());
    }

    private void addTrainingExample(INDArray inputNDSet, INDArray outPutNDSet, int idxSample, INDArray inputNetwork, INDArray outFromNetwork) {
        inputNDSet.putRow(idxSample, inputNetwork);
        outPutNDSet.putRow(idxSample, outFromNetwork);
    }

    //Network output y for state s defined as
    //y=    q(s)*(1- alpha )+alpha*( r – q(s) )   (term state)
    //      q(s)*(1- alpha )+alpha*( r+gama*maxQ(s’)-q(s))   (not term state)
    //alpha=1 =>
    //y=    r  (term state)
    //      r+gama*maxQ(s’)-q(s)  (not term state)
    //skipped ..*(1- alpha ), made learning less stable
    private INDArray modifyNetworkOut(Experience exp, INDArray inputNetwork, INDArray outFromNetwork) {
        double qOld = readMemory(inputNetwork, exp.action);
        bellmanErrorStep= exp.stepReturn.termState ?
                exp.stepReturn.reward - qOld:
                exp.stepReturn.reward + GAMMA * findMaxQTargetNetwork(exp.stepReturn.state) - qOld;
        double alpha=exp.pExpRep.w*ALPHA;
        double y=qOld*1 + alpha * bellmanErrorStep;
        outFromNetwork.putScalar(0, exp.action,y);
        return outFromNetwork;
    }

    public double getBellmanErrorAverage(int nofSteps) {

        if (bellmanErrorList.size()==0)
            return 1;

        double sumBellmanError=0;
        int j;
        for (j = 0; j < nofSteps; j++) {
            int idxListPos=bellmanErrorList.size()-j-1;
            if (idxListPos>=0)
                sumBellmanError=sumBellmanError+Math.abs(bellmanErrorList.get(idxListPos));
            else
                break;
        }
        return sumBellmanError/(j+1);
    }


    private  MultiLayerNetwork createNetwork() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .weightInit(WeightInit.XAVIER)
                .l2(L2_REGULATION)
                //.updater(new Sgd(LEARNING_RATE))
                .updater(new Nesterovs(LEARNING_RATE, MOMENTUM))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.SIGMOID)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.SIGMOID)
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


}

