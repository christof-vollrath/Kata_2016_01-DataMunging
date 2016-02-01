public class WeatherData {
    private int dy;
    private double mxT;
    private double mnT;

    public WeatherData(int dy, double mxT, double mnT) {
        this.dy = dy;
        this.mxT = mxT;
        this.mnT = mnT;
    }

    public int getDy() {
        return dy;
    }

    public double getMxT() {
        return mxT;
    }

    public double getMnT() {
        return mnT;
    }

    public double getSpread() {
        return mxT - mnT;
    }
}
