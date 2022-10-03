package quarantine;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import quarantine.FilmClassifier;
import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.Instances;

public class TestFilms {

    public static final String FILE = "classification_data/films.arff";
    Instances trainingData;

    @Before
    public void init() {
        trainingData = FilmClassifier.loadTrainingData(FILE);
    }

    @Test
    public void createTree() {
            Id3 id3tree = FilmClassifier.trainTheTree(trainingData);
            // Print the resulted tree
            System.out.println(id3tree);
    }

    @Test
    public void createTreeTrainManyTimes() {

        Id3 id3tree=null;

        for (int i = 0; i < 10000; i++) {
            id3tree = FilmClassifier.trainTheTree(trainingData);
        }

        // Print the resulted tree
        System.out.println(id3tree);
    }


    @SneakyThrows
    @Test public void testInstance() {

        Id3 id3tree = FilmClassifier.trainTheTree(trainingData);
        // Test the tree
        Instance testInstance = FilmClassifier.prepareTestInstance(trainingData);
        int result = (int) id3tree.classifyInstance(testInstance);

        String readableResult = trainingData.attribute(3).value(result);
        System.out.println("Test data               : " + testInstance);
        System.out.println("Test data classification: " + readableResult);
    }

}
