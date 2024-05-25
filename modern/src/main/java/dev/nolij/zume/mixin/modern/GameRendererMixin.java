package dev.nolij.zume.mixin.modern;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.nolij.zume.api.platform.v1.ZumeAPI;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	
	@Inject(method = "render", at = @At("HEAD"))
	public void zume$render(CallbackInfo ci) {
		ZumeAPI.renderHook();
	}
	
	@ModifyReturnValue(method = "getFov", at = @At("TAIL"))
	public double zume$modifyFOV(double original) {
		return ZumeAPI.isFOVHookActive() ? ZumeAPI.fovHook(original) : original;
	}
	
}
