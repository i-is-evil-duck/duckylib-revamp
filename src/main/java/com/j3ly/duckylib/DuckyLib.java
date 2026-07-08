package com.j3ly.duckylib;

import com.j3ly.duckylib.editor.commands.EditorCommand;
import com.j3ly.duckylib.gui.DuckyScreen;
import com.j3ly.duckylib.gui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DuckyLib.MOD_ID)
public class DuckyLib {
    public static final String MOD_ID = "duckylib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DuckyLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Theme.loadDefault();
            LOGGER.info("DuckyLib client setup complete!");
        });
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        EditorCommand.register(event.getDispatcher());
    }

    public static DuckyScreen loadLayout(ResourceLocation location) {
        return DuckyScreen.fromLayout(location);
    }

    public static void openConfigScreen() {
        Minecraft.getInstance().setScreen(DuckyScreen.fromLayout(
            new ResourceLocation(MOD_ID, "guis/config.toml")
        ));
    }

    public static void openScreen(DuckyScreen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static Theme getTheme() {
        return Theme.getCurrent();
    }

    public static void setTheme(Theme theme) {
        Theme.setCurrent(theme);
    }
}
