package zk.fornax.http.framework.security;

import java.security.SecureRandom;

final class SecureRandomBytesKeyGenerator implements BytesKeyGenerator {

    private final SecureRandom random;

    private final int keyLength;

    SecureRandomBytesKeyGenerator(int keyLength) {
        this.random = new SecureRandom();
        this.keyLength = keyLength;
    }

    @Override
    public int getKeyLength() {
        return this.keyLength;
    }

    @Override
    public byte[] generateKey() {
        byte[] bytes = new byte[this.keyLength];
        this.random.nextBytes(bytes);
        return bytes;
    }

}
