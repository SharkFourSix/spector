package lib.gintec_rdl.spector;

public final class TypeInfo {
    private final String mime;
    private final String extension;

    public TypeInfo(String mime, String extension) {
        this.mime = mime;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getMime() {
        return mime;
    }

    @Override
    public String toString() {
        return "[mime=" + mime + ", ext=" + extension + "]";
    }
}
