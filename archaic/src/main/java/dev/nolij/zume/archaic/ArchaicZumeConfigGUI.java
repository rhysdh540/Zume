package dev.nolij.zume.archaic;

import cpw.mods.fml.client.config.GuiConfig;
import dev.nolij.zume.impl.Zume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Collections;

import static dev.nolij.zume.impl.ZumeConstants.MOD_ID;

public class ArchaicZumeConfigGUI extends GuiConfig {
	
	public ArchaicZumeConfigGUI(GuiScreen parentScreen) {
		super(parentScreen, Collections.emptyList(), MOD_ID, false, false, "config");
		
		Zume.openConfigFile();
	}
	
	@Override
	public void initGui() {
		Minecraft.getMinecraft().displayGuiScreen(parentScreen);
	}
	
}
