package dev.nolij.zume.mixin.lexforge18;

import dev.nolij.zume.impl.Zume;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	
	@Redirect(method = "turnPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z"))
	public boolean zume$updateMouse$smoothCameraEnabled(Options instance) {
		return Zume.cinematicCameraEnabledHook(instance.smoothCamera);
	}
	
	@Redirect(method = "turnPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;sensitivity:D", ordinal = 0))
	public double zume$updateMouse$getMouseSensitivity$getValue(Options instance) {
		return Zume.mouseSensitivityHook(instance.sensitivity);
	}
	
}
