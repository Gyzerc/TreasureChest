package com.gyzer.Manager;

import com.gyzer.TreasureChest;
import com.gyzer.Utils.MsgUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    public String plugin;
    public String lang_hologram_top;
    public String lang_hologram_bottom_cooldown;
    public String lang_hologram_bottom_claim;
    public String lang_claim;
    public ConfigManager() {

        File file = new File(treasureChest.getDataFolder(),"config.yml");
        if (!file.exists()){
            treasureChest.saveResource("config.yml",false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        plugin = MsgUtils.msg(yml.getString("lang.plugin"));
        lang_hologram_top = MsgUtils.msg(yml.getString("lang.hologram.head"));
        lang_hologram_bottom_cooldown = MsgUtils.msg(yml.getString("lang.hologram.bottom.cooldown"));
        lang_hologram_bottom_claim = MsgUtils.msg(yml.getString("lang.hologram.bottom.claim"));
        lang_claim = MsgUtils.msg(yml.getString("lang.claim"));

    }
}
