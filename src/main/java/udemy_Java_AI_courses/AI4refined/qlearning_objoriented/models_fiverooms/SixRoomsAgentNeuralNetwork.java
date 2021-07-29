package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SixRoomsAgentNeuralNetwork implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(SixRoomsAgentTabular.class);
    public State state;

    public final ReplayBuffer replayBuffer = new ReplayBuffer();
    public MultiLayerNetwork network;   //neural network memory
    INDArray inputNDSet = Nd4j.zeros(MINIBATCH_MAXSIZE,NOF_FEATURES);
    INDArray  outPutNDSet = Nd4j.zeros(MINIBATCH_MAXSIZE,NOF_OUTPUTS);
    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random = new Random();
    public final int REPLAY_BUFFER_MAXSIZE = 20;
    public static final int MINIBATCH_MAXSIZE = 10;

    public static final int SEED = 12345;  //Random number generator seed, for reproducibility
    private static final int NOF_OUTPUTS = 6;
    private static final int NOF_FEATURES = 1;
    private static final int  NOF_NEURONS_HIDDEN=5;
    private static final double L2_REGULATION=0.0001;
    private static final int BATCH_SIZE = 150;
    private static final int NOF_ITERATIONS = 5000;
    private static final int NOF_ITERATIONS_BETWEENOUTPUTS =100;
    private static final double LEARNING_RATE =0.5;

    public final double Q_INIT = 0;
    public final double GAMMA = 1.0;  // gamma discount factor
    public final double ALPHA = 0.001;  // learning rate
    public final double PROBABILITY_RANDOM_ACTION = 0.1;  //probability chossing random action
    public final int NUM_OF_EPISODES = 1000; // number of iterations


    // inner classes
    public class Experience {
        public State s;
        public int action;
        public SixRooms.StepReturn stepReturn;  //includes reward and sNew

        public Experience(State s, int action, SixRooms.StepReturn stepReturn) {
            this.s = s;
            this.action = action;
            this.stepReturn = stepReturn;
        }

        @Override
        public String toString() {
            return "Experience{" +
                    "roomNr=" + s.getDiscreteVariable("roomNumber") +
                    ", action=" + action +
                    ", reward=" + stepReturn.reward +
                    ", termState=" + stepReturn.termState +
                    ", roomNrNew=" + stepReturn.state.getDiscreteVariable("roomNumber") +
                    '}' + System.lineSeparator();
        }
    }

    public class ReplayBuffer {
        public final List<Experience> buffer = new ArrayList<>();

        public void addExperience(Experience experience) {
            if (buffer.size() >= REPLAY_BUFFER_MAXSIZE)   //remove first/oldest item in set if set is "full"
                buffer.remove(0);
            buffer.add(experience);
        }

        public List<Experience> getMiniBatch(int batchLength) {
            List<Experience> miniBatch = new ArrayList<>();

            List<Integer> indexes = IntStream.rangeClosed(0, buffer.size() - 1)
                    .boxed().collect(Collectors.toList());
            Collections.shuffle(indexes);

            for (int i = 0; i < batchLength; i++)
                miniBatch.add(buffer.get(indexes.get(i)));

            return miniBatch;
        }

        @Override
        public String toString() {
            return bufferAsString(replayBuffer.buffer);
        }

        public String bufferAsString(List<Experience> buffer) {
            return "ReplayBuffer{" + System.lineSeparator() +
                    buffer +
                    '}';
        }

    }


    public SixRoomsAgentNeuralNetwork(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        network= createNetwork();


        logger.info("Neural network based six rooms agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);

    }

    @Override
    public int chooseBestAction(State state) {
        return 0;
    }

    @Override
    public double findMaxQ(State state) {
        return 0;
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return 0;
    }

    @Override
    public void writeMemory(State oldState, Integer Action, Double value) {

    }

    @Override
    public double readMemory(State state, int Action) {
        return 0;
    }

    public  DataSetIterator createTrainingData(List<Experience> miniBatch){

        if (miniBatch.size()>MINIBATCH_MAXSIZE)
            logger.error("To big mini batch");

        for (int idxSample= 0; idxSample < miniBatch.size(); idxSample++) {
            Experience exp=miniBatch.get(idxSample);
            double in =  exp.s.getDiscreteVariable("roomNumber");
            INDArray inExample = Nd4j.create(new double[] {in}, 1, NOF_FEATURES);
            INDArray outputExample = network.output(inExample, false);
            outputExample.putScalar(0,exp.action,exp.stepReturn.reward);

            inputNDSet.putRow(idxSample,inExample);
            outPutNDSet.putRow(idxSample,outputExample);
            //System.out.println(exp);
        }

        //System.out.println(inputNDSet);
        //System.out.println(outPutNDSet);

        DataSet dataSet = new DataSet(inputNDSet, outPutNDSet);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs);
        return new ListDataSetIterator<>(listDs);
    }

    public void printNetworkOutput(State s) {
        double in=s.getDiscreteVariable("roomNumber");
        INDArray inExample = Nd4j.create(new double[] {in}, 1, NOF_FEATURES);
        INDArray outputExample = network.output(inExample, false);
        System.out.println(outputExample);
    }

    private static MultiLayerNetwork createNetwork() {
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .weightInit(WeightInit.XAVIER)
                .l2(L2_REGULATION)
                .updater(new Sgd(LEARNING_RATE))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_FEATURES).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(NOF_NEURONS_HIDDEN).nOut(NOF_NEURONS_HIDDEN)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(0, 1))
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




}
