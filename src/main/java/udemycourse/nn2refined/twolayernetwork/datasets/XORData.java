package udemycourse.nn2refined.twolayernetwork.datasets;

public class XORData extends  DataSetter{

    public XORData() {
        defineInData();
        defineOutData();
        defineNetworkSetup();
    }

    @Override
    public void defineInData() {
        this.inData= new float[][]{
                new float[]{0, 0},
                new float[]{0, 1},
                new float[]{1, 0},
                new float[]{1, 1}
        };
    }

    @Override
    public void defineOutData() {
        this.outData= new float[][]{
                new float[]{0},
                new float[]{1},
                new float[]{1},
                new float[]{0}
        };
    }

    @Override
    public void   defineNetworkSetup() {
        this.NOF_LAYERS = 2;
        this.NOF_INPUTS = 2;
        this.NOF_NEURONS_HIDDENLAYER = 3;
        this.NOF_NEURONS_OUTPUTLAYER = 1;
    }

}
