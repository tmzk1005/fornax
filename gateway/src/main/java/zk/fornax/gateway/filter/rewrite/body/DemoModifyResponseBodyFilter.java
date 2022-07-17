package zk.fornax.gateway.filter.rewrite.body;

public class DemoModifyResponseBodyFilter extends ModifyResponseBodyFilter {

    @Override
    protected BodyModifier getBodyModifier() {
        return new BodyModifier() {
            
            @Override
            public byte[] convert(byte[] rawData) {
                return ("ChangeBody: " + new String(rawData)).getBytes();
            }

        };
    }

}
