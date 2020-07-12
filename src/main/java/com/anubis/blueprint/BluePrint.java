package com.anubis.blueprint;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class BluePrint {
    public int Id;
    public String Name;
    public String BluePrintDisplayName;
    public String UniqueIdentifier;
    public Material SecondaryMaterial;
    public String PropName;
    public String PropDescription;
    public Material PropMaterial;
    public String PropCommand;
    public int XpLevelRequired;

    public BluePrint(int Id, String Name, String BluePrintDisplayName, String UniqueIdentifier, String SecondaryMaterial, String PropName, String PropDescription, String PropMaterial, String PropCommand, int XpLevelRequired) {
        this.Id = Id;
        this.Name = Name;
        this.BluePrintDisplayName = BluePrintDisplayName;
        this.UniqueIdentifier = UniqueIdentifier;
        System.out.println(SecondaryMaterial);
        this.SecondaryMaterial = Material.getMaterial(SecondaryMaterial);
        this.PropName = PropName;
        this.PropDescription = PropDescription;
        this.PropMaterial = Material.getMaterial(PropMaterial);
        this.PropCommand = PropCommand;
        this.XpLevelRequired = XpLevelRequired;
    }

    public static ItemStack createBluePrint(int durability, String blueprintName) {
        AnubisBluePrints main = AnubisBluePrints.getInstance();
        NamespacedKey durabilityLeft = new NamespacedKey(AnubisBluePrints.getInstance(), "durability-left");
        NamespacedKey blueprintType = new NamespacedKey(AnubisBluePrints.getInstance(), "blueprint-type");

        ItemStack bluePrint = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = bluePrint.getItemMeta();

        BluePrint blueprint = main.BluePrints.stream().filter(x -> x.Name.equals(blueprintName)).findFirst().get();

        meta.setDisplayName(main.convertColors(blueprint.BluePrintDisplayName));

        meta.getPersistentDataContainer().set(durabilityLeft, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(blueprintType, PersistentDataType.STRING, blueprint.UniqueIdentifier);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(main.convertColors(main.getConfigStringValue("secondaryMaterialLore").replaceAll("%secondarymaterial%", blueprint.SecondaryMaterial.name())));
        lore.add(main.convertColors(main.getConfigStringValue("xpCostLore").replaceAll("%levels%", String.valueOf(blueprint.XpLevelRequired))));
        lore.add(main.convertColors(main.getConfigStringValue("durabilityLore").replaceAll("%durability%", String.valueOf(durability))));
        meta.setLore(lore);

        bluePrint.setItemMeta(meta);

        return bluePrint;
    }

    public static boolean isBluePrint(ItemStack item) {
        NamespacedKey blueprintType = new NamespacedKey(AnubisBluePrints.getInstance(), "blueprint-type");
        return item.getItemMeta().getPersistentDataContainer().has(blueprintType, PersistentDataType.STRING);
    }

    public static String getBluePrintType(ItemStack stack) {
        NamespacedKey key = new NamespacedKey(AnubisBluePrints.getInstance(), "blueprint-type");

        if(stack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ) {
            return stack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }

        return "";
    }
}
