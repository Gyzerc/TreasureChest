package com.gyzer;

import com.gyzer.Command.Commands;
import com.gyzer.Manager.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class TreasureChest extends JavaPlugin {

    private static TreasureChest treasureChest;
    private TreasuresManager treasuresManager;
    private CachesManager cachesManager;
    private ProtocoLibManager protocoLibManager;
    private PlayerHologramManager playerHologramManager;
    private EditorManager editorManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        treasureChest = this;
        configManager = new ConfigManager();
        treasuresManager = new TreasuresManager();
        cachesManager = new CachesManager();
        protocoLibManager = new ProtocoLibManager();
        playerHologramManager = new PlayerHologramManager();
        editorManager = new EditorManager();

        Bukkit.getPluginManager().registerEvents(new EventListeners(),this);
        Bukkit.getPluginCommand("TreasureChest").setExecutor(new Commands());
        Bukkit.getPluginCommand("TreasureChest").setTabCompleter(new Commands());
        Commands.register();
    }
    @Override
    public void onDisable() {
        cachesManager.onDisable();
    }

    public void reload() {
        configManager = new ConfigManager();
        treasuresManager = new TreasuresManager();
    }

   /* @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (random != -1) {
            protocoLibManager.sendPacket(player,
                    //HologramUtils.spawn(random, location, EntityType.ARMOR_STAND),
                    HologramUtils.armorStand(random, "测试222")
            );
           ///protocoLibManager.send(player, HologramUtils.destory(random));
        }
        else {
            Location location = player.getLocation();
            random = (new Random()).nextInt(Integer.MAX_VALUE);
            System.out.println("随机: "+random);
            protocoLibManager.sendPacket(player,
                    HologramUtils.spawn(random, location, EntityType.ARMOR_STAND),
                    HologramUtils.armorStand(random, "测试")
            );
        }
        return true;
    }*/

    public static TreasureChest getTreasureChest() {
        return treasureChest;
    }

    public TreasuresManager getTreasuresManager() {
        return treasuresManager;
    }

    public CachesManager getCachesManager() {
        return cachesManager;
    }

    public ProtocoLibManager getProtocoLibManager() {
        return protocoLibManager;
    }

    public PlayerHologramManager getPlayerHologramManager() {
        return playerHologramManager;
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void info(String str, Level level) {
        getLogger().log(level,str);
    }

}
