package dev.nolij.zume.cosmicreach.quilt;

import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.impl.QuiltLoaderImpl;
import dev.crmodders.cosmicquilt.api.entrypoint.client.ClientModInitializer;
import dev.nolij.zume.impl.CameraPerspective;
import dev.nolij.zume.impl.IZumeImplementation;
import dev.nolij.zume.impl.Zume;

public class CosmicQuiltZume implements ClientModInitializer, IZumeImplementation {
	@Override
	public void onInitializeClient(ModContainer modContainer) {
		if (QuiltLoaderImpl.INSTANCE.getEnvironmentType() != EnvType.CLIENT)
			return;
		
		Zume.LOGGER.info("Loading Modern Zume...");
		
		Zume.registerImplementation(this, QuiltLoaderImpl.INSTANCE.getConfigDir());
		if (Zume.disabled)
			return;
		
	}
	
	@Override
	public boolean isZoomPressed() {
		return false;
	}
	
	@Override
	public boolean isZoomInPressed() {
		return false;
	}
	
	@Override
	public boolean isZoomOutPressed() {
		return false;
	}
	
	@Override
	public CameraPerspective getCameraPerspective() {
		return null;
	}
}
