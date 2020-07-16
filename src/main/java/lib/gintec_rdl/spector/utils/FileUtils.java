package lib.gintec_rdl.spector.utils;

public final class FileUtils {

    public static String getFileExtension(String name) {
        final int index = name.lastIndexOf('.');
        if (index != -1) {
            return name.substring(index + 1).trim().toLowerCase();
        }
        return null;
    }
}
