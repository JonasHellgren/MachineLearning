package weka_tree;

import org.junit.Before;
import org.junit.Test;
import weka.classifiers.trees.J48;
import weka.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestIncome {

    Instances trainingData;
    String[] options;

    @Before
    public void init() {

        ArrayList<Attribute> attributes = new ArrayList<>();
        Attribute age = new Attribute("age");
        Attribute education = new Attribute("education",Arrays.asList("bachelor","highSchool","doctorate"));
        Attribute status = new Attribute("status",Arrays.asList("neverMarried","married","divorced"));
        Attribute occupation = new Attribute("occupation",Arrays.asList("transport","pro","agri","army"));
        Attribute incomeCategory = new Attribute("incomeCategory",Arrays.asList("low","medium","high"));

        attributes.add(age);
        attributes.add(education);
        attributes.add(status);
        attributes.add(occupation);
        attributes.add(incomeCategory);

        trainingData = new Instances("whatever", attributes, 0);

        Instance i1 = new DenseInstance(5);
        i1.setValue(age, 39);
        i1.setValue(education, "bachelor");
        i1.setValue(status, "neverMarried");
        i1.setValue(occupation, "transport");
        i1.setValue(incomeCategory, "medium");
        trainingData.add(i1);

        Instance i2 = new DenseInstance(5);
        i2.setValue(age, 50);
        i2.setValue(education, "bachelor");
        i2.setValue(status, "married");
        i2.setValue(occupation, "pro");
        i2.setValue(incomeCategory, "medium");
        trainingData.add(i2);

        Instance i3 = new DenseInstance(5);
        i3.setValue(age, 26);
        i3.setValue(education, "highSchool");
        i3.setValue(status, "neverMarried");
        i3.setValue(occupation, "agri");
        i3.setValue(incomeCategory, "low");
        trainingData.add(i3);



        options=new String[] { "-C", "0.50", "-M", "1" };
    }

    @Test
    public void showTrainingData() {
        System.out.println("trainingData = " + trainingData);
    }

    @Test
    public void createTree() {

        J48 tree = TreeHelper.trainTheTree(trainingData,options);
        // Print the resulted tree
        System.out.println(tree);
    }

}
