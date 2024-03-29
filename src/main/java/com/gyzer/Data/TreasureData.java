package com.gyzer.Data;

import com.gyzer.TreasureChest;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class TreasureData {
    private UUID uuid;
    private String treasure;
    private HashMap<Integer, ItemStack> items;
    private long beginRefreshTime;
    private Location location;

    public TreasureData(UUID uuid, String treasure, HashMap<Integer, ItemStack> items, long beginRefreshTime, Location location) {
        this.uuid = uuid;
        this.treasure = treasure;
        this.items = items;
        this.beginRefreshTime = beginRefreshTime;
        this.location = location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTreasure() {
        return treasure;
    }

    public HashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public long getBeginRefreshTime() {
        return beginRefreshTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setItems(HashMap<Integer, ItemStack> items) {
        this.items = items;
    }

    public void setBeginRefreshTime(long beginRefreshTime) {
        this.beginRefreshTime = beginRefreshTime;
    }

    public void refresh() {
        TreasureChest.getTreasureChest().getCachesManager().random(this);
    }

    public void update() {
        TreasureChest.getTreasureChest().getCachesManager().update(this);
    }
}
