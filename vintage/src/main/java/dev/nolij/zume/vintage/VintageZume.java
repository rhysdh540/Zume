package dev.nolij.zume.vintage;

import org.lwjgl.input.Keyboard;
import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static dev.nolij.zume.impl.ZumeConstants.*;

@Mod(
	modid = MOD_ID,
	name = MOD_NAME,
	version = MOD_VERSION, 
	acceptedMinecraftVersions = VINTAGE_VERSION_RANGE,
	guiFactory = "dev.nolij.zume.vintage.VintageConfigProvider")
public class VintageZume implements IZumeImplementation {
	private static final KeyBinding ZOOM = new KeyBinding("zume.zoom", Keyboard.KEY_Z, "zume");
	private static final KeyBinding ZOOM_IN = new KeyBinding("zume.zoom_in", Keyboard.KEY_EQUALS, "zume");
	private static final KeyBinding ZOOM_OUT = new KeyBinding("zume.zoom_out", Keyboard.KEY_MINUS, "zume");
	
	public VintageZume() {
		if (!FMLLaunchHandler.side().isClient())
			return;
		
		Zume.LOGGER.info("Loading Vintage Zume...");
		
		Zume.registerImplementation(this, new File(Launch.minecraftHome, "config").toPath());
		if (Zume.disabled)
			return;
		
		ClientRegistry.registerKeyBinding(ZOOM);
		ClientRegistry.registerKeyBinding(ZOOM_IN);
		ClientRegistry.registerKeyBinding(ZOOM_OUT);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isZoomPressed() {
		return Minecraft.getMinecraft().currentScreen == null && ZOOM.isKeyDown();
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
	
	@SubscribeEvent
	public void render(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Zume.renderHook();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void calculateFOV(EntityViewRenderEvent.FOVModifier event) {
		if (Zume.isFOVHookActive()) {
			event.setFOV((float) Zume.fovHook(event.getFOV()));
		}
	}
	
	private static final MethodHandle SET_CANCELED = MethodHandleHelper.PUBLIC.getMethodOrNull(
		Event.class,
		"setCanceled",
		MethodType.methodType(void.class, MouseEvent.class, boolean.class),
		boolean.class
	);
	private static final MethodHandle GET_DWHEEL = MethodHandleHelper.firstNonNull(
		MethodHandleHelper.PUBLIC.getMethodOrNull(
			MouseEvent.class,
			"getDwheel"
		),
		MethodHandleHelper.PUBLIC.getGetterOrNull(
			MouseEvent.class,
			"dwheel",
			int.class
		)
	);
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void mouseEvent(MouseEvent mouseEvent) {
		//noinspection DataFlowIssue
		if (Zume.mouseScrollHook((int) GET_DWHEEL.invokeExact(mouseEvent))) {
			//noinspection DataFlowIssue
			SET_CANCELED.invokeExact(mouseEvent, true);
		}
	}
	
}
