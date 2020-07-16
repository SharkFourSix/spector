package lib.gintec_rdl.spector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Provider with a {@link Logger}. That's about it</p>
 */
public abstract class FileSignatureProviderImpl implements FileSignatureProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Logger getLogger() {
        return logger;
    }
}
