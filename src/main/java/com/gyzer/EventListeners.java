package com.gyzer;

import com.gyzer.Data.Treasure;
import com.gyzer.Data.TreasureData;
import com.gyzer.Manager.EditorManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventListeners implements Listener {
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    public EventListeners() {
        chat = new HashMap<>();
    }
    private HashMap<Player,Location> chat;
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND && e.getClickedBlock().getType().equals(Material.CHEST)) {
            if (p.isSneaking()) {
                ItemStack i = p.getInventory().getItemInMainHand();
                if (i != null && !i.getType().equals(Material.AIR)) {
                    if (i.getType().equals(Material.CLOCK) && p.isOp()) {
                        e.setCancelled(true);
                        TreasureData data = treasureChest.getCachesManager().getData(e.getClickedBlock().getLocation());
                        if (data != null) {
                            Treasure treasure = treasureChest.getTreasuresManager().getTreasure(data.getTreasure());
                            if (treasure != null) {
                                EditorManager.Editor editor = treasureChest.getEditorManager().getEditor(treasure);
                                editor.opne(p);
                                return;
                            }
                            p.sendMessage(treasureChest.getConfigManager().plugin+"检测到该箱子的物资id: "+treasure.getId()+" 不存在，已自动移除该箱子.");
                            treasureChest.getCachesManager().remove(e.getClickedBlock().getLocation());
                            return;
                        }
                        else {
                            e.setCancelled(true);
                            p.sendMessage(treasureChest.getConfigManager().plugin+"创建新的物资箱子，请在聊天栏发送物资箱的ID 输入'cancel'取消创建..");
                            chat.put(p,e.getClickedBlock().getLocation());
                        }
                    }
                }
            } else {
                Location location = e.getClickedBlock().getLocation();
                TreasureData data = treasureChest.getCachesManager().getData(location);
                if (data != null) {
                    Treasure treasure = treasureChest.getTreasuresManager().getTreasure(data.getTreasure());
                    if (treasure != null) {
                        if (data.getBeginRefreshTime() == -1) {
                            data.setBeginRefreshTime(System.currentTimeMillis());
                            data.update();
                            p.sendMessage(treasureChest.getConfigManager().plugin + treasureChest.getConfigManager().lang_claim);
                            return;
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (chat.containsKey(p)) {
            Location location = chat.remove(p);
            String id = e.getMessage();
            if (id.equalsIgnoreCase("cancel")) {
                p.sendMessage(treasureChest.getConfigManager().plugin+"已取消本次创建...");
                return;
            }
            Treasure treasure = treasureChest.getTreasuresManager().getTreasure(id);
            if (treasure != null) {
                p.sendMessage(treasureChest.getConfigManager().plugin+"该物资箱id已经存在...");
                return;
            }
            EditorManager.Editor editor = treasureChest.getEditorManager().getEditor(new Treasure(id,id,1,10,new ArrayList<>()));
            editor.setLocation(location);
            editor.setSetData(true);
            editor.opne(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        chat.remove(e.getPlayer());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.CHEST)) {
            Location location = e.getBlock().getLocation();
            TreasureData data = treasureChest.getCachesManager().getData(location);
            if (data != null) {
                Player p = e.getPlayer();
                if (p.isOp()) {
                    treasureChest.getCachesManager().remove(location);
                    p.sendMessage(treasureChest.getConfigManager().plugin+" 成功删除该物资箱..");
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}
