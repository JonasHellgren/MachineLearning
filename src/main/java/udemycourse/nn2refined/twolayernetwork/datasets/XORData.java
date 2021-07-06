package udemycourse.nn2refined.twolayernetwork.datasets;

public class XORData extends  DataSetter {

    public XORData() {
        defineInData();
        defineOutData();
        defineSetup();
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
    public void defineSetup() {
        this.NOF_LAYERS = 2;
        this.NOF_INPUTS = 2;
        this.NOF_NEURONS_HIDDENLAYER = 3;
        this.NOF_NEURONS_OUTPUTLAYER = 1;
        this.NOF_ITERATIONS = 10000;
    }

}
