package com.gyzer;

import com.gyzer.Manager.CachesManager;
import com.gyzer.Manager.PlayerHologramManager;
import com.gyzer.Manager.ProtocoLibManager;
import com.gyzer.Manager.TreasuresManager;
import com.gyzer.Utils.HologramUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class TreasureChest extends JavaPlugin {

    private static TreasureChest treasureChest;
    private TreasuresManager treasuresManager;
    private CachesManager cachesManager;
    private ProtocoLibManager protocoLibManager;
    private PlayerHologramManager playerHologramManager;

    @Override
    public void onEnable() {
        treasureChest = this;
        treasuresManager = new TreasuresManager();
        cachesManager = new CachesManager();
        protocoLibManager = new ProtocoLibManager();
        playerHologramManager = new PlayerHologramManager();
    }
    @Override
    public void onDisable() {
        cachesManager.onDisable();
    }

    private int random = -1;
    @Override
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
    }

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

    public void info(String str, Level level) {
        getLogger().log(level,str);
    }

}
