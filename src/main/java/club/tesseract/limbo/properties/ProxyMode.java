package club.tesseract.limbo.properties;

public enum ProxyMode {
    BUNGEEGUARD,
    BUNGEECORD,
    VELOCITY,
    NONE;

    public static ProxyMode fromString(String mode) {
        return switch (mode.toLowerCase()) {
            case "bungeeguard" -> BUNGEEGUARD;
            case "bungeecord" -> BUNGEECORD;
            case "velocity" -> VELOCITY;
            default -> NONE;
        };
    }
}
