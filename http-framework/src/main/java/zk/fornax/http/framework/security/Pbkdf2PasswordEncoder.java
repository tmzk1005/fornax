package zk.fornax.http.framework.security;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import zk.fornax.common.utils.Hex;

public class Pbkdf2PasswordEncoder implements PasswordEncoder {

    private static final Pbkdf2PasswordEncoder DEFAULT_INSTANCE = new Pbkdf2PasswordEncoder("--^--grace--^--");

    private static final int DEFAULT_SALT_LENGTH = 16;

    private static final int DEFAULT_HASH_WIDTH = 256;

    private static final int DEFAULT_ITERATIONS = 185000;

    private final BytesKeyGenerator saltGenerator;

    private final byte[] secret;

    private final int hashWidth;

    private final int iterations;

    public Pbkdf2PasswordEncoder(CharSequence secret) {
        this(secret, DEFAULT_SALT_LENGTH, DEFAULT_ITERATIONS, DEFAULT_HASH_WIDTH);
    }

    public Pbkdf2PasswordEncoder(CharSequence secret, int saltLength, int iterations, int hashWidth) {
        this.secret = utf8Encode(secret);
        this.saltGenerator = new SecureRandomBytesKeyGenerator(saltLength);
        this.iterations = iterations;
        this.hashWidth = hashWidth;
    }

    public static Pbkdf2PasswordEncoder getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        byte[] encoded = this.encode(rawPassword, salt);
        return this.asHexString(encoded);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = this.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return MessageDigest.isEqual(digested, this.encode(rawPassword, salt));
    }

    private String asHexString(byte[] bytes) {
        return String.valueOf(Hex.encode(bytes));
    }

    private byte[] decode(String encodedBytes) {
        return Hex.decode(encodedBytes);
    }

    private byte[] encode(CharSequence rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                rawPassword.toString().toCharArray(), EncodingUtils.concatenate(salt, this.secret), this.iterations, this.hashWidth
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return EncodingUtils.concatenate(salt, skf.generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not create hash", exception);
        }
    }

    public static byte[] utf8Encode(CharSequence string) {
        try {
            ByteBuffer bytes = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException exception) {
            throw new IllegalArgumentException("Encoding failed", exception);
        }
    }

}
