package dev.nolij.zume.mixin.archaic;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.nolij.zume.api.platform.v1.ZumeAPI;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

	@Inject(method = "updateCameraAndRender", at = @At("HEAD"))
	public void zume$render(CallbackInfo ci) {
		ZumeAPI.renderHook();
	}
	
	@ModifyReturnValue(method = "getFOVModifier", at = @At("TAIL"))
	public float zume$modifyFOV(float original) {
		return ZumeAPI.isFOVHookActive() ? (float) ZumeAPI.fovHook(original) : original;
	}

	@ModifyExpressionValue(method = {"updateCameraAndRender", "updateRenderer"}, 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z"))
	public boolean zume$modifyCinematicCamera(boolean original) {
		return ZumeAPI.cinematicCameraEnabledHook(original);
	}

	@ModifyExpressionValue(method = {"updateCameraAndRender", "updateRenderer"}, 
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;mouseSensitivity:F"))
	public float zume$updateMouseSensitivity(float original) {
		return (float) ZumeAPI.mouseSensitivityHook(original);
	}
	
	@ModifyExpressionValue(method = "orientCamera", 
		at = {
			@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistance:F"),
			@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistanceTemp:F")
		})
	public float zume$modifyThirdPersonDistance(float original) {
		return (float) ZumeAPI.thirdPersonCameraHook(original);
	}

}
