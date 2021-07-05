package udemycourse.nn2refined.twolayernetwork.datasets;

abstract class DataSetter {

    public int NOF_LAYERS=2;
    public int NOF_INPUTS=2;
    public int NOF_NEURONS_HIDDENLAYER=3;
    public int NOF_NEURONS_OUTPUTLAYER=1;
    public float[][] inData;
    public float[][] outData;

    abstract  void defineInData();
    abstract  void defineOutData();
    abstract  void defineNetworkSetup();

}
