package dev.nolij.zume.modern.integration;

import dev.nolij.zume.api.platform.v1.ZumeAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModernZumeConfigScreen extends Screen {
	
	private final Screen parent;
	
	public ModernZumeConfigScreen(Component arg, Screen parent) {
		super(arg);
		this.parent = parent;
	}
	
	@Override
	public void init() {
		ZumeAPI.openConfigFile();
		
		Minecraft.getInstance().setScreen(parent);
	}
	
	@SuppressWarnings("unused")
	public void render(int mouseX, int mouseY, float delta) {
		init();
	}
	
}
