package dev.nolij.zume;

import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.gui.FabricStatusTree;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zume.legacy.LegacyZume;
import dev.nolij.zume.lexforge.LexZume;
import dev.nolij.zume.lexforge16.LexZume16;
import dev.nolij.zume.lexforge18.LexZume18;
import dev.nolij.zume.modern.ModernZume;
import dev.nolij.zume.primitive.PrimitiveZume;
import dev.nolij.zume.vintage.VintageZume;
import dev.nolij.zumegradle.proguard.ProGuardKeep;
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
		
		String FABRIC_MISSING_DEPENDENCY_MESSAGE = """
		Failed to detect which variant of Zume to load! Ensure all dependencies are installed:
		    Fabric (14.4+): Fabric API (fabric-key-binding-api-v1)
		    Legacy Fabric (6.4-12.2): Legacy Fabric API (legacy-fabric-keybinding-api-v1-common)
		    Babric (b7.3): Station API (station-keybindings-v0)""";
		
		Zume.LOGGER.error(FABRIC_MISSING_DEPENDENCY_MESSAGE);
		FabricGuiEntry.displayError("Incompatible mods found!", null, tree -> {
			var tab = tree.addTab("Error");
			tab.node.addMessage(FABRIC_MISSING_DEPENDENCY_MESSAGE, FabricStatusTree.FabricTreeWarningLevel.ERROR);
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
		if (ZumeMixinPlugin.ZUME_VARIANT == null) {
			throw new AssertionError("""
                Mixins did not load! Zume requires Mixins in order to work properly.
                Please install one of the following mixin loaders:
                14.4 - 16.0: MixinBootstrap
                8.9 - 12.2: MixinBooter >= 5.0
                7.10 - 12.2: UniMixins >= 0.1.15""");
		}
		
		switch (ZumeMixinPlugin.ZUME_VARIANT) {
			case ZumeMixinPlugin.MODERN -> new ModernZume().onInitializeClient();
			case ZumeMixinPlugin.LEGACY -> new LegacyZume().onInitializeClient();
			case ZumeMixinPlugin.PRIMITIVE -> new PrimitiveZume().onInitializeClient();
			case ZumeMixinPlugin.LEXFORGE -> new LexZume();
			case ZumeMixinPlugin.LEXFORGE18 -> new LexZume18();
			case ZumeMixinPlugin.LEXFORGE16 -> new LexZume16();
			case ZumeMixinPlugin.VINTAGE_FORGE -> new VintageZume();
		}
	}
	
}
