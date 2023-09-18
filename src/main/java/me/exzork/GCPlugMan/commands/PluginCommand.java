package me.exzork.GCPlugMan.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.command.Command;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.plugin.PluginManager;
import me.exzork.GCPlugMan.GCPlugMan;
import org.luaj.vm2.ast.Str;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Command(label = "plugman",
        permission = "plugman",
        usage = "list/enable/disable all/name/number",
        targetRequirement = Command.TargetRequirement.NONE)
public class PluginCommand implements CommandHandler {
    PluginManager pluginManager = Grasscutter.getPluginManager();
    Map<String, Plugin> pluginMap = null;
    List<String> pluginNames = new ArrayList<>();

    private HashMap<String, Boolean> pluginStatus = new HashMap<String, Boolean>();

    private void enablePlugin(String pluginName){
        Plugin plugin = pluginMap.get(pluginName);
        if(plugin == null) CommandHandler.sendMessage(null, "Plugin Not Found!");
        else{
            pluginManager.enablePlugin(plugin);
            pluginStatus.put(pluginName, true);
        }
    }

    private void disablePlugin(String pluginName){
        Plugin plugin = pluginMap.get(pluginName);
        if(plugin == null) CommandHandler.sendMessage(null, "Plugin Not Found!");
        else {
            pluginManager.disablePlugin(pluginMap.get(pluginName));
            pluginStatus.put(pluginName, false);
        }
    }

    private int getTabCountFromLongestName(){
        int tab = 0;
        for(String name: pluginNames){
            int tabNew = calculateTabCount(name);
            if(tab < tabNew) tab = tabNew;
        }
        return tab;
    }

    private int calculateTabCount(String name){
        BigDecimal length = (new BigDecimal(3+name.length())).setScale(0, RoundingMode.UP);
        BigDecimal tabCount = length.divide(BigDecimal.valueOf(8), RoundingMode.UP);
        return tabCount.intValue();
    }

    private int calculateTabNeeded(String name){
        BigDecimal length = new BigDecimal(getTabCountFromLongestName()).multiply(BigDecimal.valueOf(8));
        BigDecimal tabNeeded = length.subtract(BigDecimal.valueOf(3+name.length())).divide(BigDecimal.valueOf(8),RoundingMode.UP);
        return tabNeeded.intValue();
    }

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        Field field = null;
        String list = "\nList of Plugin : \n";
        try {
            field = pluginManager.getClass().getDeclaredField("plugins");
            field.setAccessible(true);
            pluginMap = (LinkedHashMap<String,Plugin>) field.get(pluginManager);
            int no = 1;
            for(Map.Entry<String,Plugin> entry : pluginMap.entrySet()){
                String pluginName = entry.getKey();
                if(!pluginName.equals("GCPlugMan")){
                    pluginStatus.putIfAbsent(pluginName, true);
                    if (!pluginNames.contains(pluginName)) pluginNames.add(pluginName);
                    String status = pluginStatus.get(pluginName) ? "Enabled":"Disabled";
                    list += no + ". " + pluginName;
                    for (int i = 0; i < calculateTabNeeded(pluginName); i++) {
                        list += "\t";
                    }
                    list +="[" + status+"]";
                    if (no < pluginMap.size()) list += "\n";
                    no++;
                }
            }
        } catch (Exception e){}
        if(args.size() == 2){
            int no = 0;
            switch (args.get(0)){
                case "disable":
                    try{
                        no = Integer.parseInt(args.get(1));
                    }catch (Exception e){}
                    if(no > pluginNames.size()) {
                        CommandHandler.sendMessage(targetPlayer, "Plugin Not Found!");
                        return;
                    }
                    if(args.get(1).equals("GCPlugMan")) return;
                    if(no>0) disablePlugin(pluginNames.get(no-1));
                    else if (args.get(1).equals("all")) {
                        for(String name : pluginNames){
                            disablePlugin(name);
                        }
                    }
                    else disablePlugin(args.get(1));
                    break;
                case "enable":
                    try{
                        no = Integer.parseInt(args.get(1));
                    }catch (Exception e){}
                    if(no > pluginNames.size()) {
                        CommandHandler.sendMessage(targetPlayer, "Plugin Not Found!");
                        return;
                    }
                    if(args.get(1).equals("GCPlugMan")) return;
                    if(no>0) enablePlugin(pluginNames.get(no-1));
                    else if (args.get(1).equals("all")){
                        for(String name : pluginNames){
                            enablePlugin(name);
                        }
                    }
                    else enablePlugin(args.get(1));
                    break;
                case "list":
                default:
                    CommandHandler.sendMessage(sender, list);
            }
        }else if(args.size() == 1){
            if(args.get(0).equals("list")){
                CommandHandler.sendMessage(sender, list);
            }
        }else{
            this.sendUsageMessage(targetPlayer);
        }
    }
}
