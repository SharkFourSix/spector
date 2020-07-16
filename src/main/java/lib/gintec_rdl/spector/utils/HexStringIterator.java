package lib.gintec_rdl.spector.utils;

import java.util.Iterator;

/**
 * <p>This class provides a facility to iterate through a hexadecimal string while converting each
 * character pair to a corresponding decimal integer.</p>
 * <p>The class is re-usable and can be reset by calling {@link #setHexString(String)}. However it is not
 * thread safe and callers should exercise caution when using the class in multithreaded environments.</p>
 */
public class HexStringIterator implements Iterator<Integer>, Iterable<Integer> {
    private String hex;
    private int length;
    private int offset;

    /**
     * <p>Sets the hexadecimal string and resets the state of the iterator so that the next
     * call to {@literal #next} will start from the beginning</p>
     *
     * @param hex Hex string to set
     */
    public void setHexString(String hex) {
        this.hex = hex.toUpperCase();
        if ((length = hex.length()) % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be a multiple of 2");
        }
        offset = 0;
    }

    public boolean hasNext() {
        return offset < length;
    }

    public Integer next() {
        final char[] chars = {getHexCharAt(offset), getHexCharAt(offset + 1)};
        offset += 2;
        return atodec(chars);
    }

    public void remove() {
        throw new UnsupportedOperationException("Class does not support removing items");
    }

    private char getHexCharAt(int index) {
        char c = hex.charAt(index);
        if (LocaleUtils.numberInclusivelyInRange(c, '0', '9') || LocaleUtils.numberInclusivelyInRange(c, 'A', 'F')) {
            return c;
        }
        throw new IllegalArgumentException("Invalid hex character '" + c + "' at index " + index);
    }

    private int atodec(char[] chars) {
        return atoi(chars[0]) * 16 + atoi(chars[1]);
    }

    private int atoi(char c) {
        return c >= 'A' ? c - 'A' + 10 : c - '0';
    }

    public Iterator<Integer> iterator() {
        return this;
    }
}
