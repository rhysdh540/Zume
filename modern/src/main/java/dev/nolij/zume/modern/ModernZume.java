package dev.nolij.zume.modern;

import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zume.integration.implementation.embeddium.ZumeEmbeddiumConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class ModernZume implements ClientModInitializer, IZumeImplementation {
	private static final KeyMapping ZOOM = new KeyMapping("zume.zoom", GLFW.GLFW_KEY_Z, "zume");
	private static final KeyMapping ZOOM_IN = new KeyMapping("zume.zoom_in", GLFW.GLFW_KEY_EQUAL, "zume");
	private static final KeyMapping ZOOM_OUT = new KeyMapping("zume.zoom_out", GLFW.GLFW_KEY_MINUS, "zume");
	
	
	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
			return;
		
		Zume.LOGGER.info("Loading Modern Zume...");
		
		Zume.registerImplementation(this, FabricLoader.getInstance().getConfigDir());
		if (Zume.disabled)
			return;
		
		KeyBindingHelper.registerKeyBinding(ZOOM);
		KeyBindingHelper.registerKeyBinding(ZOOM_IN);
		KeyBindingHelper.registerKeyBinding(ZOOM_OUT);
		
		if (MethodHandleHelper.PUBLIC.getClassOrNull("org.embeddedt.embeddium.client.gui.options.OptionIdentifier") != null) {
			new ZumeEmbeddiumConfigScreen();
		}
	}
	
	@Override
	public boolean isZoomPressed() {
		return Minecraft.getInstance().screen == null && ZOOM.isDown();
	}
	
	@Override
	public boolean isZoomInPressed() {
		return ZOOM_IN.isDown();
	}
	
	@Override
	public boolean isZoomOutPressed() {
		return ZOOM_OUT.isDown();
	}
	
	private static final MethodHandle GET_PERSPECTIVE = MethodHandleHelper.PUBLIC.getMethodOrNull(
		Options.class,
		FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary",
			"net.minecraft.class_315", "method_31044", "()Lnet/minecraft/class_5498;"), // Options.getCameraType
		MethodType.methodType(Enum.class, Options.class));
	private static final MethodHandle PERSPECTIVE =
		MethodHandleHelper.PUBLIC.getGetterOrNull(Options.class, "field_1850", int.class); // Options.thirdPersonView
	
	@Override
	public CameraPerspective getCameraPerspective() {
		int ordinal;
		if (GET_PERSPECTIVE != null)
			ordinal = ((Enum<?>) GET_PERSPECTIVE.invokeExact(Minecraft.getInstance().options)).ordinal();
		else
			//noinspection DataFlowIssue
			ordinal = (int) PERSPECTIVE.invokeExact(Minecraft.getInstance().options);
		
		return CameraPerspective.values()[ordinal];
	}
	
}
