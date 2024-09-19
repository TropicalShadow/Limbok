package club.tesseract.limbo.features.impl;

import club.tesseract.limbo.Limbo;
import club.tesseract.limbo.features.BasicFeature;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;

public class VoidHeightFeature extends BasicFeature {

    private final int voidHeight;

    EventListener<PlayerMoveEvent> voidHeightEvent;

    public VoidHeightFeature(Limbo limbo, final int voidHeight) {
        super(limbo);
        this.voidHeight = voidHeight;
    }

    @Override
    public void onEnable() {
        voidHeightEvent = EventListener.builder(PlayerMoveEvent.class).handler(event -> {
            final Pos newPosition = event.getNewPosition();

            if(newPosition.y() < voidHeight) {
                event.getPlayer().teleport(limbo.getServerProperties().getWorldSpawnPosition());
            }
        }).build();
        MinecraftServer.getGlobalEventHandler().addListener(voidHeightEvent);
    }

    @Override
    public void onDisable() {
        MinecraftServer.getGlobalEventHandler().removeListener(voidHeightEvent);
    }
}
