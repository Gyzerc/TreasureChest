package com.gyzer.Data;

import com.gyzer.TreasureChest;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class Treasure {
    private String id;
    private String display;
    private double percent;
    private int cooldown;
    List<ItemStack> items;

    public Treasure(String id, String display, double percent, int cooldown, List<ItemStack> items) {
        this.id = id;
        this.display = display;
        this.percent = percent;
        this.cooldown = cooldown;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public double getPercent() {
        return percent;
    }

    public String getDisplay() {
        return display;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void update() {
        TreasureChest.getTreasureChest().getTreasuresManager().update(this);
    }
}
