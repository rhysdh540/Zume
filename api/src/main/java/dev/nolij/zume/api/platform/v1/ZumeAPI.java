package dev.nolij.zume.api.platform.v1;

import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.Zume;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@ApiStatus.NonExtendable
public final class ZumeAPI {
	
	@Contract(pure = true)
	public static Logger getLogger() {
		return Zume.LOGGER;
	}
	
	/**
	 * Attempts to open Zume's config file in the system's text editor.
	 */
	public static void openConfigFile() {
		Zume.openConfigFile();
	}
	
	/**
	 * Invoke this in the initializer of your Zume implementation.
	 *
	 * @param implementation The {@linkplain IZumeImplementation} Zume should use. 
	 * @param instanceConfigPath The {@linkplain Path} Zume should use for storing the instance-specific config.
	 */
	public static void registerImplementation(@NotNull final IZumeImplementation implementation,
	                                          @NotNull final Path instanceConfigPath) {
		Zume.registerImplementation(new dev.nolij.zume.impl.IZumeImplementation() {
			@Override
			public boolean isZoomPressed() {
				return implementation.isZoomPressed();
			}
			
			@Override
			public boolean isZoomInPressed() {
				return implementation.isZoomInPressed();
			}
			
			@Override
			public boolean isZoomOutPressed() {
				return implementation.isZoomOutPressed();
			}
			
			@Override
			public CameraPerspective getCameraPerspective() {
				return CameraPerspective.values()[implementation.getCameraPerspective().ordinal()];
			}
			
			@Override
			public void onZoomActivate() {
				implementation.onZoomActivate();
			}
		}, instanceConfigPath);
	}
	
	//region Query Methods
	/**
	 * Returns `true` if Zoom is activated.
	 */
	@Contract(pure = true)
	public static boolean isActive() {
		return Zume.isActive();
	}
	
	/**
	 * Returns `true` if FOV should be hooked.
	 */
	@Contract(pure = true)
	public static boolean isFOVHookActive() {
		return Zume.isFOVHookActive();
	}
	
	/**
	 * Returns `true` if mouse scrolling should be hooked.
	 */
	@Contract(pure = true)
	public static boolean isMouseScrollHookActive() {
		return Zume.isMouseScrollHookActive();
	}
	//endregion
	
	//region Hooks
	/**
	 * This should be invoked once at the beginning of every frame. 
	 * It will handle Keybindings and Zoom Scrolling if the other hooks in this API are used correctly.
	 */
	public static void renderHook() {
		Zume.renderHook();
	}
	
	/**
	 * ONLY INVOKE THIS METHOD WHEN {@linkplain ZumeAPI#isFOVHookActive()} RETURNS `true`. 
	 * That check was explicitly excluded from this method for efficiency and for mixin compatibility.
	 * The {@linkplain IZumeImplementation} is responsible for this check.
	 *
	 * @param fov The unmodified FOV value
	 * {@return The new FOV transformed by Zume}
	 */
	public static double fovHook(double fov) {
		return Zume.fovHook(fov);
	}
	
	/**
	 * This method assumes Zume is active and the camera perspective is third-person. 
	 * If it is not, using this value will cause bugs.
	 *
	 * @param distance The vanilla third-person camera distance
	 * @return The new third-person camera distance
	 */
	public static double thirdPersonCameraHook(double distance) {
		return Zume.thirdPersonCameraHook(distance);
	}
	
	/**
	 * The return value of this method can be safely used without checking whether Zume is active.
	 *
	 * @param mouseSensitivity The unmodified mouse sensitivity
	 * {@return The new mouse sensitivity, transformed by Zume}
	 */
	public static double mouseSensitivityHook(double mouseSensitivity) {
		return Zume.mouseSensitivityHook(mouseSensitivity);
	}
	
	/**
	 * The return value of this method can be safely used without checking whether Zume is active.
	 *
	 * @param cinematicCameraEnabled The unmodified cinematic camera state
	 * {@return The new cinematic camera state, transformed by Zume}
	 */
	public static boolean cinematicCameraEnabledHook(boolean cinematicCameraEnabled) {
		return Zume.cinematicCameraEnabledHook(cinematicCameraEnabled);
	}
	
	/**
	 * The return value of this method can be safely used without checking whether Zume is active.
	 *
	 * @param scrollDelta The scroll delta (magnitude will be ignored, only the sign is used)
	 * {@return `true` if the invoker should prevent further handling of this scroll event}
	 */
	public static boolean mouseScrollHook(int scrollDelta) {
		return Zume.mouseScrollHook(scrollDelta);
	}
	//endregion
	
}
