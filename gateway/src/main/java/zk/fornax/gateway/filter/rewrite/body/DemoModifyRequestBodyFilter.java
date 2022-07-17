package zk.fornax.gateway.filter.rewrite.body;

public class DemoModifyRequestBodyFilter extends ModifyRequestBodyFilter {

    @Override
    protected BodyModifier getBodyModifier() {
        return new BodyModifier() {

            @Override
            public byte[] convert(byte[] rawData) {
                return ("ChangeRequest: " + new String(rawData)).getBytes();
            }

        };
    }

}
