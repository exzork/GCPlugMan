package me.exzork.GCPlugMan;

import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.plugin.PluginConfig;
import me.exzork.GCPlugMan.commands.PluginCommand;

import java.util.HashMap;

public class GCPlugMan extends Plugin {
    private static GCPlugMan instance;

    public static GCPlugMan getInstance() {
        return instance;
    }

    private PluginConfig configuration;

    @Override
    public void onLoad() {
        this.getLogger().info("load plugman3");
        super.onLoad();
    }

    @Override
    public void onEnable() {
        this.getHandle().registerCommand(new PluginCommand());
        super.onEnable();
    }
}
