package lib.gintec_rdl.spector.utils;

import java.util.Iterator;

/**
 * <p>This class provides a facility to iterate through a hexadecimal string while converting each
 * character pair to a corresponding decimal integer.</p>
 * <p>The class is re-usable and can be reset by calling {@link #setHexString(String)}. However it is not
 * thread safe and callers should exercise caution when using the class in multithreaded environments.</p>
 */
public class HexStringIterator implements Iterator<HexStringIterator.ByteValue>, Iterable<HexStringIterator.ByteValue> {
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

    public ByteValue next() {
        final char[] chars = {getHexCharAt(offset), getHexCharAt(offset + 1)};
        offset += 2;
        return new ByteValue(chars, this);
    }

    public void remove() {
        throw new UnsupportedOperationException("Class does not support removing items");
    }

    private char getHexCharAt(int index) {
        char c = hex.charAt(index);
        if (c == '?' || LocaleUtils.numberInclusivelyInRange(c, '0', '9')
                || LocaleUtils.numberInclusivelyInRange(c, 'A', 'F')) {
            return c;
        }
        throw new IllegalArgumentException("Invalid hex character '" + c + "' at index " + index
                + ". Only hex digits and wildcard (?) specifiers are allowed.");
    }

    private int atodec(char[] chars) {
        return atoi(chars[0]) * 16 + atoi(chars[1]);
    }

    private int atoi(char c) {
        return c >= 'A' ? c - 'A' + 10 : c - '0';
    }

    public Iterator<ByteValue> iterator() {
        return this;
    }

    public static class ByteValue {
        private Byte low;
        private Byte high;
        private final Byte octet;
        private final boolean wildcard;

        ByteValue(char[] chars, HexStringIterator hsi) {
            wildcard = chars[0] == '?' || chars[1] == '?';
            if (wildcard) {
                octet = null;
                if (chars[0] != '?') {
                    low = (byte) ((hsi.atoi(chars[0]) & 0xff));
                }
                if (chars[1] != '?') {
                    high = (byte) ((hsi.atoi(chars[1]) & 0xff));
                }
            } else {
                octet = (byte) (hsi.atodec(chars) & 0xff);
                low = high = null;
            }
        }

        public boolean matches(byte input) {
            if (wildcard) {
                int _low = (input & 0xf0) >> 1;
                int _high = (input & 0x0f);

                // Both low and high are optional
                if (low == null && high == null) {
                    return true;
                } else if (low != null) {
                    return low.intValue() == _low;
                } else {
                    return high.intValue() == _high;
                }
            } else {
                return octet.intValue() == input;
            }
        }

        @Override
        public String toString() {
            return "ByteValue{" +
                    "low=" + low +
                    ", high=" + high +
                    ", octet=" + octet +
                    ", wildcard=" + wildcard +
                    '}';
        }
    }
}
