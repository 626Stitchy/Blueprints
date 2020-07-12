package com.anubis.blueprint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BluePrintCommand implements CommandExecutor {

    private AnubisBluePrints plugin;

    public BluePrintCommand() {
        this.plugin = AnubisBluePrints.getInstance();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] params) {
        if(params.length == 0) {
            sender.sendMessage(plugin.convertColors("&9BluePrints version " + plugin.getDescription().getVersion()));
            return true;
        }
        if(params[0].equalsIgnoreCase("give")) {
            if(!sender.hasPermission("blueprint.give")) {
                sender.sendMessage(plugin.convertColors("&cYou do not have permissions for this action!"));
            }
            if(plugin.getServer().getPlayer(params[1]) == null) {
                sender.sendMessage(plugin.convertColors(plugin.getConfigStringValue("playerNotFound").replaceAll("%player%", params[1])));
                return false;
            }

            if(!plugin.bluePrintExists(params[2])) {
                sender.sendMessage(plugin.convertColors("&4This blueprint type does not exist!"));
                return false;
            }

            plugin.getServer().getPlayer(params[1]).getInventory().addItem(BluePrint.createBluePrint(Integer.parseInt(params[3]), params[2]));
            return true;
        }
        if(params[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("blueprint.reload")) {
                sender.sendMessage(plugin.convertColors("&cYou do not have permissions for this action!"));
                return true;
            }
            this.plugin.reloadConfig();
            this.plugin.saveConfig();
            this.plugin.loadBluePrints();
            sender.sendMessage(plugin.convertColors(plugin.getConfigStringValue("configReload")));
            return true;
        }

        sender.sendMessage(plugin.convertColors("&cInvalid usage"));

        return true;
    }
}
