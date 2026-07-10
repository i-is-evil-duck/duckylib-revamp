package com.j3ly.duckylib.editor.commands;

import com.j3ly.duckylib.DuckyLib;
import com.j3ly.duckylib.editor.EditorOverlay;
import com.j3ly.duckylib.gui.DuckyScreen;
import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.layout.LayoutSerializer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;

public class EditorCommand {

    private static DuckyScreen currentEditorScreen;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("duckyeditor")
            .executes(ctx -> {
                if (!Minecraft.getInstance().hasSingleplayerServer()) {
                    ctx.getSource().sendFailure(Component.literal("Editor only works in single-player!"));
                    return 0;
                }

                EditorOverlay.toggle();

                if (EditorOverlay.isActive()) {
                    if (!(Minecraft.getInstance().screen instanceof DuckyScreen)) {
                        currentEditorScreen = createEditorScreen();
                        Minecraft.getInstance().setScreen(currentEditorScreen);
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal("§a[DuckyLib] Editor enabled. Click widgets to edit."), false);
                } else {
                    if (Minecraft.getInstance().screen == currentEditorScreen) {
                        Minecraft.getInstance().setScreen(null);
                        currentEditorScreen = null;
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal("§c[DuckyLib] Editor disabled."), false);
                }
                return 1;
            })
            .then(Commands.literal("export")
                .then(Commands.argument("filename", StringArgumentType.word())
                    .executes(ctx -> {
                        String filename = StringArgumentType.getString(ctx, "filename");
                        Widget root = getRootWidget();
                        if (root == null) {
                            ctx.getSource().sendFailure(Component.literal("No DuckyLib screen is open!"));
                            return 0;
                        }

                        String toml = LayoutSerializer.serialize(root);
                        File file = new File("duckylib_exports/" + filename + ".toml");
                        file.getParentFile().mkdirs();
                        try {
                            java.nio.file.Files.write(file.toPath(), toml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            ctx.getSource().sendSuccess(() -> Component.literal("§aExported to: " + file.getAbsolutePath()), false);
                        } catch (Exception e) {
                            ctx.getSource().sendFailure(Component.literal("Export failed: " + e.getMessage()));
                            return 0;
                        }
                        return 1;
                    })
                )
                .executes(ctx -> {
                    Widget root = getRootWidget();
                    if (root == null) {
                        ctx.getSource().sendFailure(Component.literal("No DuckyLib screen is open!"));
                        return 0;
                    }

                    String toml = LayoutSerializer.serialize(root);
                    Minecraft.getInstance().keyboardHandler.setClipboard(toml);
                    ctx.getSource().sendSuccess(() -> Component.literal("§aLayout copied to clipboard!"), false);
                    return 1;
                })
            )
        );

        dispatcher.register(Commands.literal("de").redirect(dispatcher.getRoot().getChild("duckyeditor")));
    }

    private static DuckyScreen createEditorScreen() {
        Panel root = new Panel("root", 0, 0, 0, 0);

        Label title = new Label("title", 20, 20, "DuckyLib Editor");
        title.setScale(2.0f);
        title.setCentered(false);
        root.addChild(title);

        Label hint = new Label("hint", 20, 50, "Add or edit widgets using the property panel on the right.");
        hint.setCentered(false);
        root.addChild(hint);

        Button demoButton = new Button("demo_btn", 20, 80, 120, 30, "Demo Button");
        demoButton.setOnClick(() -> DuckyLib.LOGGER.info("Demo button clicked!"));
        root.addChild(demoButton);

        Checkbox demoCheckbox = new Checkbox("demo_cb", 20, 130, "Enable Feature");
        root.addChild(demoCheckbox);

        return DuckyScreen.fromWidget(root);
    }

    private static Widget getRootWidget() {
        if (Minecraft.getInstance().screen instanceof DuckyScreen) {
            return ((DuckyScreen) Minecraft.getInstance().screen).getRootWidget();
        }
        return null;
    }
}
