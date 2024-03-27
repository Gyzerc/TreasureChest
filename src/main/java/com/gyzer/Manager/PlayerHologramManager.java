package com.gyzer.Manager;

import com.gyzer.Data.TreasureData;
import com.gyzer.TreasureChest;
import com.gyzer.Utils.HologramUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerHologramManager {

    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    private final ProtocoLibManager protocoLibManager = treasureChest.getProtocoLibManager();

    private HashMap<Player,PlayerHologram> caches;

    public PlayerHologramManager() {
        caches = new HashMap<>();
    }

    public PlayerHologram getPlayerHologram(Player p){
        return caches.getOrDefault(p,new PlayerHologram(new HashMap<>()));
    }

    public class PlayerHologram {

        private HashMap<UUID,Hologram> holograms;

        public PlayerHologram(HashMap<UUID, Hologram> holograms) {
            this.holograms = holograms;
        }

        public void update(Player p, TreasureData data, String str1, String str2) {
            UUID uuid = data.getUuid();
            Hologram hologram = holograms.get(uuid);
            if (hologram != null) {
                protocoLibManager.sendPacket(p,
                        HologramUtils.armorStand(hologram.getId1(),str1),
                        HologramUtils.armorStand(hologram.getId2(),str2));
            }
            else {
                int id1 = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                int id2 = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
                hologram = new Hologram(id1,id2);


                Location loc = data.getLocation();
                Location loc1 = loc.clone().add(0,1.5,0);
                Location loc2 = loc.clone().add(0,1,0);

                holograms.put(uuid,hologram);
                protocoLibManager.sendPacket(p,
                        HologramUtils.spawn(id1,loc1, EntityType.ARMOR_STAND),
                        HologramUtils.spawn(id2,loc2, EntityType.ARMOR_STAND),
                        HologramUtils.armorStand(hologram.getId1(),str1),
                        HologramUtils.armorStand(hologram.getId2(),str2));
            }
        }

        public void remove(Player p,UUID uuid) {
            Hologram hologram = holograms.get(uuid);
            if (hologram != null) {
                protocoLibManager.sendPacket(p,
                        HologramUtils.destory(hologram.getId1()),
                        HologramUtils.destory(hologram.getId2()));
            }
        }
    }
    public class Hologram {
        private int id1;
        private int id2;

        public Hologram(int id1, int id2) {
            this.id1 = id1;
            this.id2 = id2;
        }

        public int getId1() {
            return id1;
        }

        public int getId2() {
            return id2;
        }
    }
}
