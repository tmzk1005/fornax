package zk.fornax.gateway.filter.rewrite.body;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public interface BodyModifier {

    byte[] convert(byte[] rawData);

    default ByteBuf convert(ByteBuf byteBuf) {
        byte[] rawData;
        if (byteBuf.hasArray()) {
            rawData = byteBuf.array();
        } else {
            rawData = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(rawData);
        }
        byte[] convertedBytes = convert(rawData);
        return Unpooled.wrappedBuffer(convertedBytes);
    }

    default String getNewContentType() {
        return null;
    }

}
