package com.gyzer.Manager;

import com.gyzer.Data.Treasure;
import com.gyzer.Data.TreasureData;
import com.gyzer.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CachesManager {
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    private final ConfigManager configManager = treasureChest.getConfigManager();
    private File file;
    private YamlConfiguration yml;
    private ConcurrentHashMap<UUID, TreasureData> caches;
    private ConcurrentHashMap<Location, TreasureData> caches_loc;
    public CachesManager() {
        caches = new ConcurrentHashMap<>();
        caches_loc = new ConcurrentHashMap<>();
        this.file = new File(treasureChest.getDataFolder(),"data.yml");
        if (!file.exists()) {
            treasureChest.saveResource("data.yml",false);
            treasureChest.info("成功创建物资箱数据文件.",Level.INFO);
        }
        yml = YamlConfiguration.loadConfiguration(file);

        loadAllDatas();

        Bukkit.getScheduler().runTaskTimerAsynchronously(treasureChest,()->{
            caches.forEach(((uuid, treasureData) -> {
                String id = treasureData.getTreasure();
                if (id != null) {
                    Treasure treasure = treasureChest.getTreasuresManager().getTreasure(id);
                    if (treasure != null) {
                        long begin = treasureData.getBeginRefreshTime();
                        int min = 0;
                        int sec = 0;
                        String bottom = configManager.lang_hologram_bottom_claim;
                        if (begin != -1) {
                            int currentSeconds = (int) ((System.currentTimeMillis() - begin) / 1000);
                            int targetSeconds = treasure.getCooldown() * 60;
                            int less = targetSeconds - currentSeconds;
                            if (less <= 0) {
                                treasureData.setBeginRefreshTime(-1);
                                Bukkit.getScheduler().runTask(treasureChest, treasureData::refresh);
                            }
                            else {
                                min = less / 60;
                                sec = less - ( min * 60);
                            }
                            bottom = configManager.lang_hologram_bottom_cooldown.replace("%minutes%", String.valueOf(min)).replace("%seconds%", String.valueOf(sec));

                        }
                        for (Entity entity : treasureData.getLocation().getWorld().getNearbyEntities(treasureData.getLocation(), 5, 5, 5)) {
                            if (entity instanceof Player) {
                                Player p = (Player) entity;
                                PlayerHologramManager.PlayerHologram playerHologram = treasureChest.getPlayerHologramManager().getPlayerHologram(p);
                                playerHologram.update(p, treasureData, configManager.lang_hologram_top.replace("%name%", treasure.getDisplay()), bottom);
                            }
                        }
                    }
                }
            }));

            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerHologramManager.PlayerHologram playerHologram = treasureChest.getPlayerHologramManager().getPlayerHologram(p);
                List<UUID> removes = new ArrayList<>();
                for (UUID uid : playerHologram.getHolograms().keySet()) {
                    TreasureData data = treasureChest.getCachesManager().getData(uid);
                    if (data != null) {
                        Location loc = data.getLocation();
                        if (loc.getWorld().getName().equals(p.getWorld().getName())) {
                            if (loc.distance(p.getLocation()) <= 5) {
                                continue;
                            }
                        }
                        playerHologram.removeHolo(p,uid);
                        removes.add(uid);
                    }
                }
                playerHologram.removeUIDS(p,removes);
            }
        },20,20);
    }

    public TreasureData getData(Location loc) {
        return caches_loc.get(loc);
    }
    public TreasureData getData(UUID uuid) {
        return caches.get(uuid);
    }
    public void remove(Location loc) {
        TreasureData data = caches_loc.remove(loc);
        if (data != null) {
            UUID uuid = data.getUuid();
            caches.remove(uuid);
        }
    }

    public void setTreasureChest(Location location,Treasure treasure) {
        UUID uuid = UUID.randomUUID();
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        TreasureData data = new TreasureData(uuid,treasure.getId(),randomItems(treasure),-1,block.getLocation());
        caches.put(uuid,data);
        caches_loc.put(block.getLocation(),data);

        Container container = (Container) block.getState();
        container.getInventory().clear();

        data.getItems().forEach(((integer, itemStack) -> {
            container.getInventory().setItem(integer,itemStack);
        }));
    }


    public void random(TreasureData data) {
        Location location = data.getLocation();
        if (!location.getBlock().getType().equals(Material.CHEST)) {
            location.getBlock().setType(Material.CHEST);
        }
        Treasure treasure =TreasureChest.getTreasureChest().getTreasuresManager().getTreasure(data.getTreasure());
        if (treasure != null) {
            HashMap<Integer, ItemStack> items = randomItems(treasure);
            data.setItems(items);

            Container container = (Container) location.getBlock().getState();
            container.getInventory().clear();

            items.forEach(((integer, itemStack) -> {
                container.getInventory().setItem(integer,itemStack);
            }));
        }
    }

    public HashMap<Integer, ItemStack> randomItems(Treasure treasure) {
        List<ItemStack> items = treasure.getItems();
        HashMap<Integer, ItemStack> list = new HashMap<>();
        List<Integer> roll = new ArrayList<>();
        if (items.size() > 0) {
            int amount = (int) (treasure.getPercent() * items.size());
            if (amount >= 1) {
                for (int a = 0; a < (Math.min(amount, items.size())) ; a++){
                    list.put(a,findItem(items,roll));
                }
            }
        }
        return list;
    }


    public ItemStack findItem(List<ItemStack> itemStacks,List<Integer> rolls) {
        int roll = (new Random()).nextInt(itemStacks.size());
        if (rolls.contains(roll)) {
            return findItem(itemStacks,rolls);
        }
        rolls.add(roll);
        return itemStacks.get(roll).clone();
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
                TreasureData data = new TreasureData(uuid,treasure,items,begin,loc);
                caches.put(uuid,data);
                caches_loc.put(loc,data);
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

    public void update(TreasureData treasureData) {
        UUID uuid = treasureData.getUuid();
        caches.put(uuid,treasureData);
        caches_loc.put(treasureData.getLocation(),treasureData);
    }
}
