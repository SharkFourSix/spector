package lib.gintec_rdl.spector;

import lib.gintec_rdl.spector.utils.GetValue;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>This provider provides a facility to load signature files from resources.</p>
 * <p>The signatures are cached for subsequent calls. If however, the provider fails to load the signatures, each call
 * to {@link #getSignatures()} will always attempt to load the signatures from the resources.</p>
 * <p>Therefore, the number of attempts is dependent upon how many times callers invoke {@link #getSignatures()}.</p>
 */
public final class ResourceFileSignatureProvider extends FileSignatureProviderImpl {
    private boolean loaded;
    private final String directory;
    private List<FileSignature> fileSignatures;

    public ResourceFileSignatureProvider(String directory) {
        this.directory = GetValue.of(directory).notNull("directory");
    }

    public String getName() {
        return "Resource File Signature Provider";
    }

    public List<FileSignature> getSignatures() {
        InputStreamReader reader;
        FileSignature[] signatureArray;

        if (!loaded) {
            reader = null;
            try {
                reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(directory));
                signatureArray = Spector.GSON.fromJson(reader, FileSignature[].class);
                fileSignatures = Collections.unmodifiableList(Arrays.asList(signatureArray));
                loaded = true;
            } catch (Exception e) {
                getLogger().error("Error loading file signatures from resource path {}",
                        new Object[]{directory, e});
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        getLogger().warn("Error closing resource handle when loading signatures from {}",
                                new Object[]{directory, e});
                    }
                }
            }
            fileSignatures = fileSignatures != null ? fileSignatures : Collections.<FileSignature>emptyList();
        }
        return fileSignatures;
    }
}
