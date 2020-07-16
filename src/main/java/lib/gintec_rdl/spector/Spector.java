package lib.gintec_rdl.spector;

import com.google.gson.Gson;
import lib.gintec_rdl.spector.utils.GetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Spector {
    private static final Logger LOG = LoggerFactory.getLogger(Spector.class);
    private static final Spector instance = new Spector();
    static final Gson GSON = new Gson();

    private final AtomicBoolean signaturesLoaded;
    private final AtomicBoolean providersLoaded;
    private final FileSignatureInspector inspector;
    private final Set<FileSignature> fileSignatures;
    private final ConcurrentHashMap<String, FileSignatureProvider> providers;

    private boolean autoLoadProviders() {
        return Boolean.valueOf(System.getProperty("spector.autoloadProviders", "false"));
    }

    private Spector() {
        signaturesLoaded = new AtomicBoolean();
        providersLoaded = new AtomicBoolean();
        inspector = new FileSignatureInspector(this);
        providers = new ConcurrentHashMap<String, FileSignatureProvider>();
        fileSignatures = new LinkedHashSet<FileSignature>();
        if (autoLoadProviders()) {
            LOG.warn("Auto loading providers");
            loadSpiProviders();
        }
    }

    private TypeInfo getFileType(File file) {
        loadSignatures();
        return inspector.getContentType(file);
    }

    public static TypeInfo inspect(String path) {
        return inspect(new File(path));
    }

    Set<FileSignature> getFileSignatures() {
        return fileSignatures;
    }

    private void appendProviders(FileSignatureProvider[] providers) {
        for (FileSignatureProvider provider : providers) {
            this.providers.put(provider.getName(), provider);
        }
    }

    /**
     * <p>Inspect the given file and return its type</p>
     *
     * @param file The file to inspect
     * @return {@link TypeInfo} object containing file content type or null if the type of the file could not be detected
     */
    public static TypeInfo inspect(File file) {
        return instance.getFileType(file);
    }

    /**
     * <p>Loads providers using SPI API. If providers where already loaded, the method does nothing</p>
     */
    public static void loadProviders() {
        instance.loadSpiProviders();
    }

    /**
     * <p>Manually add {@link FileSignatureProvider}s to spector, from where to source file type detection signatures.</p>
     * <p>Alternatively, providers can be added to spector by way of automatic discovery through the SPI API.</p>
     *
     * @param providers List of signature
     */
    public static void addProviders(FileSignatureProvider... providers) {
        instance.appendProviders(GetValue.of(providers).notNull("Provider list cannot be null"));
    }

    private void loadSignatures() {
        loadSpiProviders();

        if (signaturesLoaded.compareAndSet(false, true)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Loading signatures from {} providers", new Object[]{providers.size()});
            }
            if (!providers.isEmpty()) {
                for (FileSignatureProvider provider : providers.values()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Loading file signatures from {}", new Object[]{provider.getName()});
                    }
                    List<FileSignature> signatureList = provider.getSignatures();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Loaded {} file signatures from {}", new Object[]{signatureList.size(), provider.getName()});
                    }
                    this.fileSignatures.addAll(signatureList);
                }
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Loaded {} file signatures in total", new Object[]{this.fileSignatures.size()});
            }
        }
    }

    private void loadSpiProviders() {
        ServiceLoader<FileSignatureProvider> serviceLoader;
        Iterator<FileSignatureProvider> iterator;

        if (providersLoaded.compareAndSet(false, true)) {
            serviceLoader = ServiceLoader.load(FileSignatureProvider.class);
            iterator = serviceLoader.iterator();

            while (iterator.hasNext()) {
                FileSignatureProvider provider;

                provider = iterator.next();
                providers.put(provider.getName(), provider);
            }
        }
    }
}
