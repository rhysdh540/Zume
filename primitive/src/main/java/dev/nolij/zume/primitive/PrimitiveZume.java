package dev.nolij.zume.primitive;

import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zume.mixin.primitive.GameRendererAccessor;
import dev.nolij.zume.mixin.primitive.MinecraftAccessor;
import dev.nolij.zumegradle.proguard.ProGuardKeep;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Keybind;
import net.minecraft.util.SmoothFloat;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PrimitiveZume implements ClientModInitializer, IZumeImplementation {
	private static final Keybind ZOOM = new Keybind("zume.zoom", Keyboard.KEY_Z);
	private static final Keybind ZOOM_IN = new Keybind("zume.zoom_in", Keyboard.KEY_EQUALS);
	private static final Keybind ZOOM_OUT = new Keybind("zume.zoom_out", Keyboard.KEY_MINUS);
	
	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
			return;
		
		Zume.LOGGER.info("Loading Primitive Zume...");
		
		Zume.registerImplementation(this, FabricLoader.getInstance().getConfigDir());
	}
	
	@Override
	public boolean isZoomPressed() {
		//noinspection UnreachableCode,DataFlowIssue
		return MinecraftAccessor.getInstance().screen == null && Keyboard.isKeyDown(ZOOM.keyId);
	}
	
	@Override
	public boolean isZoomInPressed() {
		return Keyboard.isKeyDown(ZOOM_IN.keyId);
	}
	
	@Override
	public boolean isZoomOutPressed() {
		return Keyboard.isKeyDown(ZOOM_OUT.keyId);
	}
	
	@Override
	public CameraPerspective getCameraPerspective() {
		//noinspection UnreachableCode,DataFlowIssue
		return MinecraftAccessor.getInstance().options.thirdPersonView 
		       ? CameraPerspective.THIRD_PERSON 
		       : CameraPerspective.FIRST_PERSON;
	}
	
	@Override
	public void onZoomActivate() {
		//noinspection DataFlowIssue
		if (Zume.config.enableCinematicZoom && !MinecraftAccessor.getInstance().options.smoothCamera) {
			final GameRendererAccessor gameRenderer = (GameRendererAccessor) MinecraftAccessor.getInstance().gameRenderer;
			gameRenderer.setSmoothTurnX(new SmoothFloat());
			gameRenderer.setSmoothTurnY(new SmoothFloat());
		}
	}
	
	@ProGuardKeep.WithObfuscation
	@EventListener
	public static void registerKeyBindings(KeyBindingRegisterEvent event) {
		final List<Keybind> binds = event.keyBindings;
		
		binds.add(ZOOM);
		binds.add(ZOOM_IN);
		binds.add(ZOOM_OUT);
	}
	
}
