package com.gyzer.Command;

import com.gyzer.Command.Sub.SetCommand;
import com.gyzer.TreasureChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commands implements TabExecutor, CommandExecutor {
    private final TreasureChest treasureChest = TreasureChest.getTreasureChest();
    private static HashMap<String, CommandProvider> commands;
    public static void register(){

        commands = new HashMap<>();
        commands.put("set",new SetCommand());
    }
    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String s,  String[] args) {
        int length = args.length;
        if (length == 0){
            //发送指令提示

        }
        else {

            String subCommandName = args[0];
            HashMap<String,CommandProvider> map=commands;
            CommandProvider cmd = map.get(subCommandName);
            if (cmd == null){
                //sender.sendMessage(configManager.plugin+configManager.unknown_command);
                return false;
            }
            if (sender.hasPermission(cmd.getPermission())) {
                if (length == cmd.getLength()) {
                    cmd.handle(sender, args);
                    return true;
                }
            }
            else {
                //sender.sendMessage(configManager.plugin + configManager.permission);
                return true;
            }
            //sender.sendMessage(configManager.plugin+configManager.unknown_command);
            return false;
        }

        return true;
    }

    @Override
    public  List<String> onTabComplete( CommandSender commandSender,  Command command,  String s,  String[] args) {
        int length = args.length;
        List<String> tab=new ArrayList<>();
        if (length == 1 ){
            for (Map.Entry<String,CommandProvider> entry:commands.entrySet()){
                CommandProvider legendaryCommand=entry.getValue();
                if ((legendaryCommand.isAdmin() && commandSender.isOp()) || (commandSender.hasPermission(legendaryCommand.getPermission()))){
                    tab.add(entry.getKey());
                }
            }
            return tab;
        }
        else {
            String subCommand = args[0];
            HashMap<String,CommandProvider> map=commands;
            CommandProvider legendaryCommand = map.get(subCommand);
            if (legendaryCommand != null){
                if ((legendaryCommand.isAdmin() && commandSender.isOp()) || (commandSender.hasPermission(legendaryCommand.getPermission()))){
                    return legendaryCommand.complete(commandSender,args);
                }
            }
        }
        return null;
    }
}
