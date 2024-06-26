package dev.nolij.zume.mixin.archaic;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.nolij.zume.impl.Zume;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	@Inject(method = "updateCameraAndRender", at = @At("HEAD"))
	public void zume$render$HEAD(CallbackInfo ci) {
		Zume.renderHook();
	}
	
	@ModifyReturnValue(method = "getFOVModifier", at = @At("TAIL"))
	public float zume$getFOV$TAIL(float original) {
		if (Zume.isFOVHookActive())
			return (float) Zume.fovHook(original);
		
		return original;
	}

	@ModifyExpressionValue(method = {"updateCameraAndRender", "updateRenderer"}, 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z"))
	public boolean zume$updateMouse$smoothCameraEnabled(boolean original) {
		return Zume.cinematicCameraEnabledHook(original);
	}

	@ModifyExpressionValue(method = {"updateCameraAndRender", "updateRenderer"}, 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;mouseSensitivity:F"))
	public float zume$updateMouse$mouseSensitivity(float original) {
		return (float) Zume.mouseSensitivityHook(original);
	}
	
	@ModifyExpressionValue(method = "orientCamera", 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistance:F"))
	public float zume$orientCamera$thirdPersonDistance(float original) {
		return (float) Zume.thirdPersonCameraHook(original);
	}
	
	@ModifyExpressionValue(method = "orientCamera", 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistanceTemp:F"))
	public float zume$orientCamera$thirdPersonDistanceTemp(float original) {
		return (float) Zume.thirdPersonCameraHook(original);
	}

}
