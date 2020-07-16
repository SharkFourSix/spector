package lib.gintec_rdl.spector;

import java.util.List;

/**
 * <p>A file signature provider provides the facility to load file signatures from various sources.</p>
 */
public interface FileSignatureProvider {
    /**
     * @return Returns the name of the provider
     */
    String getName();

    /**
     * @return Returns the file signatures loaded by this provider
     */
    List<FileSignature> getSignatures();
}
