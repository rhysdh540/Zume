package dev.nolij.zume.common;

import dev.nolij.zume.common.config.ZumeConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Zume {
	
	public static final String MOD_ID = "zume";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final String CONFIG_FILE_NAME = MOD_ID + ".json5";
	
	public static ZumeVariant ZUME_VARIANT = null;
	
	private static final ClassLoader classLoader = Zume.class.getClassLoader();
	public static void calculateZumeVariant() {
		if (ZUME_VARIANT != null)
			return;
		
		var connectorPresent = false;
		try {
			Class.forName("dev.su5ed.sinytra.connector.service.ConnectorLoaderService");
			connectorPresent = true;
		} catch (ClassNotFoundException ignored) {}
		
		if (!connectorPresent && 
			classLoader.getResource("net/fabricmc/fabric/api/client/keybinding/v1/KeyBindingHelper.class") != null)
			ZUME_VARIANT = ZumeVariant.MODERN;
		else if (classLoader.getResource("net/legacyfabric/fabric/api/client/keybinding/v1/KeyBindingHelper.class") != null)
			ZUME_VARIANT = ZumeVariant.LEGACY;
		else if (classLoader.getResource("net/modificationstation/stationapi/api/client/event/option/KeyBindingRegisterEvent.class") != null)
			ZUME_VARIANT = ZumeVariant.PRIMITIVE;
		else if (classLoader.getResource("cpw/mods/fml/client/registry/ClientRegistry.class") != null)
			ZUME_VARIANT = ZumeVariant.ARCHAIC_FORGE;
		else if (classLoader.getResource("net/minecraftforge/oredict/OreDictionary.class") != null)
			ZUME_VARIANT = ZumeVariant.VINTAGE_FORGE;
		else if (classLoader.getResource("net/neoforged/neoforge/common/NeoForge.class") != null)
			ZUME_VARIANT = ZumeVariant.NEOFORGE;
		else {
			try {
				final Class<?> forgeVersionClass = Class.forName("net.minecraftforge.versions.forge.ForgeVersion");
				final Method getVersionMethod = forgeVersionClass.getMethod("getVersion");
				final String forgeVersion = (String) getVersionMethod.invoke(null);
				final int major = Integer.parseInt(forgeVersion.substring(0, forgeVersion.indexOf('.')));
				if (major > 40)
					ZUME_VARIANT = ZumeVariant.LEXFORGE;
				else if (major > 36)
					ZUME_VARIANT = ZumeVariant.LEXFORGE18;
				else 
					ZUME_VARIANT = ZumeVariant.LEXFORGE16;
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
		}
	}
	
	public static IZumeProvider ZUME_PROVIDER;
	
	public static ZumeConfig CONFIG;
	public static File CONFIG_FILE;
	private static double inverseSmoothness = 1D;
	
	public static void init(final IZumeProvider zumeProvider, final File configFile) {
		if (ZUME_PROVIDER != null)
			throw new AssertionError("Zume already initialized!");
		
		ZUME_PROVIDER = zumeProvider;
		CONFIG_FILE = configFile;
		
		ZumeConfig.create(configFile, config -> {
			CONFIG = config;
			inverseSmoothness = 1D / CONFIG.zoomSmoothnessMs;
			toggle = false;
		});
	}
	
	public static void openConfigFile() throws IOException {
		Desktop.getDesktop().open(Zume.CONFIG_FILE);
	}
	
	private static double fromZoom = -1D;
	private static double zoom = 1D;
	private static long tweenStart = 0L;
	private static long tweenEnd = 0L;
	
	private static double getZoom() {
		if (tweenEnd != 0L && CONFIG.zoomSmoothnessMs != 0) {
			final long timestamp = System.currentTimeMillis();
			
			if (tweenEnd >= timestamp) {
				final long delta = timestamp - tweenStart;
				final double progress = 1 - delta * inverseSmoothness;
				
				var easedProgress = progress;
				for (var i = 1; i < CONFIG.easingExponent; i++)
					easedProgress *= progress;
				easedProgress = 1 - easedProgress;
				
				return fromZoom + ((zoom - fromZoom) * easedProgress);
			}
		}
		
		return zoom;
	}
	
	public static double transformFOV(final double original) {
		var zoom = getZoom();
		
		if (CONFIG.useQuadratic) {
			zoom *= zoom;
		}
		
		return CONFIG.minFOV + ((Math.max(CONFIG.maxFOV, original) - CONFIG.minFOV) * zoom);
	}
	
	public static boolean transformCinematicCamera(final boolean original) {
		if (Zume.CONFIG.enableCinematicZoom && isActive()) {
			return true;
		}
		
		return original;
	}
	
	public static double transformMouseSensitivity(final double original) {
		if (!isActive())
			return original;
		
		final double zoom = getZoom();
		var result = original;
		
		result *= CONFIG.mouseSensitivityFloor + (zoom * (1 - CONFIG.mouseSensitivityFloor));
		
		return result;
	}
	
	private static int sign(final int input) {
		return input >> (Integer.SIZE - 1) | 1;
	}
	
	public static boolean transformHotbarScroll(final int scrollDelta) {
		if (Zume.CONFIG.enableZoomScrolling)
			Zume.scrollDelta += sign(scrollDelta);
		
		return !(Zume.CONFIG.enableZoomScrolling && isActive());
	}
	
	private static double clamp(final double value, final double min, final double max) {
		return Math.max(Math.min(value, max), min);
	}
	
	private static void setZoom(final double targetZoom) {
		if (CONFIG.zoomSmoothnessMs == 0) {
			setZoomNoTween(targetZoom);
			return;
		}
		
		final double currentZoom = getZoom();
		tweenStart = System.currentTimeMillis();
		tweenEnd = tweenStart + CONFIG.zoomSmoothnessMs;
		fromZoom = currentZoom;
		zoom = clamp(targetZoom, 0D, 1D);
	}
	
	private static void setZoomNoTween(final double targetZoom) {
		tweenStart = 0L;
		tweenEnd = 0L;
		fromZoom = -1D;
		zoom = clamp(targetZoom, 0D, 1D);
	}
	
	public static boolean isActive() {
		if (ZUME_PROVIDER == null)
			return false;
		
		if (CONFIG.toggleMode)
			return toggle;
		
		return ZUME_PROVIDER.isZoomPressed();
	}
	
	public static boolean isZooming() {
		return isActive() || (zoom == 1D && tweenEnd != 0L && System.currentTimeMillis() < tweenEnd);
	}
	
	public static int scrollDelta = 0;
	private static boolean toggle = false;
	private static boolean wasHeld = false;
	private static boolean wasZooming = false;
	private static long prevTimestamp;
	
	public static void render() {
		final long timestamp = System.currentTimeMillis();
		final boolean held = ZUME_PROVIDER.isZoomPressed();
		final boolean zooming = isActive();
		
		if (CONFIG.toggleMode && held && !wasHeld)
			toggle = !toggle;
		
		if (zooming) {
			if (!wasZooming) {
				ZUME_PROVIDER.onZoomActivate();
				setZoom(CONFIG.defaultZoom);
			}
			
			final long timeDelta = timestamp - prevTimestamp;
			
			if (CONFIG.enableZoomScrolling && scrollDelta != 0) {
				setZoom(zoom - scrollDelta * CONFIG.zoomSpeed * 4E-3D);
			} else if (ZUME_PROVIDER.isZoomInPressed() ^ ZUME_PROVIDER.isZoomOutPressed()) {
				final double interpolatedIncrement = CONFIG.zoomSpeed * 1E-4D * timeDelta;
				
				if (ZUME_PROVIDER.isZoomInPressed())
					setZoom(zoom - interpolatedIncrement);
				else if (ZUME_PROVIDER.isZoomOutPressed())
					setZoom(zoom + interpolatedIncrement);
			}
		} else if (wasZooming) {
			setZoom(1D);
		}
		
		scrollDelta = 0;
		prevTimestamp = timestamp;
		wasHeld = held;
		wasZooming = zooming;
	}
	
}
