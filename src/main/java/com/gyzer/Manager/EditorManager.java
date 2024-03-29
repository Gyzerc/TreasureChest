package com.gyzer.Manager;

import com.gyzer.Data.Treasure;
import com.gyzer.Data.TreasureData;
import com.gyzer.TreasureChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EditorManager implements Listener {
    private HashMap<String,Editor> cache;
    public EditorManager() {
        cache = new HashMap<String, Editor>();
        chat = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, TreasureChest.getTreasureChest());
    }

    public Editor getEditor(Treasure treasure) {
        String id = treasure.getId();
        Editor editor = cache.get(id);
        if (editor == null) {
            editor = new Editor(treasure);
            cache.put(id,editor);
        }
        return editor;
    }

    private HashMap<Player,DoubleStore<Treasure,Editor>> chat;
    public class Editor implements InventoryHolder {
        private boolean setData;
        private Inventory inv;
        private Treasure treasure;
        private Location location;
        public Editor(Treasure treasure) {
            this.setData = false;
            this.treasure = treasure;
            this.inv = Bukkit.createInventory(Editor.this,54,"编辑: "+treasure.getDisplay());
            for (int slot = 45; slot < 54;slot++){
                ItemStack i = new ItemStack(Material.GREEN_STAINED_GLASS);
                ItemMeta meta = i.getItemMeta();
                meta.setDisplayName(" ");
                i.setItemMeta(meta);
                inv.setItem(slot,i);
            }
            ItemStack i = new ItemStack(Material.GREEN_WOOL);
            ItemMeta meta = i.getItemMeta();
            meta.setDisplayName("[ 点击保存 ]");
            i.setItemMeta(meta);
            inv.setItem(49,i);

            int slot = 0;
            for (ItemStack item : treasure.getItems()) {
                if (slot < 45) {
                    inv.setItem(slot,item);
                }
                slot++;
            }
        }

        public boolean isSetData() {
            return setData;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public void setSetData(boolean setData) {
            this.setData = setData;
        }

        public void opne(Player p){
            Bukkit.getScheduler().runTask(TreasureChest.getTreasureChest(),()->p.openInventory(inv));
        }

        @Override
        public  Inventory getInventory() {
            return inv;
        }



        public void onClick(InventoryClickEvent e) {
            if (e.getRawSlot() >= 45 && e.getRawSlot() <=53){
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                if (e.getRawSlot() == 49) {
                    String id = treasure.getId();
                    List<ItemStack> items = new ArrayList<>();
                    for (int slot = 0; slot < 45; slot++) {
                        ItemStack i = inv.getItem(slot);
                        if (i != null && !i.getType().equals(Material.AIR)) {
                            items.add(i);
                        }
                    }
                    treasure.setItems(items);
                    p.closeInventory();
                    p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+"接下来发送聊天发送: 完整率;刷新时间");
                    p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+" 例如: 0.5;10 -> 50%完整率，10分刷新间隔");
                    chat.put(p,new DoubleStore<Treasure, Editor>(treasure,this));
                }
            }
        }
    }

    @EventHandler
    public void onIN(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof Editor) {
            Editor editor = (Editor) e.getInventory().getHolder();
            editor.onClick(e);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if (chat.containsKey(e.getPlayer())) {
            Player p =e.getPlayer();
            e.setCancelled(true);
            String msg = e.getMessage();
            String[] args = msg.split(";");
            if (args.length > 1){
                if (getDouble(args[0]) && getInt(args[1])) {
                    DoubleStore<Treasure, Editor> doubleStore = chat.remove(p);
                    Treasure treasure = doubleStore.getV1();
                    double percent = Double.parseDouble(args[0]);
                    int cooldown = Integer.parseInt(args[1]);
                    treasure.setPercent(percent);
                    treasure.setCooldown(cooldown);
                    treasure.update();
                    p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+"成功保存本次编辑.");

                    Editor editor = doubleStore.getV2();
                    if (editor.isSetData() && editor.getLocation() != null) {
                        Bukkit.getScheduler().runTask(TreasureChest.getTreasureChest(),()->
                                TreasureChest.getTreasureChest().getCachesManager().setTreasureChest(editor.getLocation(),treasure));
                    }
                    return;
                }
                p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+"请输入正确的完整率小数或者正确的时间冷却数字.");
                return;
            }
            p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+"接下来发送聊天发送: 完整率;刷新时间");
            p.sendMessage(TreasureChest.getTreasureChest().getConfigManager().plugin+" 例如: 0.5;10 -> 50%完整率，10分刷新间隔");
        }
    }

    public boolean getDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }
    public boolean getInt(String str) {
        try {
             Integer.parseInt(str);
             return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }


    public class DoubleStore<T,K> {
        private T v1;
        private K v2;

        public DoubleStore(T v1, K v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public T getV1() {
            return v1;
        }

        public K getV2() {
            return v2;
        }

        public void setV1(T v1) {
            this.v1 = v1;
        }

        public void setV2(K v2) {
            this.v2 = v2;
        }
    }
}
