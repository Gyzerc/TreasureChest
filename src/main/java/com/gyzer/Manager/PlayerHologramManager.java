package com.gyzer.Manager;

import com.gyzer.Data.TreasureData;
import com.gyzer.TreasureChest;
import com.gyzer.Utils.HologramUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
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
        PlayerHologram playerHologram = caches.get(p);
        if (playerHologram != null) {
            return playerHologram;
        }
        playerHologram = new PlayerHologram(new HashMap<>());
        caches.put(p,playerHologram);
        return playerHologram;
    }

    public class PlayerHologram {

        private HashMap<UUID,Hologram> holograms;

        public PlayerHologram(HashMap<UUID, Hologram> holograms) {
            this.holograms = holograms;
        }

        public HashMap<UUID, Hologram> getHolograms() {
            return holograms;
        }

        public void setHolograms(HashMap<UUID, Hologram> holograms) {
            this.holograms = holograms;
        }

        public void update(Player p, TreasureData data, String str1, String str2) {
            PlayerHologram playerHologram = getPlayerHologram(p);



            UUID uuid = data.getUuid();
            Hologram hologram = playerHologram.getHolograms().get(uuid);

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
                Location loc1 = loc.clone().add(0.5,-0.8,0.5);
                Location loc2 = loc.clone().add(0.5,-1.1,0.5);;

                HashMap<UUID,Hologram> hologramHashMap = playerHologram.getHolograms();
                hologramHashMap.put(uuid,hologram);
                playerHologram.setHolograms(hologramHashMap);
                caches.put(p,playerHologram);

                protocoLibManager.sendPacket(p,
                        HologramUtils.spawn(id1,loc1, EntityType.ARMOR_STAND),
                        HologramUtils.spawn(id2,loc2, EntityType.ARMOR_STAND),
                        HologramUtils.armorStand(hologram.getId1(),str1),
                        HologramUtils.armorStand(hologram.getId2(),str2));
            }
        }

        public void removeHolo(Player p,UUID uuid) {
            PlayerHologram playerHologram = getPlayerHologram(p);
            HashMap<UUID,Hologram> hologramHashMap = playerHologram.getHolograms();
            Hologram hologram = hologramHashMap.get(uuid);
            playerHologram.setHolograms(holograms);
            caches.put(p,playerHologram);

            if (hologram != null) {
                protocoLibManager.sendPacket(p,
                        HologramUtils.destory(hologram.getId1()),
                        HologramUtils.destory(hologram.getId2()));
            }

        }

        public void removeUIDS(Player p,List<UUID> uids) {
            PlayerHologram playerHologram = getPlayerHologram(p);
            HashMap<UUID,Hologram> hologramHashMap = playerHologram.getHolograms();
            uids.forEach(hologramHashMap::remove);
            playerHologram.setHolograms(hologramHashMap);
            caches.put(p,playerHologram);
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
