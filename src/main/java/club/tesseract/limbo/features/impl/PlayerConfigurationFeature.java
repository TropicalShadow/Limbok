package club.tesseract.limbo.features.impl;

import club.tesseract.limbo.Limbo;
import club.tesseract.limbo.features.BasicFeature;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

public class PlayerConfigurationFeature extends BasicFeature {

    EventListener<AsyncPlayerConfigurationEvent> playerConfigurationEvent;

    public PlayerConfigurationFeature(Limbo limbo) {
        super(limbo);
    }

    @Override
    public void onEnable() {
        final Pos spawnPosition = limbo.getServerProperties().getWorldSpawnPosition();

        playerConfigurationEvent = EventListener.builder(AsyncPlayerConfigurationEvent.class).handler(event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(limbo.getInstanceContainer());
            player.setRespawnPoint(spawnPosition);
        }).build();

        MinecraftServer.getGlobalEventHandler().addListener(playerConfigurationEvent);
    }

    @Override
    public void onDisable() {
        MinecraftServer.getGlobalEventHandler().removeListener(playerConfigurationEvent);
    }
}
