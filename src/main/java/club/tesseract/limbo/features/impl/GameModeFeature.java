package club.tesseract.limbo.features.impl;

import club.tesseract.limbo.Limbo;
import club.tesseract.limbo.features.BasicFeature;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class GameModeFeature extends BasicFeature {

    EventListener<PlayerSpawnEvent> setGameModeEvent;

    public GameModeFeature(Limbo limbo) {
        super(limbo);
    }

    @Override
    public void onEnable() {
        setGameModeEvent = EventListener.builder(PlayerSpawnEvent.class).handler(event -> {
            final Player player = event.getPlayer();
            player.setGameMode(limbo.getServerProperties().getGameMode());
        }).build();
        MinecraftServer.getGlobalEventHandler().addListener(setGameModeEvent);
    }

    @Override
    public void onDisable() {
        MinecraftServer.getGlobalEventHandler().removeListener(setGameModeEvent);
    }
}
