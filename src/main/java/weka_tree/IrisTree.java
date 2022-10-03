package weka_tree;

import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IrisTree {

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

    static J48 trainTheTree(Instances trainingData) {
        J48 id3tree = new J48();

        String[] options = new String[4];
        options[0] = "-C";
        options[1] = "0.25";
        options[2] = "-M";
        options[3] = "30";

        try {
            id3tree.setOptions(options);
            trainingData.setClassIndex(trainingData.numAttributes()-1);
            id3tree.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id3tree;
    }

}
