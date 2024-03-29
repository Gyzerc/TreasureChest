package com.gyzer.Command.Sub;

import com.gyzer.Command.CommandProvider;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends CommandProvider {
    public ReloadCommand( ) {
        super("", "reload", 1, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        treasureChest.reload();
        sender.sendMessage(treasureChest.getConfigManager().plugin+" 成功重载配置文件.");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
