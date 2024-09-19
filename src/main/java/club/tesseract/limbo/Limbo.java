package club.tesseract.limbo;

import club.tesseract.limbo.blockhandler.SignHandler;
import club.tesseract.limbo.features.Feature;
import club.tesseract.limbo.features.impl.*;
import club.tesseract.limbo.properties.ServerProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.BlockManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public final class Limbo {

    @Getter
    ServerProperties serverProperties;
    @Getter
    private InstanceContainer instanceContainer;

    private final CopyOnWriteArraySet<Feature> features = new CopyOnWriteArraySet<>();

    public Limbo() {
        try {
            serverProperties = new ServerProperties(new File("server.properties"));
        } catch (IOException e) {
            log.error("Failed to load server properties", e);
            System.exit(1);
        }
    }


    public void createLimbo() {
        MinecraftServer minecraftServer = MinecraftServer.init();

        features.add(new ProxySupportFeature(this));
        features.add(new PlayerConfigurationFeature(this));
        features.add(new WorldFeature(this));
        features.add(new GameModeFeature(this));

        Integer voidHeight = serverProperties.getWorldVoidHeight();
        if(voidHeight != null) {
            features.add(new VoidHeightFeature(this, voidHeight));
        }

        BlockManager bm = MinecraftServer.getBlockManager();
        bm.registerHandler(SignHandler.NAMESPACE_ID, SignHandler::new);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer();


        features.forEach(Feature::onEnable);
        MinecraftServer.getSchedulerManager().buildShutdownTask(this::stop);

        log.info("Starting server on {}:{}", serverProperties.getServerIp(), serverProperties.getServerPort());
        minecraftServer.start(serverProperties.getServerIp(), serverProperties.getServerPort());
    }

    void stop() {
        features.forEach(Feature::onDisable);
    }
}