package zk.fornax.http.framework.security;

public interface BytesKeyGenerator {

    int getKeyLength();

    byte[] generateKey();

}
