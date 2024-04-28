package io.github.amerebagatelle.mods.nuit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.Metadata;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SkyboxManager implements NuitApi, ClientTickEvent.ClientLevel {
    private static final SkyboxManager INSTANCE = new SkyboxManager();
    private final Map<ResourceLocation, Skybox> skyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(ResourceLocation, Skybox)
     */
    private final Map<ResourceLocation, Skybox> permanentSkyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    private final List<Skybox> activeSkyboxes = new LinkedList<>();
    private final Predicate<? super Skybox> renderPredicate = (skybox) -> !this.activeSkyboxes.contains(skybox) && skybox.isActive();
    private Skybox currentSkybox = null;
    private boolean enabled = true;

    public static Skybox parseSkyboxJson(ResourceLocation id, JsonObject jsonObject) {
        Metadata metadata;

        try {
            metadata = Metadata.CODEC.decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst();
        } catch (RuntimeException e) {
            NuitClient.getLogger().warn("Skipping invalid skybox {}", id.toString(), e);
            NuitClient.getLogger().warn(jsonObject.toString());
            return null;
        }

        SkyboxType<? extends Skybox> type = SkyboxType.REGISTRY.get(metadata.getType());
        //Preconditions.checkNotNull(type, "Unknown skybox type: " + metadata.getType().getPath().replace('_', '-'));
        if (type == null) {
            NuitClient.getLogger().warn("Unknown skybox type: {}", metadata.getType().getPath().replace('_', '-'));
            return null;
        }
        return type.getCodec(metadata.getSchemaVersion()).decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst();
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }

    public void addSkybox(ResourceLocation identifier, JsonObject jsonObject) {
        Skybox skybox = SkyboxManager.parseSkyboxJson(identifier, jsonObject);
        if (skybox != null) {
            this.addSkybox(identifier, skybox);
        }
    }

    public void addSkybox(ResourceLocation identifier, Skybox skybox) {
        Preconditions.checkNotNull(identifier, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.skyboxMap.put(identifier, skybox);
        this.sortSkybox();
    }

    /**
     * Sorts skyboxes by ascending order with priority field. Skyboxes with
     * identical priority will not be re-ordered, this will largely come down to
     * the alphabetical order that Minecraft resources load in.
     * <p>
     * Minecraft's resource loading order example:
     * "fabricskyboxes:sky/overworld_sky1.json"
     * "fabricskyboxes:sky/overworld_sky10.json"
     * "fabricskyboxes:sky/overworld_sky11.json"
     * "fabricskyboxes:sky/overworld_sky2.json"
     */
    private void sortSkybox() {
        Map<ResourceLocation, Skybox> newSortedMap = this.skyboxMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Skybox::getPriority)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (skybox, skybox2) -> skybox, Object2ObjectLinkedOpenHashMap::new));
        this.skyboxMap.clear();
        this.skyboxMap.putAll(newSortedMap);
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     *
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(ResourceLocation identifier, Skybox skybox) {
        Preconditions.checkNotNull(identifier, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        this.permanentSkyboxMap.put(identifier, skybox);
    }

    @Internal
    public void clearSkyboxes() {
        this.skyboxMap.clear();
        this.activeSkyboxes.clear();
    }

    @Internal
    public void renderSkyboxes(LevelRendererAccessor worldRendererAccess, PoseStack matrixStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        this.activeSkyboxes.forEach(skybox -> {
            this.currentSkybox = skybox;
            skybox.render(worldRendererAccess, matrixStack, projectionMatrix, tickDelta, camera, thickFog, fogCallback);
        });
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Skybox getCurrentSkybox() {
        return this.currentSkybox;
    }

    @Override
    public List<Skybox> getActiveSkyboxes() {
        return this.activeSkyboxes;
    }

    //todo: implement this
    @Override
    public void tick(net.minecraft.client.multiplayer.ClientLevel client) {
        StreamSupport
                .stream(Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxMap.values()).spliterator(), false)
                .forEach(skybox -> skybox.tick(client));
        this.activeSkyboxes.removeIf(skybox -> !skybox.isActive());
        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        this.skyboxMap.values().stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.permanentSkyboxMap.values().stream().filter(this.renderPredicate).forEach(this.activeSkyboxes::add);
        this.activeSkyboxes.sort(Comparator.comparingInt(Skybox::getPriority));
    }

    public Map<ResourceLocation, Skybox> getSkyboxMap() {
        return skyboxMap;
    }
}
