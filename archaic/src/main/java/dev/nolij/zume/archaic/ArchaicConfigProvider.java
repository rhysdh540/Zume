package dev.nolij.zume.archaic;

import cpw.mods.fml.client.config.GuiConfig;
import dev.nolij.zumegradle.proguard.ProGuardKeep;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.gui.ForgeGuiFactory;

@SuppressWarnings("unused")
public class ArchaicConfigProvider extends ForgeGuiFactory {
	
	@ProGuardKeep
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return Class.forName("dev.nolij.zume.vintage.VintageZumeConfigGUI").asSubclass(GuiConfig.class);
	}
	
}
