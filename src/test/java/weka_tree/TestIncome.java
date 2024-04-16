package weka_tree;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import super_vised.weka_tree.TreeHelper;
import weka.classifiers.trees.J48;
import weka.core.*;

import java.util.ArrayList;
import java.util.Arrays;

public class TestIncome {

    public static final int NUM_ATTRIBUTES = 5;
    public static final String DEFAULT_CATEGORY = "low";
    Instances trainingData;
    String[] options;
    Attribute age;
    Attribute education;
    Attribute status;
    Attribute occupation;
    Attribute incomeCategory;

    @Before
    public void init() {

        ArrayList<Attribute> attributes = new ArrayList<>();
        age = new Attribute("age");
        education = new Attribute("education", Arrays.asList("bachelor", "highSchool", "doctorate"));
        status = new Attribute("status", Arrays.asList("neverMarried", "married", "divorced"));
        occupation = new Attribute("occupation", Arrays.asList("transport", "pro", "agri", "army"));
        incomeCategory = new Attribute("incomeCategory", Arrays.asList("low", "medium", "high"));

        attributes.add(age);
        attributes.add(education);
        attributes.add(status);
        attributes.add(occupation);
        attributes.add(incomeCategory);

        trainingData = new Instances("whatever", attributes, 0);

        trainingData.add(createInstance(39, "bachelor", "neverMarried", "transport", "medium"));
        trainingData.add(createInstance(50, "bachelor", "married", "pro", "medium"));
        trainingData.add(createInstance(18, "highSchool", "neverMarried", "agri", "low"));
        trainingData.add(createInstance(28, "bachelor", "married", "pro", "medium"));
        trainingData.add(createInstance(37, "highSchool", "married", "agri", "medium"));
        trainingData.add(createInstance(24, "highSchool", "neverMarried", "army", "low"));
        trainingData.add(createInstance(52, "highSchool", "divorced", "transport", "medium"));
        trainingData.add(createInstance(40, "doctorate", "married", "pro", "high"));

        options = new String[]{"-C", "0.50", "-M", "1"};
    }

    @Test
    public void showTrainingData() {
        System.out.println("trainingData = " + trainingData);
    }

    @Test
    public void createTree() {
        J48 tree = TreeHelper.trainTheTree(trainingData, options);
        System.out.println(tree);
    }

    @SneakyThrows
    @Test
    public void valueOfSingleInstance() {
        J48 tree = TreeHelper.trainTheTree(trainingData, options);
        Instance instance = createInstance(39, "bachelor", "neverMarried", "transport", DEFAULT_CATEGORY);
        instance.setDataset(trainingData);
        int result = (int) tree.classifyInstance(instance);
        String readableResult = incomeCategory.value(result);
        System.out.println("readableResult = " + readableResult);

        Assert.assertEquals("medium", readableResult);

    }

    @SneakyThrows
    @Test
    public void valueOfSingleInstanceWithDoctorate() {
        J48 tree = TreeHelper.trainTheTree(trainingData, options);
        Instance instance = createInstance(39, "doctorate", "neverMarried", "transport", DEFAULT_CATEGORY);
        instance.setDataset(trainingData);
        int result = (int) tree.classifyInstance(instance);
        String readableResult = incomeCategory.value(result);
        System.out.println("readableResult = " + readableResult);

        Assert.assertEquals("high", readableResult);

    }

    @Test
    public void createTreeTrainManyTimes() {

        J48 tree = null;

        for (int i = 0; i < 10000; i++) {
            tree = TreeHelper.trainTheTree(trainingData, options);
        }

        System.out.println(tree);
    }

    Instance createInstance(int a, String ed, String st, String occup, String categ) {
        Instance i1 = new DenseInstance(NUM_ATTRIBUTES);
        i1.setValue(age, a);
        i1.setValue(education, ed);
        i1.setValue(status, st);
        i1.setValue(occupation, occup);
        i1.setValue(incomeCategory, categ);
        return i1;

    }

}
