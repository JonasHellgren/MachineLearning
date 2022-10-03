package weka_tree;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class TestFilms {

    public static final String FILE = "classification_data/films.arff";
    Instances trainingData;
    String[] options;

    @Before
    public void init() {
        trainingData = TreeHelper.loadTrainingData(FILE);
        options=new String[] { "-C", "0.50", "-M", "1" };
    }

    @Test
    public void createTree() {

            J48 tree = TreeHelper.trainTheTree(trainingData,options);
            // Print the resulted tree
            System.out.println(tree);
    }

    @Test
    public void createTreeTrainManyTimes() {

        J48 tree=null;

        for (int i = 0; i < 10000; i++) {
            tree = TreeHelper.trainTheTree(trainingData,options);
        }

        // Print the resulted tree
        System.out.println(tree);
    }


    @SneakyThrows

    @Test public void testInstance() {

        J48 tree = TreeHelper.trainTheTree(trainingData,options);
        // Test the tree
       /*
        Instance testInstance = FilmTree.prepareTestInstance(trainingData);
        int result = (int) tree.classifyInstance(testInstance);

        String readableResult = trainingData.attribute(3).value(result);
        System.out.println("Test data               : " + testInstance);
        System.out.println("Test data classification: " + readableResult);

        */
    }

}
