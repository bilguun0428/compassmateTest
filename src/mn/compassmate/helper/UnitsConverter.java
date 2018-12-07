package mn.compassmate.helper;

public final class UnitsConverter {

    private static final double KNOTS_TO_KPH_RATIO = 0.539957;
    private static final double KNOTS_TO_MPH_RATIO = 0.868976;
    private static final double KNOTS_TO_MPS_RATIO = 1.94384;
    private static final double KNOTS_TO_CPS_RATIO = 0.0194384449;
    private static final double METERS_TO_FEET_RATIO = 0.3048;
    private static final double METERS_TO_MILE_RATIO = 1609.34;

    private UnitsConverter() {
    }

    public static double knotsFromKph(double value) { // km/h
        return value * KNOTS_TO_KPH_RATIO;
    }

    public static double kphFromKnots(double value) {
        return value / KNOTS_TO_KPH_RATIO;
    }

    public static double knotsFromMph(double value) {
        return value * KNOTS_TO_MPH_RATIO;
    }

    public static double mphFromKnots(double value) {
        return value / KNOTS_TO_MPH_RATIO;
    }

    public static double knotsFromMps(double value) { // m/s
        return value * KNOTS_TO_MPS_RATIO;
    }

    public static double mpsFromKnots(double value) {
        return value / KNOTS_TO_MPS_RATIO;
    }

    public static double knotsFromCps(double value) { // cm/s
        return value * KNOTS_TO_CPS_RATIO;
    }

    public static double feetFromMeters(double value) {
        return value / METERS_TO_FEET_RATIO;
    }

    public static double metersFromFeet(double value) {
        return value * METERS_TO_FEET_RATIO;
    }

    public static double milesFromMeters(double value) {
        return value / METERS_TO_MILE_RATIO;
    }

    public static double metersFromMiles(double value) {
        return value * METERS_TO_MILE_RATIO;
    }

}
