package java_ai_gym.models_common;


import java_ai_gym.models_sixrooms.SixRooms;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/***
 * Following parameters are especially critical: MINI_BATCH_SIZE, NOF_NEURONS_HIDDEN, LEARNING_RATE, RB_ALP
 */

public abstract class AgentNeuralNetwork implements Learnable {

    private final static Logger logger = Logger.getLogger(AgentNeuralNetwork.class.getName());
  //  private static final Logger logger = LoggerFactory.getLogger(AgentNeuralNetwork.class);
    public State state;
    public  ReplayBuffer replayBuffer;
    public MultiLayerNetwork network;   //neural network memory
    public MultiLayerNetwork networkTarget;   //neural network memory

    private final Random random = new Random();

    public int nofFits=0;
    double bellmanErrorStep;
    public List<Double> bellmanErrorList=new ArrayList<>();

    protected  int SEED = 12345;  //Random number generator seed, for reproducibility

    protected  Integer NOF_OUTPUTS ;
    protected  Integer NOF_FEATURES ;
    protected  Integer  NOF_NEURONS_HIDDEN;

    public  int MINI_BATCH_SIZE = 30;
    protected  double L2_REGULATION=0.000001;
    protected  double LEARNING_RATE =0.1;
    protected  double MOMENTUM=0.8;

    protected double GAMMA = 1.0;  // gamma discount factor
    protected  double ALPHA = 1.0;  // learning rate in Q-learning update
    protected  double PROBABILITY_RANDOM_ACTION_START = 0.9;  //probability choosing random action
    protected  double PROBABILITY_RANDOM_ACTION_END = 0.1;
    public  int NUM_OF_EPISODES = 1000; // number of iterations
    protected int NOF_FITS_BETWEEN_TARGET_NETWORK_UPDATE=50;

    protected abstract  INDArray setNetworkInput(State state,EnvironmentParametersAbstract envParams);

    @Override
    public State getState() {
        return state;
    }

    @Override
    public int chooseBestAction(State state,EnvironmentParametersAbstract envParams) {
        INDArray outFromNetwork = calcOutFromNetwork(state, network,envParams);
        return outFromNetwork.argMax().getInt();
    }

    @Override
    public double findMaxQ(State state,EnvironmentParametersAbstract envParams) {
        INDArray inputNetwork = setNetworkInput(state, envParams);
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
        return outFromNetwork.max().getDouble();
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public int chooseAction(double fractionEpisodesFinished,EnvironmentParametersAbstract envParams) {
        double probRandAction=PROBABILITY_RANDOM_ACTION_START+
                (PROBABILITY_RANDOM_ACTION_END-PROBABILITY_RANDOM_ACTION_START)*fractionEpisodesFinished;

        return (Math.random() < probRandAction) ?
                chooseRandomAction(envParams.discreteActionsSpace) :
                chooseBestAction(state,envParams);
    }

    @Override
    public void writeMemory(State oldState, Integer Action, Double value,EnvironmentParametersAbstract envParams) {
        //not valid for NN
    }

    @Override
    public double readMemory(State state, int action,EnvironmentParametersAbstract envParams) {
        INDArray inputNetwork = setNetworkInput(state, envParams);
        return readMemory(inputNetwork,  action);
    }



    public double readMemory(INDArray inputNetwork, int action) {
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
        return outFromNetwork.getDouble(action);
    }

    private double findMaxQTargetNetwork(State state,EnvironmentParametersAbstract envParams) {
        INDArray inputNetwork = setNetworkInput(state, envParams);
        INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, networkTarget);
        return outFromNetwork.max().getDouble();
    }

    public INDArray calcOutFromNetwork(State state,MultiLayerNetwork network,EnvironmentParametersAbstract envParams) {
        INDArray inputNetwork = setNetworkInput(state, envParams);
        return network.output(inputNetwork, false);
    }

    public INDArray calcOutFromNetwork(INDArray inputNetwork,MultiLayerNetwork network) {
        return network.output(inputNetwork, false);
    }


    public DataSetIterator createTrainingData(List<Experience> miniBatch,EnvironmentParametersAbstract envParams) {

        INDArray inputNDSet = Nd4j.zeros(MINI_BATCH_SIZE,NOF_FEATURES);
        INDArray  outPutNDSet = Nd4j.zeros(MINI_BATCH_SIZE,NOF_OUTPUTS);

        if (miniBatch.size() > MINI_BATCH_SIZE)
            logger.warning("To big mini batch");

        for (int idxSample= 0; idxSample < miniBatch.size(); idxSample++) {
            Experience exp=miniBatch.get(idxSample);
            INDArray inputNetwork = setNetworkInput(exp.s, envParams);
            INDArray outFromNetwork= calcOutFromNetwork(inputNetwork, network);
            outFromNetwork = modifyNetworkOut(exp, inputNetwork, outFromNetwork,envParams);
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
    private INDArray modifyNetworkOut(Experience exp, INDArray inputNetwork, INDArray outFromNetwork,EnvironmentParametersAbstract envParams) {
        double qOld = readMemory(inputNetwork, exp.action);
        bellmanErrorStep= exp.stepReturn.termState ?
                exp.stepReturn.reward - qOld:
                exp.stepReturn.reward + GAMMA * findMaxQTargetNetwork(exp.stepReturn.state,envParams) - qOld;
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

    protected  boolean isAnyNetworkSizeFieldNull() {
        return (NOF_OUTPUTS==null | NOF_FEATURES==null | NOF_NEURONS_HIDDEN==null);
    }

    protected  boolean isAnyFieldNull() {
        return (state==null | replayBuffer==null | network==null | networkTarget==null);

    }



}