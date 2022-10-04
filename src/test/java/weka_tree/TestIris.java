package weka_tree;

import org.junit.Before;
import org.junit.Test;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class TestIris {

    public static final String FILE = "classification_data/iris.arff";
    String[] options;
    Instances trainingData;

    @Before
    public void init() {
        trainingData = TreeHelper.loadTrainingData(FILE);
        options = new String[4];
        options = new String[]{"-C", "0.25", "-M", "30"};
    }

    @Test
    public void createTree() {
        J48 j48Tree = TreeHelper.trainTheTree(trainingData, options);
        System.out.println(j48Tree);
    }

    @Test
    public void createTreeTrainManyTimes() {

        J48 j48Tree = null;

        for (int i = 0; i < 1000; i++) {
            j48Tree = TreeHelper.trainTheTree(trainingData, options);
        }

        System.out.println(j48Tree);
    }


}
