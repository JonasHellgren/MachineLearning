package weka_tree;

import org.junit.Before;
import org.junit.Test;
import super_vised.weka_tree.TreeHelper;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class TestFilms {

    public static final String FILE = "classification_data/films.arff";
    Instances trainingData;
    String[] options;

    @Before
    public void init() {
        trainingData = TreeHelper.loadTrainingData(FILE);
        options = new String[]{"-C", "0.50", "-M", "1"};
    }

    @Test
    public void createTree() {
        J48 tree = TreeHelper.trainTheTree(trainingData, options);
        System.out.println(tree);
    }

    @Test
    public void createTreeTrainManyTimes() {

        J48 tree = null;

        for (int i = 0; i < 10000; i++) {
            tree = TreeHelper.trainTheTree(trainingData, options);
        }

        System.out.println(tree);
    }


}
