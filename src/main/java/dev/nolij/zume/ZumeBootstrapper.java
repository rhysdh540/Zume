package dev.nolij.zume;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.gui.FabricStatusTree;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zumegradle.proguard.ProGuardKeep;
import dev.rdh.pcl.PackedClassLoader;
import net.minecraftforge.fml.common.Mod;

import static dev.nolij.zume.impl.ZumeConstants.*;

@Mod(
	value = MOD_ID,
	modid = MOD_ID,
	name = MOD_NAME,
	version = MOD_VERSION,
	acceptedMinecraftVersions = VINTAGE_VERSION_RANGE,
	guiFactory = "dev.nolij.zume.vintage.VintageConfigProvider")
public class ZumeBootstrapper {
	
	@ProGuardKeep
	@SuppressWarnings("Java8CollectionRemoveIf") // extra lambda = extra method = extra size
	public static void fabricPreLaunch() {
		if (ZumeMixinPlugin.ZUME_VARIANT != null)
			return;
		
		String message = """
		Failed to detect which variant of Zume to load! Ensure all dependencies are installed:
		    Fabric (14.4+): Fabric API (fabric-key-binding-api-v1)
		    Legacy Fabric (6.4-12.2): Legacy Fabric API (legacy-fabric-keybinding-api-v1-common)
		    Babric (b7.3): Station API (station-keybindings-v0)""";
		
		Zume.LOGGER.error(message);
		FabricGuiEntry.displayError("Incompatible mods found!", null, tree -> {
			var tab = tree.addTab("Error");
			tab.node.addMessage(message, FabricStatusTree.FabricTreeWarningLevel.ERROR);
			var itr = tree.tabs.iterator();
			while (itr.hasNext()) {
				if (itr.next() != tab)
					itr.remove();
			}
		}, true);
	}
	
	@ProGuardKeep
	@SuppressWarnings("InstantiationOfUtilityClass") // it's not a utility class
	public static void fabricInit() {
		new ZumeBootstrapper();
	}
	
	public ZumeBootstrapper() {
		String className =
		switch (ZumeMixinPlugin.ZUME_VARIANT) {
			case ZumeMixinPlugin.MODERN -> "dev.nolij.zume.modern.ModernZume";
			case ZumeMixinPlugin.LEGACY -> "dev.nolij.zume.legacy.LegacyZume";
			case ZumeMixinPlugin.PRIMITIVE -> "dev.nolij.zume.primitive.PrimitiveZume";
			case ZumeMixinPlugin.LEXFORGE -> "dev.nolij.zume.lexforge.LexZume";
			case ZumeMixinPlugin.LEXFORGE18 -> "dev.nolij.zume.lexforge18.LexZume18";
			case ZumeMixinPlugin.LEXFORGE16 -> "dev.nolij.zume.lexforge16.LexZume16";
			case ZumeMixinPlugin.VINTAGE_FORGE -> "dev.nolij.zume.vintage.VintageZume";
			default -> throw new AssertionError("""
                Mixins did not load! Zume requires Mixins in order to work properly.
                Please install one of the following mixin loaders:
                    14.4 - 16.0: MixinBootstrap
                    8.9 - 12.2: MixinBooter >= 5.0
                    7.10 - 12.2: UniMixins >= 0.1.15""");
		};
		
		ClassLoader classLoader = ZumeBootstrapper.class.getClassLoader();
		if(classLoader.getResource("zume.pack") != null) {
			System.out.println("Loading packed classloader");
			classLoader = new PackedClassLoader(ZumeBootstrapper.class.getClassLoader(), "zume.pack");
			Thread.currentThread().setContextClassLoader(classLoader);
		} else {
			System.out.println("Loading normal classloader");
		}
		
		try {
			Object o = classLoader.loadClass(className).getDeclaredConstructor().newInstance();
			if (o instanceof ClientModInitializer)
				((ClientModInitializer) o).onInitializeClient();
		} catch (NoClassDefFoundError e) {
			if (!e.getMessage().contains("net/fabricmc/api/ClientModInitializer")) {
				throw e;
			}
		}
	}
	
}
