package weka_tree;

import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * http://technobium.com/decision-trees-explained-using-weka/
 */

public class FilmClassifier {

    static Instances loadTrainingData(String fileName) {
        BufferedReader reader = null;
        Instances trainingData = null;
        try {
            // Read the training data
            reader = new BufferedReader(new FileReader(fileName));
            trainingData = new Instances(reader);

            // Setting class attribute
            trainingData.setClassIndex(trainingData.numAttributes() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return trainingData;
    }

    static Id3 trainTheTree(Instances trainingData) {
        Id3 id3tree = new Id3();

        String[] options = new String[1];
        // Use unpruned tree.
        options[0] = "-U";

        try {
            id3tree.setOptions(options);
            id3tree.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id3tree;
    }

    static Instance prepareTestInstance(Instances trainingData) {
        Instance instance = new Instance(3);
        instance.setDataset(trainingData);

        instance.setValue(trainingData.attribute(0), "Europe");
        instance.setValue(trainingData.attribute(1), "no");
        instance.setValue(trainingData.attribute(2), "comedy");

        return instance;
    }

}
