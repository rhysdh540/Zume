package dev.nolij.zume.lexforge16;

import cpw.mods.modlauncher.api.INameMappingService;
import org.lwjgl.glfw.GLFW;
import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static dev.nolij.zume.impl.ZumeConstants.MOD_ID;

@Mod(MOD_ID)
public class LexZume16 implements IZumeImplementation {
	private static final KeyMapping ZOOM = new KeyMapping("zume.zoom", GLFW.GLFW_KEY_Z, "zume");
	private static final KeyMapping ZOOM_IN = new KeyMapping("zume.zoom_in", GLFW.GLFW_KEY_EQUAL, "zume");
	private static final KeyMapping ZOOM_OUT = new KeyMapping("zume.zoom_out", GLFW.GLFW_KEY_MINUS, "zume");
	
	public LexZume16() {
		if (!FMLEnvironment.dist.isClient())
			return;
		
		Zume.LOGGER.info("Loading LexZume16...");
		
		LexZume16ConfigScreen.register();
		
		Zume.registerImplementation(this, FMLPaths.CONFIGDIR.get());
		if (Zume.disabled)
			return;
		
		ClientRegistry.registerKeyBinding(ZOOM);
		ClientRegistry.registerKeyBinding(ZOOM_IN);
		ClientRegistry.registerKeyBinding(ZOOM_OUT);
		
		MinecraftForge.EVENT_BUS.addListener(this::render);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::calculateFOV);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onMouseScroll);
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
	
	private static final MethodHandle GET_CAMERA_TYPE = MethodHandleHelper.PUBLIC.getMethodOrNull(
		Options.class,
		ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_243230_g"),
		MethodType.methodType(Enum.class, Options.class)
	);
	private static final MethodHandle THIRD_PERSON_VIEW =
		MethodHandleHelper.PUBLIC.getGetterOrNull(Options.class, "field_74320_O", int.class);
	
	@Override
	public CameraPerspective getCameraPerspective() {
		int ordinal;
		if (GET_CAMERA_TYPE != null)
			ordinal = ((Enum<?>) GET_CAMERA_TYPE.invokeExact(Minecraft.getInstance().options)).ordinal();
		else
			//noinspection DataFlowIssue
			ordinal = (int) THIRD_PERSON_VIEW.invokeExact(Minecraft.getInstance().options);
		
		return CameraPerspective.values()[ordinal];
	}
	
	private void render(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Zume.renderHook();
		}
	}
	
	private void calculateFOV(EntityViewRenderEvent.FOVModifier event) {
		if (Zume.isFOVHookActive()) {
			event.setFOV(Zume.fovHook(event.getFOV()));
		}
	}
	
	private void onMouseScroll(InputEvent.MouseScrollEvent event) {
		if (Zume.mouseScrollHook((int) event.getScrollDelta())) {
			event.setCanceled(true);
		}
	}
	
}
