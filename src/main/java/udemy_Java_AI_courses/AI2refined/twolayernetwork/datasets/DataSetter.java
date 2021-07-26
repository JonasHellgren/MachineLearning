package udemy_Java_AI_courses.AI2refined.twolayernetwork.datasets;

public abstract class DataSetter {


    public int NOF_LAYERS=2;
    public int NOF_INPUTS=2;
    public int NOF_NEURONS_HIDDENLAYER=3;
    public int NOF_NEURONS_OUTPUTLAYER=1;
    public float LEARNING_RATE = 0.3f;
    public float MOMENTUM = 0.6f;
    public int NOF_ITERATIONS = 10000;

    public float[][] inData;
    public float[][] outData;

    abstract  void defineInData();
    abstract  void defineOutData();
    abstract  void defineSetup();


}
