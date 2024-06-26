package dev.nolij.zume.modern.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.nolij.zume.api.util.v1.MethodHandleHelper;
import dev.nolij.zume.impl.Zume;
import dev.nolij.zumegradle.proguard.ProGuardKeep;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.function.Function;

import static dev.nolij.zume.impl.ZumeConstants.MOD_ID;

@ProGuardKeep.WithObfuscation
public class ZumeModMenuIntegration implements ModMenuApi {
	
	private static final MethodHandle LITERALTEXT_INIT = MethodHandleHelper.PUBLIC.getConstructorOrNull(
		MethodHandleHelper.PUBLIC.getClassOrNull("net.minecraft.class_2585"),
		MethodType.methodType(Component.class, String.class),
		String.class);
	
	@Override
	public String getModId() {
		return MOD_ID;
	}
	
	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return (parent) -> {
            try {
	            //noinspection DataFlowIssue
	            return new ModernZumeConfigScreen((Component) LITERALTEXT_INIT.invokeExact(""), parent);
            } catch (Throwable e) {
	            Zume.LOGGER.error("Error opening config screen: ", e);
				return null;
            }
        };
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> new ModernZumeConfigScreen(Component.literal(""), parent);
	}
	
}
