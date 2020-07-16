package lib.gintec_rdl.spector.utils;

public final class LocaleUtils {

    public static boolean numberInclusivelyInRange(int value, int min, int max) {
        return min <= value && value <= max;
    }
}
