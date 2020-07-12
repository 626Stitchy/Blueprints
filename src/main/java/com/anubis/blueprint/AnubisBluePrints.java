package com.anubis.blueprint;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AnubisBluePrints extends JavaPlugin
{
    private static AnubisBluePrints INSTANCE;

    public List<BluePrint> BluePrints = new ArrayList<>();

    public static AnubisBluePrints getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        PluginDescriptionFile pdfFile = getDescription();
        Logger logger = getLogger();
        registerConfig();
        saveDefaultConfig();
        registerCommands();
        registerEvents();
        loadBluePrints();

        logger.info(pdfFile.getName() + " Has been enabled with version " + pdfFile.getVersion());
    }

    @Override
    public void onDisable()
    {
        PluginDescriptionFile pdfFile = getDescription();
        Logger logger = getLogger();

        logger.info(pdfFile.getName() + " Has been disabled");
    }

    public void loadBluePrints() {
        if (this.getConfig().getConfigurationSection("blueprints") != null) {
            for (String tmp : this.getConfig().getConfigurationSection("blueprints").getKeys(false)) {
                this.BluePrints.add(new BluePrint(Integer.parseInt(tmp), getBluePrintData(tmp, "name"), getBluePrintData(tmp, "bluePrintDisplayName"), getBluePrintData(tmp, "uniqueIdentifier"), getBluePrintData(tmp, "secondaryMaterial"), getBluePrintData(tmp, "propName"), getBluePrintData(tmp, "propDescription"), getBluePrintData(tmp, "propMaterial"), getBluePrintData(tmp, "propCommand"), Integer.parseInt(getBluePrintData(tmp, "xpLevelRequired"))));
            }
        }
    }

    public String getBluePrintData(String id, String key) {
        return getConfig().get("blueprints." + id + "." + key).toString();
    }

    private void registerConfig()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void registerCommands()
    {
        getCommand("blueprint").setExecutor(new BluePrintCommand());
    }

    public void registerEvents()
    {
        getServer().getPluginManager().registerEvents(new AnvilEvent(), this);
    }

    public String getConfigStringValue(String key) {
        return getConfig().get(key).toString();
    }

    public String convertColors(String input) {
        return input.replaceAll("&", "§");
    }

    public boolean bluePrintExists(String bluePrintName) {
        return this.BluePrints.stream().anyMatch(x -> x.Name.equals(bluePrintName));
    }

    public String getNameFromBluePrintType(String bluePrintType) {
        return this.BluePrints.stream().filter(x -> x.UniqueIdentifier.equals(bluePrintType)).findFirst().get().Name;
    }

    public BluePrint getBluePrintFromType(String bluePrintType) {
        return this.BluePrints.stream().filter(x -> x.UniqueIdentifier.equals(bluePrintType)).findFirst().get();
    }
}