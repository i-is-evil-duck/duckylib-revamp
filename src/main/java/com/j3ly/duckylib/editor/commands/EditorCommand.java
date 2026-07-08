package com.j3ly.duckylib.editor.commands;

import com.j3ly.duckylib.DuckyLib;
import com.j3ly.duckylib.editor.EditorOverlay;
import com.j3ly.duckylib.gui.DuckyScreen;
import com.j3ly.duckylib.gui.widget.Widget;
import com.j3ly.duckylib.layout.LayoutSerializer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;

public class EditorCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("duckyeditor")
            .executes(ctx -> {
                if (!ctx.getSource().getLevel().isClientSide) {
                    ctx.getSource().sendFailure(Component.literal("Editor only works on client!"));
                    return 0;
                }

                if (!Minecraft.getInstance().hasSingleplayerServer()) {
                    ctx.getSource().sendFailure(Component.literal("Editor only works in single-player!"));
                    return 0;
                }

                EditorOverlay.toggle();

                if (EditorOverlay.isActive()) {
                    ctx.getSource().sendSuccess(() -> Component.literal("§a[DuckyLib] Editor enabled. Click widgets to edit."), false);
                } else {
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

    private static Widget getRootWidget() {
        if (Minecraft.getInstance().screen instanceof DuckyScreen) {
            return ((DuckyScreen) Minecraft.getInstance().screen).getRootWidget();
        }
        return null;
    }
}
