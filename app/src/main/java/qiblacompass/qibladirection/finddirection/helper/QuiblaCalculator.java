package qiblacompass.qibladirection.finddirection.helper;

public class QuiblaCalculator {

    public static double calculateQibla(double latitude, double longitude) {
        double phiK = 21.4 * Math.PI / 180.0;
        double lambdaK = 39.8 * Math.PI / 180.0;
        double phi = latitude * Math.PI / 180.0;
        double lambda = longitude * Math.PI / 180.0;
        double psi = 180.0 / Math.PI * Math.atan2(Math.sin(lambdaK - lambda), Math.cos(phi) * Math.tan(phiK) - Math.sin(phi) * Math.cos(lambdaK - lambda));
        return Math.round(psi);
    }

}