package zk.fornax.common.utils;

public final class Hex {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private Hex() {
    }

    public static char[] encode(byte[] bytes) {
        final int count = bytes.length;
        char[] result = new char[2 * count];
        int j = 0;
        for (byte oneByte : bytes) {
            result[j++] = HEX_CHARS[(0xF0 & oneByte) >>> 4];
            result[j++] = HEX_CHARS[(0x0F & oneByte)];
        }
        return result;
    }

    public static byte[] decode(CharSequence s) {
        int count = s.length();
        if (count % 2 != 0) {
            throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
        }
        byte[] result = new byte[count / 2];
        for (int i = 0; i < count; i += 2) {
            int msb = Character.digit(s.charAt(i), 16);
            int lsb = Character.digit(s.charAt(i + 1), 16);
            if (msb < 0 || lsb < 0) {
                throw new IllegalArgumentException("Detected a Non-hex character at " + (i + 1) + " or " + (i + 2) + " position");
            }
            result[i / 2] = (byte) ((msb << 4) | lsb);
        }
        return result;
    }

}
