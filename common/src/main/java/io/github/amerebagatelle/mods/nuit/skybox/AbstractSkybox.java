package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.components.Conditions;
import io.github.amerebagatelle.mods.nuit.components.Decorations;
import io.github.amerebagatelle.mods.nuit.components.Properties;
import io.github.amerebagatelle.mods.nuit.components.Weather;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.Objects;

/**
 * All classes that implement {@link AbstractSkybox} should
 * have a default constructor as it is required when checking
 * the type of the skybox.
 */
public abstract class AbstractSkybox implements NuitSkybox {

    /**
     * The current alpha for the skybox. Expects all skyboxes extending this to accommodate this.
     * This variable is responsible for fading in/out skyboxes.
     */
    public transient float alpha;
    protected Properties properties = Properties.of();
    protected Conditions conditions = Conditions.of();
    protected Decorations decorations = Decorations.of();

    protected boolean unexpectedConditionTransition = false;
    protected long lastTime = -2;
    protected float conditionAlpha = 0f;


    protected AbstractSkybox() {
    }

    protected AbstractSkybox(Properties properties, Conditions conditions, Decorations decorations) {
        this.properties = properties;
        this.conditions = conditions;
        this.decorations = decorations;
    }

    @Override
    public void tick(ClientLevel clientWorld) {
        this.updateAlpha();
    }

    /**
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    @Override
    public float updateAlpha() {
        long currentTime = Objects.requireNonNull(Minecraft.getInstance().level).getDayTime() % this.properties.getFade().getDuration();

        boolean condition = this.checkConditions();

        float fadeAlpha = 1f;
        if (this.properties.getFade().isAlwaysOn()) {
            this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
        } else {
            Tuple<Long, Long> keyFrames = Utils.findClosestKeyframes(this.properties.getFade().getKeyFrames(), currentTime);
            fadeAlpha = Utils.calculateInterpolatedAlpha(currentTime, this.properties.getFade().getDuration(), keyFrames.getA(), keyFrames.getB(), this.properties.getFade().getKeyFrames().get(keyFrames.getA()), this.properties.getFade().getKeyFrames().get(keyFrames.getB()));

            if ((this.lastTime == currentTime - 1 || this.lastTime == currentTime) && !this.unexpectedConditionTransition) { // Check if time is ticking or if time is same (doDaylightCycle gamerule)
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
            } else {
                this.unexpectedConditionTransition = true;
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, NuitClient.config().generalSettings.unexpectedTransitionDuration, condition);
                if (this.unexpectedConditionTransition && (this.conditionAlpha == 0f || this.conditionAlpha == 1f)) {
                    this.unexpectedConditionTransition = false;
                }
            }
        }

        this.alpha = fadeAlpha * this.conditionAlpha;
        this.lastTime = currentTime;

        return this.alpha;
    }

    /**
     * @return Whether all conditions were met
     */
    protected boolean checkConditions() {
        return this.checkDimensions() && this.checkWorlds() && this.checkBiomes() && this.checkXRanges() &&
                this.checkYRanges() && this.checkZRanges() && this.checkWeather() && this.checkEffects();
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    protected boolean checkBiomes() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        Objects.requireNonNull(client.player);
        return this.conditions.getBiomes().getEntries().isEmpty() || this.conditions.getBiomes().isExcludes() ^ (
                this.conditions.getBiomes().getEntries().contains(client.level.registryAccess().registryOrThrow(Registries.BIOME).getKey(client.level.getBiome(client.player.blockPosition()).value())) ||
                        this.conditions.getBiomes().getEntries().contains(DefaultHandler.DEFAULT) && DefaultHandler.checkFallbackBiomes());
    }

    /**
     * @return Whether the current dimension identifier is valid for this skybox
     */
    protected boolean checkDimensions() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return this.conditions.getDimensions().getEntries().isEmpty() || this.conditions.getBiomes().isExcludes() ^ (
                this.conditions.getDimensions().getEntries().contains(client.level.dimension().location()) ||
                        this.conditions.getDimensions().getEntries().contains(DefaultHandler.DEFAULT) && DefaultHandler.checkFallbackDimensions());
    }

    /**
     * @return Whether the current dimension sky effect is valid for this skybox
     */
    protected boolean checkWorlds() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return this.conditions.getWorlds().getEntries().isEmpty() || this.conditions.getBiomes().isExcludes() ^ (
                this.conditions.getWorlds().getEntries().contains(client.level.dimensionType().effectsLocation()) ||
                        this.conditions.getWorlds().getEntries().contains(DefaultHandler.DEFAULT) && DefaultHandler.checkFallbackWorlds());
    }

    /*
		Check if an effect that should prevent skybox from showing
     */
    protected boolean checkEffects() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);

        Camera camera = client.gameRenderer.getMainCamera();

        if (this.conditions.getEffects().getEntries().isEmpty()) {
            // Vanilla checks
            boolean thickFog = client.level.effects().isFoggyAt(Mth.floor(camera.getPosition().x()), Mth.floor(camera.getPosition().y())) || client.gui.getBossOverlay().shouldCreateWorldFog();
            if (thickFog) {
                // Render skybox in thick fog, enabled by default
                return this.properties.isRenderInThickFog();
            }

            FogType cameraSubmersionType = camera.getFluidInCamera();
            if (cameraSubmersionType == FogType.POWDER_SNOW || cameraSubmersionType == FogType.LAVA)
                return false;

            return !(camera.getEntity() instanceof LivingEntity livingEntity) || (!livingEntity.hasEffect(MobEffects.BLINDNESS) && !livingEntity.hasEffect(MobEffects.DARKNESS));

        } else {
            if (camera.getEntity() instanceof LivingEntity livingEntity) {
                return (this.conditions.getEffects().isExcludes() ^ this.conditions.getEffects().getEntries().stream().noneMatch(identifier -> client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).get(identifier) != null && livingEntity.hasEffect(client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).wrapAsHolder(client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).get(identifier)))));
            }
        }
        return true;
    }

    /**
     * @return Whether the current x values are valid for this skybox.
     */
    protected boolean checkXRanges() {
        double playerX = Objects.requireNonNull(Minecraft.getInstance().player).getX();
        return Utils.checkRanges(playerX, this.conditions.getXRanges().getEntries(), this.conditions.getXRanges().isExcludes());
    }

    /**
     * @return Whether the current y values are valid for this skybox.
     */
    protected boolean checkYRanges() {
        double playerY = Objects.requireNonNull(Minecraft.getInstance().player).getY();
        return Utils.checkRanges(playerY, this.conditions.getYRanges().getEntries(), this.conditions.getYRanges().isExcludes());
    }

    /**
     * @return Whether the current z values are valid for this skybox.
     */
    protected boolean checkZRanges() {
        double playerZ = Objects.requireNonNull(Minecraft.getInstance().player).getZ();
        return Utils.checkRanges(playerZ, this.conditions.getZRanges().getEntries(), this.conditions.getZRanges().isExcludes());
    }

    /**
     * @return Whether the current weather is valid for this skybox.
     */
    protected boolean checkWeather() {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        Biome.Precipitation precipitation = world.getBiome(player.blockPosition()).value().getPrecipitationAt(player.blockPosition());
        if (this.conditions.getWeathers().getEntries().isEmpty()) {
            return true;
        }

        if ((this.conditions.getWeathers().isExcludes() ^ this.conditions.getWeathers().getEntries().contains(Weather.THUNDER)) && world.isThundering()) {
            return true;
        }
        if ((this.conditions.getWeathers().isExcludes() ^ this.conditions.getWeathers().getEntries().contains(Weather.RAIN)) && world.isRaining() && !world.isThundering()) {
            return true;
        }
        if ((this.conditions.getWeathers().isExcludes() ^ this.conditions.getWeathers().getEntries().contains(Weather.SNOW)) && world.isRaining() && precipitation == Biome.Precipitation.SNOW) {
            return true;
        }
        if ((this.conditions.getWeathers().isExcludes() ^ this.conditions.getWeathers().getEntries().contains(Weather.BIOME_RAIN)) && world.isRaining() && precipitation == Biome.Precipitation.RAIN) {
            return true;
        }
        return (this.conditions.getWeathers().isExcludes() ^ this.conditions.getWeathers().getEntries().contains(Weather.CLEAR)) && !world.isRaining() && !world.isThundering();
    }

    public void renderDecorations(LevelRendererAccessor worldRendererAccess, PoseStack matrixStack, Matrix4f projectionMatrix, float tickDelta, BufferBuilder bufferBuilder, float alpha, Runnable fogCallback) {
        RenderSystem.enableBlend();
        Map<Long, Quaternionf> keyframes = this.decorations.getRotation().getKeyframes();
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;

        // Custom Blender
        this.decorations.getBlend().applyBlendFunc(alpha);
        matrixStack.pushPose();

        // axis rotation
        long currentTime = world.getDayTime() % this.decorations.getRotation().getRotationDuration();
        var closestKeyframes = Utils.findClosestKeyframes(keyframes, currentTime);
        var result = new Quaternionf();
        keyframes.get(closestKeyframes.getA()).nlerp(keyframes.get(closestKeyframes.getB()), alpha, result);
        matrixStack.mulPose(result);

        // Vanilla rotation
        //matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        // Iris Compat
        //matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(IrisCompat.getSunPathRotation()));
        //matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        Matrix4f matrix4f2 = matrixStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // Sun
        if (this.decorations.isSunEnabled()) {
            RenderSystem.setShaderTexture(0, this.decorations.getSunTexture());
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, -30.0F).uv(0.0F, 0.0F).endVertex();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, -30.0F).uv(1.0F, 0.0F).endVertex();
            bufferBuilder.vertex(matrix4f2, 30.0F, 100.0F, 30.0F).uv(1.0F, 1.0F).endVertex();
            bufferBuilder.vertex(matrix4f2, -30.0F, 100.0F, 30.0F).uv(0.0F, 1.0F).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
        // Moon
        if (this.decorations.isMoonEnabled()) {
            RenderSystem.setShaderTexture(0, this.decorations.getMoonTexture());
            int moonPhase = world.getMoonPhase();
            int xCoord = moonPhase % 4;
            int yCoord = moonPhase / 4 % 2;
            float startX = xCoord / 4.0F;
            float startY = yCoord / 2.0F;
            float endX = (xCoord + 1) / 4.0F;
            float endY = (yCoord + 1) / 2.0F;
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, 20.0F).uv(endX, endY).endVertex();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, 20.0F).uv(startX, endY).endVertex();
            bufferBuilder.vertex(matrix4f2, 20.0F, -100.0F, -20.0F).uv(startX, startY).endVertex();
            bufferBuilder.vertex(matrix4f2, -20.0F, -100.0F, -20.0F).uv(endX, startY).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
        // Stars
        if (this.decorations.isStarsEnabled()) {
            float i = 1.0F - world.getRainLevel(tickDelta);
            float brightness = world.getStarBrightness(tickDelta) * i;
            if (brightness > 0.0F) {
                RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
                FogRenderer.setupNoFog();
                worldRendererAccess.getStarsBuffer().bind();
                worldRendererAccess.getStarsBuffer().drawWithShader(matrixStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
                VertexBuffer.unbind();
                fogCallback.run();
            }
        }
        matrixStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public Decorations getDecorations() {
        return this.decorations;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public Conditions getConditions() {
        return this.conditions;
    }

    @Override
    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public int getLayer() {
        return this.properties.getLayer();
    }

    @Override
    public boolean isActive() {
        return this.getAlpha() != 0F;
    }

    @Override
    public String toString() {
        return String.format("[layer=%s, alpha=%s, dimension=%s, world=%s, biomes=%s, xranges=%s, yranges=%s, zranges=%s, weather=%s, effects=%s]", getProperties().getLayer(), getAlpha(), checkDimensions(), checkWorlds(), checkBiomes(), checkXRanges(), checkYRanges(), checkZRanges(), checkWeather(), checkEffects());
    }
}