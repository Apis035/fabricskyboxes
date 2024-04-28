package io.github.amerebagatelle.mods.nuit.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.*;
import io.github.amerebagatelle.mods.nuit.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class SquareTexturedSkybox extends TexturedSkybox<SquareTexturedSkybox> {
    public static Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Texture.CODEC.fieldOf("texture").forGetter(SquareTexturedSkybox::getTexture)
    ).apply(instance, SquareTexturedSkybox::new));
    protected Texture texture;

    public SquareTexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, Texture texture) {
        super(properties, conditions, decorations, blend);
        this.texture = texture;
    }

    @Override
    public RegistrySupplier<SkyboxType<SquareTexturedSkybox>> getType() {
        return SkyboxType.SQUARE_TEXTURED_SKYBOX;
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public void renderSkybox(LevelRendererAccessor worldRendererAccess, PoseStack matrices, float tickDelta, Camera camera, boolean thickFog, Runnable runnable) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.setShaderTexture(0, this.texture.getTextureId());
        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            UVRange tex = this.texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F);
            matrices.pushPose();

            if (i == 1) {
                tex = this.texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1);
                matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            } else if (i == 2) {
                tex = this.texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F);
                matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
            } else if (i == 3) {
                tex = this.texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F);
                matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
            } else if (i == 4) {
                tex = this.texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1);
                matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
            } else if (i == 5) {
                tex = this.texture.withUV(0, 1.0F / 2.0F, 1.0F / 3.0F, 1);
                matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.last().pose();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(tex.getMinU(), tex.getMinV()).endVertex();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(tex.getMinU(), tex.getMaxV()).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(tex.getMaxU(), tex.getMaxV()).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(tex.getMaxU(), tex.getMinV()).endVertex();
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
