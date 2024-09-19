package club.tesseract.limbo.features.impl;

import ch.qos.logback.core.util.FileUtil;
import club.tesseract.limbo.Limbo;
import club.tesseract.limbo.features.BasicFeature;
import club.tesseract.limbo.properties.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import net.hollowcube.schem.Rotation;
import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.SchematicBuilder;
import net.hollowcube.schem.SchematicReader;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.chunk.ChunkUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WorldFeature extends BasicFeature {

    public WorldFeature(Limbo limbo) {
        super(limbo);
    }

    @Override
    public void onEnable() {
        InstanceContainer instanceContainer = limbo.getInstanceContainer();

        instanceContainer.setChunkSupplier(LightingChunk::new);

        ServerProperties properties = limbo.getServerProperties();

        final Pos worldSpawnPosition = properties.getWorldSpawnPosition();
        final int worldPreloadChunks = properties.getWorldPreloadChunks();
        final String schematicName = properties.getSchematicName();
        final String worldName = properties.getWorldName();
        final AnvilLoader loader = new AnvilLoader(worldName);
        instanceContainer.setChunkLoader(loader);


        var chunks = new ArrayList<CompletableFuture<Chunk>>();
        if(worldPreloadChunks > 0){
            ChunkUtils.forChunksInRange(worldSpawnPosition.chunkX(), worldSpawnPosition.chunkZ(), worldPreloadChunks, (x, z) -> chunks.add(instanceContainer.loadChunk(x, z)));
            CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();
        }

        if(schematicName != null){
            Path schematicPath = Path.of(schematicName);
            if(Files.exists(schematicPath)){
                Schematic schematic = new SchematicReader().read(schematicPath);
                RelativeBlockBatch blockBatch = schematic.build(Rotation.NONE, false);

                blockBatch.apply(instanceContainer, 0, 69, 0, () -> {
                    log.info("Applied schematic {}", schematicName);
                });
            }else{
                log.error("Schematic file {} does not exist", schematicName);
            }
        }
        CompletableFuture.runAsync(() -> {
            LightingChunk.relight(instanceContainer, instanceContainer.getChunks());
            if(Files.notExists(Path.of(worldName))){
                instanceContainer.saveInstance();
                instanceContainer.saveChunksToStorage();
                log.info("Saved world {}", worldName);
            }
            log.info("Relight {} chunks", chunks.size());
        });
    }

    @Override
    public void onDisable() {
        // no-op
    }
}
