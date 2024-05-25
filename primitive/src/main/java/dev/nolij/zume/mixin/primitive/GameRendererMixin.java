package dev.nolij.zume.mixin.primitive;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.nolij.zume.api.platform.v1.ZumeAPI;
import net.minecraft.class_555;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_555.class)
public abstract class GameRendererMixin {
	
	@Inject(method = "method_1844", at = @At("HEAD"))
	public void zume$render(CallbackInfo ci) {
		ZumeAPI.renderHook();
	}
	
	@ModifyReturnValue(method = "method_1848", at = @At("TAIL"))
	public float zume$modifyFOV(float original) {
		return ZumeAPI.isFOVHookActive() ? (float) ZumeAPI.fovHook(original) : original;
	}
	
	@ModifyExpressionValue(method = "method_1844", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;cinematicMode:Z"))
	public boolean zume$modifyCinematicCamera(boolean original) {
		return ZumeAPI.cinematicCameraEnabledHook(original);
	}
	
	@ModifyExpressionValue(method = "method_1844", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;mouseSensitivity:F"))
	public float zume$updateMouseSensitivity(float original) {
		return (float) ZumeAPI.mouseSensitivityHook(original);
	}
	
	@ModifyExpressionValue(method = "method_1851", at = {
		@At(value = "FIELD", target = "Lnet/minecraft/class_555;field_2359:F"),
		@At(value = "FIELD", target = "Lnet/minecraft/class_555;field_2360:F")
	})
	public float zume$modifyThirdPersonDistance(float original) {
        return (float) ZumeAPI.thirdPersonCameraHook(original);
	}
	
}
