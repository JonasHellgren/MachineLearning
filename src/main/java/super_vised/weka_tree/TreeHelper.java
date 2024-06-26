package super_vised.weka_tree;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TreeHelper {

    public static Instances loadTrainingData(String fileName) {
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

    /**
     * “-C 0.25 -M 1” options are selected for the algorithm. There options represent:
     * confidenceFactor — The confidence factor used for pruning (smaller values incur more pruning).
     * minNumObj — The minimum number of instances per leaf.
     */

    public static J48 trainTheTree(Instances trainingData, String[] options) {
        J48 id3tree = new J48();

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
