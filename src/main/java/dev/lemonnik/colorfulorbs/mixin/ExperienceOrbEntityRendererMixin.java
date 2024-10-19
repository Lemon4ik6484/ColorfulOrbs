package dev.lemonnik.colorfulorbs.mixin;

import dev.lemonnik.colorfulorbs.ConfigClass;
import dev.lemonnik.colorfulorbs.HexToRGB;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;

@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class ExperienceOrbEntityRendererMixin extends EntityRenderer{

    protected ExperienceOrbEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Accessor("TEXTURE")
    static Identifier getTexture() {
        throw new AssertionError();
    }

    private static final Identifier TEXTURE = getTexture();

    private static final int[] colorRGB = HexToRGB.convert(ConfigClass.color);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(ExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        matrixStack.push();

        matrixStack.scale(0.5F, 0.5F, 0.5F);
        this.shadowRadius = 0.0F;

        int j = experienceOrbEntity.getOrbSize();
        float uMin = (float)(j % 4 * 16 + 0) / 64.0F;
        float uMax = (float)(j % 4 * 16 + 16) / 64.0F;
        float vMin = (float)(j / 4 * 16 + 0) / 64.0F;
        float vMax = (float)(j / 4 * 16 + 16) / 64.0F;

        int red = colorRGB[0];
        int green = colorRGB[1];
        int blue = colorRGB[2];

        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        renderVertex(vertexConsumer, positionMatrix, normalMatrix, -0.5F, -0.25F, red, green, blue, uMin, vMax, light);
        renderVertex(vertexConsumer, positionMatrix, normalMatrix, 0.5F, -0.25F, red, green, blue, uMax, vMax, light);
        renderVertex(vertexConsumer, positionMatrix, normalMatrix, 0.5F, 0.75F, red, green, blue, uMax, vMin, light);
        renderVertex(vertexConsumer, positionMatrix, normalMatrix, -0.5F, 0.75F, red, green, blue, uMin, vMin, light);

        matrixStack.pop();
        ci.cancel();
    }

    private void renderVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, 0.0F)
                .color(red, green, blue, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
    }
}
