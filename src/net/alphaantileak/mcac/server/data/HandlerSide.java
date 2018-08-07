package net.alphaantileak.mcac.server.data;

public enum HandlerSide {
    CLIENT,
    SERVER;

    public HandlerSide other() {
        return this == CLIENT ? SERVER : CLIENT;
    }
}
