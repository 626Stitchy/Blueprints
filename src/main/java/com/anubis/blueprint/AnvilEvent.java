package com.anubis.blueprint;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AnvilEvent implements Listener {
    private AnubisBluePrints plugin;

    public AnvilEvent() { this.plugin = AnubisBluePrints.getInstance(); }

    @EventHandler
    public void PrepareAnvilEvent(PrepareAnvilEvent e) {
        if(e.getInventory().getItem(0) == null || e.getInventory().getItem(1) == null || !BluePrint.isBluePrint(e.getInventory().getItem(1))) {
            return;
        }

        BluePrint bluePrint = plugin.getBluePrintFromType(BluePrint.getBluePrintType(e.getInventory().getItem(1)));
        if(e.getInventory().getItem(0).getType() != bluePrint.SecondaryMaterial) {
            return;
        }

        ItemStack prop = new ItemStack(bluePrint.PropMaterial, 1);

        ItemMeta meta = prop.getItemMeta();
        meta.setDisplayName(plugin.convertColors(bluePrint.PropName));
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

        List<String> lore = new ArrayList<>();
        lore.add(plugin.convertColors(bluePrint.PropDescription));
        meta.setLore(lore);

        prop.setItemMeta(meta);
        e.setResult(prop);

        plugin.getServer().getScheduler().runTask(plugin, () -> e.getInventory().setRepairCost(bluePrint.XpLevelRequired));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ANVIL || e.getInventory().getItem(0) == null || e.getInventory().getItem(1) == null)
            return;

        if(e.getSlotType() != InventoryType.SlotType.RESULT || BluePrint.getBluePrintType(e.getInventory().getItem(1)).equals("")) {
            return;
        }

        NamespacedKey blueprintTypeKey = new NamespacedKey(plugin, "blueprint-type");
        String blueprintType = e.getInventory().getItem(1).getItemMeta().getPersistentDataContainer().get(blueprintTypeKey, PersistentDataType.STRING);
        BluePrint bluePrint = plugin.getBluePrintFromType(blueprintType);

        if(bluePrint.SecondaryMaterial != e.getClickedInventory().getItem(0).getType()) {
            return;
        }

        if(e.getInventory().getItem(1).getAmount() > 1) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(plugin.convertColors("&cYou can only use 1 blueprint at a time!"));
            return;
        }

        if(e.getWhoClicked().getExpToLevel() < bluePrint.XpLevelRequired) {
            e.getWhoClicked().sendMessage(plugin.convertColors("&cYou don't have enough experience to do this!"));
            e.setCancelled(true);
            return;
        }
        NamespacedKey durabilityLeft = new NamespacedKey(plugin, "durability-left");

        int oldDurability = e.getInventory().getItem(1).getItemMeta().getPersistentDataContainer().get(durabilityLeft, PersistentDataType.INTEGER);
        int durability = oldDurability - 1;

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), bluePrint.PropCommand.replaceAll("%player%", e.getWhoClicked().getName()));

        if(durability == 0) {
            e.getWhoClicked().sendMessage(plugin.convertColors(plugin.getConfigStringValue("bluePrintBroke")));
            e.getInventory().removeItem(e.getInventory().getItem(0));
            e.getInventory().removeItem(e.getInventory().getItem(1));
            e.getWhoClicked().closeInventory();
            return;
        }

        plugin.getServer().getPlayer(e.getWhoClicked().getName()).setLevel(plugin.getServer().getPlayer(e.getWhoClicked().getName()).getLevel() - bluePrint.XpLevelRequired);

        ItemStack newBluePrint = BluePrint.createBluePrint(durability, plugin.getNameFromBluePrintType(blueprintType));

        e.getWhoClicked().getInventory().addItem(newBluePrint);

        e.getWhoClicked().sendMessage(plugin.convertColors(plugin.getConfigStringValue("bluePrintUsed").replaceAll("%durability%", String.valueOf(durability))));
        e.getInventory().removeItem(e.getInventory().getItem(0));
        e.getInventory().removeItem(e.getInventory().getItem(1));

        e.getWhoClicked().closeInventory();
    }
}
