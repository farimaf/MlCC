/**
 * Created by farima on 6/27/17.
 */
public class Preprocessor {

    public static void main(String[] args) {
        new RandomSelecter().makeRandomTestTrain();
        new CloneMetricsIntegrator().intergrate();
    }
}