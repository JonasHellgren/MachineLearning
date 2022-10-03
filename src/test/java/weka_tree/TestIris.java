package weka_tree;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class TestIris {

    public static final String FILE = "classification_data/iris.arff";
    Instances trainingData;

    @Before
    public void init() {
        trainingData = IrisTree.loadTrainingData(FILE);
    }

    @Test
    public void createTree() {
            J48 j48Tree = IrisTree.trainTheTree(trainingData);
            System.out.println(j48Tree);
    }

    @Test
    public void createTreeTrainManyTimes() {

        J48 j48Tree=null;

        for (int i = 0; i < 1000; i++) {
            j48Tree = IrisTree.trainTheTree(trainingData);
        }

        System.out.println(j48Tree);
    }

/*
    @SneakyThrows
    @Test public void testInstance() {

        J48 j48Tree = IrisTree.trainTheTree(trainingData);
        // Test the tree
        Instance testInstance = IrisTree.prepareTestInstance(trainingData);
        int result = (int) j48Tree.classifyInstance(testInstance);

        String readableResult = trainingData.attribute(3).value(result);
        System.out.println("Test data               : " + testInstance);
        System.out.println("Test data classification: " + readableResult);
    }


 */
}
