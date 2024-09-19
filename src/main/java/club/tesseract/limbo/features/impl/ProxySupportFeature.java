package club.tesseract.limbo.features.impl;

import club.tesseract.limbo.Limbo;
import club.tesseract.limbo.features.BasicFeature;
import club.tesseract.limbo.properties.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ProxySupportFeature extends BasicFeature {

    private final ServerProperties properties;

    public ProxySupportFeature(Limbo limbo) {
        super(limbo);
        this.properties = limbo.getServerProperties();
    }

    @Override
    public void onEnable() {
        switch (properties.getProxyMode()) {
            case BUNGEEGUARD -> {
                log.info("Proxy mode is set to BUNGEEGUARD.");
                enableBungeeCordProxy(true);
            }
            case BUNGEECORD -> {
                log.info("Proxy mode is set to BUNGEECORD.");
                enableBungeeCordProxy(false);
            }
            case VELOCITY -> {
                log.info("Proxy mode is set to VELOCITY.");
                enableVelocityProxy();
            }
            case NONE -> {
                log.info("Proxy mode is set to NONE.");
            }
        }
    }

    void enableBungeeCordProxy(boolean isBungeeGuard) {
        final Set<String> proxySecret = new HashSet<>(getProxySecret());
        BungeeCordProxy.enable();
        if(isBungeeGuard){
            BungeeCordProxy.setBungeeGuardTokens(proxySecret);
        }
    }

    void enableVelocityProxy() {
        final Set<String> proxySecret = getProxySecret();
        if(proxySecret.isEmpty()) {
            log.error("Failed to enable Velocity proxy. Proxy secret is not set.");
            return;
        }
        VelocityProxy.enable(proxySecret.iterator().next());
    }

    Set<String> getProxySecret() {
        final String proxySecretFile = properties.getProxySecretFile();
        if(proxySecretFile == null || proxySecretFile.isEmpty()) {
            log.warn("Proxy secret file is not set.");
            return new HashSet<String>();
        }
        try{
            List<String> fileLines = Files.readAllLines(Path.of(proxySecretFile));
            return fileLines.stream().map(String::trim).collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to read proxy secret file", e);
            return new HashSet<>();
        }
    }


    @Override
    public void onDisable() {
        // no-op
    }
}
