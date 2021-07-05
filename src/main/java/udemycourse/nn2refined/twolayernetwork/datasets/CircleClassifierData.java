package udemycourse.nn2refined.twolayernetwork.datasets;

public class CircleClassifierData extends  DataSetter {
    public CircleClassifierData() {
        defineInData();
        defineOutData();
        defineNetworkSetup();
    }

    @Override
    public void defineInData() {
        this.inData = new float[][]{
                // YELLOW CIRCLES 1 -> (1,0,0)
                new float[]{0.1f, 0.2f},
                new float[]{0.3f, 0.2f},
                new float[]{0.15f, 0.58f},
                new float[]{0.45f, 0.7f},
                new float[]{0.4f, 0.9f},

                // GREEN CIRCLES 2 -> (0,1,0)
                new float[]{0.4f, 1.2f},
                new float[]{0.45f, 0.95f},
                new float[]{0.42f, 1f},
                new float[]{0.5f, 1.1f},
                new float[]{0.52f, 1.45f},

                // BLUE CIRCLES 3 -> (0,0,1)
                new float[]{0.6f, 0.2f},
                new float[]{0.75f, 0.7f},
                new float[]{0.9f, 0.34f},
                new float[]{0.85f, 0.76f},
                new float[]{0.8f, 0.34f}
        };
    }

    @Override
    public void defineOutData() {
        this.outData= new float[][]{
                new float[] { 1,0,0 },
                new float[] { 1,0,0 },
                new float[] { 1,0,0 },
                new float[] { 1,0,0 },
                new float[] { 1,0,0 },
                new float[] { 0,1,0 },
                new float[] { 0,1,0 },
                new float[] { 0,1,0 },
                new float[] { 0,1,0 },
                new float[] { 0,1,0 },
                new float[] { 0,0,1 },
                new float[] { 0,0,1 },
                new float[] { 0,0,1 },
                new float[] { 0,0,1 },
                new float[] { 0,0,1 }
        };
    }

    @Override
    public void   defineNetworkSetup() {
        this.NOF_LAYERS = 2;
        this.NOF_INPUTS = 2;
        this.NOF_NEURONS_HIDDENLAYER = 4;
        this.NOF_NEURONS_OUTPUTLAYER = 3;
    }
}
