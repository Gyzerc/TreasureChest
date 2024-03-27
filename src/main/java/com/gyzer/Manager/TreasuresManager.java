package com.gyzer.Manager;

import com.gyzer.Data.Treasure;
import com.gyzer.TreasureChest;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class TreasuresManager {
    private HashMap<String, Treasure> caches;
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    private File file;
    private YamlConfiguration yml;
    public TreasuresManager() {
        caches = new HashMap<>();
        file = new File(treasureChest.getDataFolder() , "chests.yml");
        if (!file.exists()) {
            treasureChest.saveResource("chests.yml",false);
        }
        yml = YamlConfiguration.loadConfiguration(file);
        loadAllTreasures();
    }
    public Treasure getTreasure(String id){
        return caches.get(id);
    }

    private void loadAllTreasures() {
        ConfigurationSection section = yml.getConfigurationSection("chests");
        if (section != null) {
            for (String id : section.getKeys(false)) {
                String display = MsgUtils.msg(yml.getString(id+".display"));
                int cooldown = yml.getInt(id+".cooldown",10);
                double percent = yml.getDouble(id+".percent",0.5);
                List<ItemStack> items = new ArrayList<>();
                ConfigurationSection itemSection = section.getConfigurationSection(id+".items");
                if (itemSection != null) {
                    for (String slotStr : itemSection.getKeys(false)) {
                        ItemStack i = itemSection.getItemStack(slotStr);
                        items.add(i);
                    }
                }
                caches.put(id,new Treasure(id,display,percent,cooldown,items));
            }
        }
        treasureChest.info("成功加载 "+caches.size()+" 个物资箱.", Level.INFO);
    }

    public void update(Treasure treasure){
        long time = System.currentTimeMillis();
        caches.put(treasure.getId(),treasure);
        String id = treasure.getId();
        yml.set("chests."+id+".cooldown",treasure.getCooldown());
        yml.set("chests."+id+".percent",treasure.getPercent());
        yml.set("chests."+id+".items",null);
        if (!treasure.getItems().isEmpty()) {
            int a = 0;
            for (ItemStack i : treasure.getItems()) {
                yml.set("chests."+id+".items."+a,i);
                a++;
            }
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        treasureChest.info("成功保持物资箱 "+id+" 耗时 "+(System.currentTimeMillis()- time)+"ms",Level.INFO);
    }
}
