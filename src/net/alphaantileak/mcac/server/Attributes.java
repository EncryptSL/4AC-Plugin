package net.alphaantileak.mcac.server;

import io.netty.util.AttributeKey;
import net.alphaantileak.mcac.server.data.HandlerSide;
import net.alphaantileak.mcac.server.data.Stage;

/**
 * @author notaviable
 * @version 1.0
 */
public class Attributes {
    public static final AttributeKey<Stage> PROTOCOL_STAGE = AttributeKey.valueOf("protocolStage");
    public static final AttributeKey<HandlerSide> HANDLER_SIDE = AttributeKey.valueOf("handlerSide");
}
