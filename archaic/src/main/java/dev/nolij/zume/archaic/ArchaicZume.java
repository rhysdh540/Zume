package dev.nolij.zume.archaic;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zume.mixin.archaic.EntityRendererAccessor;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.MouseFilter;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static dev.nolij.zume.impl.ZumeConstants.*;

@Mod(
	modid = MOD_ID,
	name = MOD_NAME,
	version = MOD_VERSION,
	acceptedMinecraftVersions = ARCHAIC_VERSION_RANGE,
	guiFactory = "dev.nolij.zume.archaic.ArchaicConfigProvider",
	dependencies = "required-after:unimixins@[0.1.15,)")
public class ArchaicZume implements IZumeImplementation {
	
	public static final KeyBinding ZOOM = new KeyBinding("zume.zoom", Keyboard.KEY_Z, "zume");
	public static final KeyBinding ZOOM_IN = new KeyBinding("zume.zoom_in", Keyboard.KEY_EQUALS, "zume");
	public static final KeyBinding ZOOM_OUT = new KeyBinding("zume.zoom_out", Keyboard.KEY_MINUS, "zume");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!FMLLaunchHandler.side().isClient())
			return;
		
		Zume.LOGGER.info("Loading Archaic Zume...");
		
		Zume.registerImplementation(this, Launch.minecraftHome.toPath().resolve("config"));
		if (Zume.disabled)
			return;
		
		ClientRegistry.registerKeyBinding(ZOOM);
		ClientRegistry.registerKeyBinding(ZOOM_IN);
		ClientRegistry.registerKeyBinding(ZOOM_OUT);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isZoomPressed() {
		return Minecraft.getMinecraft().currentScreen == null && ZOOM.isPressed();
	}
	
	@Override
	public boolean isZoomInPressed() {
		return ZOOM_IN.isPressed();
	}
	
	@Override
	public boolean isZoomOutPressed() {
		return ZOOM_OUT.isPressed();
	}
	
	@Override
	public CameraPerspective getCameraPerspective() {
		return CameraPerspective.values()[Minecraft.getMinecraft().gameSettings.thirdPersonView];
	}
	
	@Override
	public void onZoomActivate() {
		if (Zume.config.enableCinematicZoom && !Minecraft.getMinecraft().gameSettings.smoothCamera) {
			final EntityRendererAccessor entityRenderer = (EntityRendererAccessor) Minecraft.getMinecraft().entityRenderer;
			entityRenderer.setMouseFilterXAxis(new MouseFilter());
			entityRenderer.setMouseFilterYAxis(new MouseFilter());
			entityRenderer.setSmoothCamYaw(0F);
			entityRenderer.setSmoothCamPitch(0F);
			entityRenderer.setSmoothCamFilterX(0F);
			entityRenderer.setSmoothCamFilterY(0F);
			entityRenderer.setSmoothCamPartialTicks(0F);
		}
	}
	
	private static final MethodHandle SET_CANCELED = MethodHandleHelper.PUBLIC.getMethodOrNull(
		Event.class, 
		"setCanceled", 
		MethodType.methodType(void.class, MouseEvent.class, boolean.class), 
		boolean.class
	);
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void mouseEvent(MouseEvent mouseEvent) {
		if (Zume.mouseScrollHook(mouseEvent.dwheel)) {
			//noinspection DataFlowIssue
			SET_CANCELED.invokeExact(mouseEvent, true);
		}
	}
	
}
