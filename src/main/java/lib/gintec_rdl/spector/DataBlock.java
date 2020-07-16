package lib.gintec_rdl.spector;

/**
 * <p>A data block is a group of bytes used for sequentially scanning for exact blocks in files, to determine the file type.</p>
 */
public class DataBlock {
    /**
     * Direction to where to move the file pointer before scanning
     */
    public enum SeekType {
        /**
         * Move the pointer relative to the current offset
         */
        Current,
        /**
         * Move the pointer relative to the beginning of the file, in forward fashion.
         */
        Begin,
        /**
         * Move the pointer relative to the end of the file, in reverse fashion
         */
        End;

        public long calculateOffset(long newPosition, long currentPosition, long fileSize) {
            switch (this) {
                case End:
                    return fileSize - Math.abs(newPosition);
                case Begin:
                default:
                    return Math.abs(newPosition);
                case Current:
                    return currentPosition + Math.abs(newPosition);
            }
        }
    }

    private long offset;
    private String name;
    private SeekType seek;
    private String bytes;

    public DataBlock() {
        offset = 0;
        seek = SeekType.Begin;
    }

    long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        this.offset = offset;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    SeekType getSeek() {
        return seek;
    }

    public void setSeek(SeekType seek) {
        this.seek = seek;
    }

    String getBytes() {
        return bytes;
    }

    /**
     * Set the bytes in hexadecimal format. The string must be a multiple of 2
     *
     * @param bytes .
     */
    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    /**
     * Get the actual size of the encoded bytes
     *
     * @return
     */
    int getSize() {
        return bytes.length() / 2;
    }

    @Override
    public String toString() {
        return getName();
    }
}
