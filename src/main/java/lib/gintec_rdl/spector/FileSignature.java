package lib.gintec_rdl.spector;

import java.util.List;

/**
 * <p>A file signature comprises of specific byte sequences frequently found in specific files.
 * These bytes are then used to detect the type of file.</p>
 */
public class FileSignature {
    private String name;
    private String ext;
    private String mime;
    private List<DataBlock> blocks;

    private transient int totalBlockSize;

    FileSignature() {
        totalBlockSize = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the file extension normally used by files of this type
     */
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * @return Returns the MIME content type of the file
     */
    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * @return Returns data blocks, used to detect files
     */
    public List<DataBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<DataBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * @return Returns the total block size in bytes
     */
    public synchronized int getTotalBlockSize() {
        if (totalBlockSize == -1) {
            if (blocks != null && !blocks.isEmpty()) {
                totalBlockSize = 0;
                for (DataBlock block : blocks) {
                    totalBlockSize += block.getSize();
                }
            }
        }
        return totalBlockSize;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileSignature && this.name.equalsIgnoreCase(((FileSignature) obj).name);
    }

    @Override
    public String toString() {
        return "[name=" + name + ", type=" + mime + ", ext=" + ext + "]";
    }
}
