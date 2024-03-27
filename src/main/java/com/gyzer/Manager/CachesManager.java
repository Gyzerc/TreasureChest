package com.gyzer.Manager;

import com.gyzer.Data.Treasure;
import com.gyzer.Data.TreasureData;
import com.gyzer.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CachesManager {
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    private File file;
    private YamlConfiguration yml;
    private ConcurrentHashMap<UUID, TreasureData> caches;
    public CachesManager() {
        caches = new ConcurrentHashMap<>();
        this.file = new File(treasureChest.getDataFolder(),"data.yml");
        if (!file.exists()) {
            treasureChest.saveResource("data.yml",false);
            treasureChest.info("成功创建物资箱数据文件.",Level.INFO);
        }
        yml = YamlConfiguration.loadConfiguration(file);

        loadAllDatas();

        Bukkit.getScheduler().runTaskTimerAsynchronously(treasureChest,()->{
            caches.forEach(((uuid, treasureData) -> {
                Treasure treasure = treasureChest.getTreasuresManager().getTreasure(treasureData.getTreasure());
                long begin = treasureData.getBeginRefreshTime() / 1000;
                int hour = 0;
                int min = 0;
                int sec = 0;
                if (begin != -1) {
                    int less = (int) ((System.currentTimeMillis() - begin) / 1000);

                    int TotalMin = less / 60;

                    hour = (int)(TotalMin / 60);
                    min = TotalMin - (  (int) (hour * 60) );
                    sec = (TotalMin - (hour * 60) - min) * 60;

                    if (less >= (treasure.getCooldown() * 60)) {
                        treasureData.setBeginRefreshTime(-1);
                        treasureData.refresh();
                    }
                }
                for (Entity entity: treasureData.getLocation().getNearbyEntities(5,5,5)) {
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        PlayerHologramManager.PlayerHologram playerHologram = treasureChest.getPlayerHologramManager().getPlayerHologram(p);
                        playerHologram.update(p,treasureData,treasure.getDisplay(),(hour+":"+min+":"+sec));
                    }
                }
            }));
        },20,20);
    }
    private void loadAllDatas() {
        ConfigurationSection section = yml.getConfigurationSection("data");
        if (section != null) {
            for (String uuidStr : section.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                String treasure = section.getString(uuidStr+".treasure");
                long begin = section.get(uuidStr+".begin") != null ? section.getLong(uuidStr+".begin") : -1;
                Location loc = section.getLocation(uuidStr+".location");
                HashMap<Integer, ItemStack> items = new HashMap<>();
                ConfigurationSection itemsSection = section.getConfigurationSection(uuidStr+".items");
                if (begin != -1 && itemsSection != null) {
                    for (String slotStr : itemsSection.getKeys(false)) {
                        items.put(Integer.parseInt(slotStr),itemsSection.getItemStack(slotStr));
                    }
                }
                caches.put(uuid,new TreasureData(uuid,treasure,items,begin,loc));
            }
        }
        treasureChest.info("加载 "+caches.size()+" 个物资缓存.",Level.INFO);
    }
    public void onDisable() {
        int a = 0;
        for (Map.Entry<UUID,TreasureData> entry:caches.entrySet()) {
            UUID uuid = entry.getKey();
            TreasureData treasureData = entry.getValue();
            String uuidStr = uuid.toString();
            yml.set("data."+uuidStr,null);
            yml.set("data."+uuidStr+".treasure",treasureData.getTreasure());
            yml.set("data."+uuidStr+".begin",treasureData.getBeginRefreshTime());
            yml.set("data."+uuidStr+".location",treasureData.getLocation());
            if (!treasureData.getItems().isEmpty()) {
                treasureData.getItems().forEach((integer, itemStack) -> {
                    yml.set("data."+uuidStr+".items."+integer,itemStack);
                });
            }
            a++;
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        treasureChest.info("保存 "+a+" 个物资箱缓存.",Level.INFO);
    }
}
