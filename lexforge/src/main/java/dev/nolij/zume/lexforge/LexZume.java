package dev.nolij.zume.lexforge;

import org.lwjgl.glfw.GLFW;
import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zume.integration.implementation.embeddium.ZumeEmbeddiumConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.invoke.MethodHandle;

import static dev.nolij.zume.impl.ZumeConstants.MOD_ID;

@Mod(MOD_ID)
public class LexZume implements IZumeImplementation {
	private static final KeyMapping ZOOM = new KeyMapping("zume.zoom", GLFW.GLFW_KEY_Z, "zume");
	private static final KeyMapping ZOOM_IN = new KeyMapping("zume.zoom_in", GLFW.GLFW_KEY_EQUAL, "zume");
	private static final KeyMapping ZOOM_OUT = new KeyMapping("zume.zoom_out", GLFW.GLFW_KEY_MINUS, "zume");
	
	public LexZume() {
		if (!FMLEnvironment.dist.isClient())
			return;
		
		Zume.LOGGER.info("Loading LexZume...");
		
		LexZumeConfigScreen.register();
		
		Zume.registerImplementation(this, FMLPaths.CONFIGDIR.get());
		if (Zume.disabled)
			return;
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyBindings);
		MinecraftForge.EVENT_BUS.addListener(this::render);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::calculateFOV);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onMouseScroll);
		
		if (MethodHandleHelper.PUBLIC.getClassOrNull("org.embeddedt.embeddium.api.OptionGUIConstructionEvent") != null) {
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
	
	@Override
	public CameraPerspective getCameraPerspective() {
		return CameraPerspective.values()[Minecraft.getInstance().options.getCameraType().ordinal()];
	}
	
	private void registerKeyBindings(RegisterKeyMappingsEvent event) {
		event.register(ZOOM);
		event.register(ZOOM_IN);
		event.register(ZOOM_OUT);
	}
	
	private void render(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Zume.renderHook();
		}
	}
	
	private void calculateFOV(ViewportEvent.ComputeFov event) {
		if (Zume.isFOVHookActive()) {
			event.setFOV(Zume.fovHook(event.getFOV()));
		}
	}
	
	private static final MethodHandle GET_SCROLL_DELTA = MethodHandleHelper.firstNonNull(
		MethodHandleHelper.PUBLIC.getMethodOrNull(InputEvent.MouseScrollingEvent.class, "getScrollDelta"),
		MethodHandleHelper.PUBLIC.getMethodOrNull(InputEvent.MouseScrollingEvent.class, "getDeltaY")
	);
	
	private void onMouseScroll(InputEvent.MouseScrollingEvent event) {
		//noinspection DataFlowIssue
		if (Zume.mouseScrollHook((int) (double) GET_SCROLL_DELTA.invokeExact(event))) {
			event.setCanceled(true);
		}
	}
	
}
