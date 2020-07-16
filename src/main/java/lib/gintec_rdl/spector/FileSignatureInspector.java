package lib.gintec_rdl.spector;

import lib.gintec_rdl.spector.utils.FileUtils;
import lib.gintec_rdl.spector.utils.HexStringIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

final class FileSignatureInspector implements FileInspector {
    private static final Map<String, List<FileSignature>> hints = Collections
            .synchronizedMap(new LinkedHashMap<String, List<FileSignature>>());

    private static final Logger logger = LoggerFactory.getLogger(FileSignatureInspector.class);

    private final Spector spector;

    FileSignatureInspector(Spector spector) {
        this.spector = spector;
    }

    public TypeInfo getContentType(File file) {
        String extension;
        TypeInfo typeInfo;
        RandomAccessFile raf;
        HexStringIterator iterator;
        List<FileSignature> signatureHints;

        raf = null;
        typeInfo = null;
        iterator = new HexStringIterator();
        extension = FileUtils.getFileExtension(file.getName());

        try {
            raf = new RandomAccessFile(file, "r");
            if ((signatureHints = getSignatureHints(extension)) != null) {
                typeInfo = inspectFile0(signatureHints, iterator, raf, extension, false);
            }
            if (typeInfo == null) {
                typeInfo = inspectFile0(spector.getFileSignatures(), iterator, raf, extension, true);
            }
        } catch (Exception e) {
            logger.error("Error during inspection of file {}: {}", file, e.getMessage());
        } finally {
            closeHandle(raf);
        }
        return typeInfo;
    }

    private TypeInfo inspectFile0(Collection<FileSignature> signatures, HexStringIterator iterator,
                                  RandomAccessFile raf, String extension, boolean saveHint) throws Exception {
        TypeInfo typeInfo;
        for (FileSignature signature : signatures) {
            if ((typeInfo = inspectFile(signature, iterator, raf)) != null) {
                if (saveHint) {
                    addSignatureHint(extension, signature);
                }
                return typeInfo;
            }
        }
        return null;
    }

    private List<FileSignature> getSignatureHints(String extension) {
        if (extension != null) {
            synchronized (hints) {
                return hints.get(extension);
            }
        }
        return null;
    }

    private void addSignatureHint(String extension, FileSignature signature) {
        List<FileSignature> list;

        if (extension != null) {
            synchronized (hints) {
                if ((list = hints.get(extension)) == null) {
                    hints.put(extension, list = new LinkedList<FileSignature>());
                }
                list.add(signature);
            }
        }
    }

    private TypeInfo inspectFile(FileSignature signature, HexStringIterator iterator, RandomAccessFile file) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Inspecting file using {} signature", signature.getName());
        }
        if (signature.getTotalBlockSize() <= file.length()) {
            for (final DataBlock dataBlock : signature.getBlocks()) {
                if (!inspectBlock(dataBlock, iterator, file)) {
                    return null;
                }
            }
            return new TypeInfo(signature.getMime(), signature.getExt());
        } else {
            logger.warn("Signature blocks for {} exceed file size", signature.getName());
        }
        return null;
    }

    private boolean inspectBlock(DataBlock dataBlock, HexStringIterator iterator, RandomAccessFile file) throws Exception {
        int read;
        byte[] buf;
        long fileSize;
        long newOffset;
        long currentOffset;

        fileSize = file.length();
        currentOffset = file.getFilePointer();
        newOffset = dataBlock.getSeek().calculateOffset(dataBlock.getOffset(), currentOffset, fileSize);

        if (newOffset >= 0L && newOffset < fileSize) {
            buf = new byte[dataBlock.getSize()];
            file.seek(dataBlock.getSeek().calculateOffset(dataBlock.getOffset(), currentOffset, fileSize));
            if ((read = file.read(buf)) == dataBlock.getSize()) {
                iterator.setHexString(dataBlock.getBytes());
                return blockMatches(iterator, buf);
            } else {
                logger.warn("{} data block size ({}) did not match actual read bytes ({}) at offset {}",
                        dataBlock.getName(), dataBlock.getSize(), read, newOffset);
            }
        } else {
            logger.warn("{} data block would fall outside the file size boundary at offset {} of seek {}. " +
                    "Please check your schema.", dataBlock.getName(), dataBlock.getOffset(), dataBlock.getSeek());
        }
        return false;
    }

    private boolean blockMatches(HexStringIterator iterator, byte[] buf) {
        int index = 0;
        for (int _byte : iterator) {
            if (_byte != (buf[index] & 0xff)) {
                return false;
            }
            index++;
        }
        return true;
    }

    private static void closeHandle(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                logger.error("Error closing handle");
            }
        }
    }
}
