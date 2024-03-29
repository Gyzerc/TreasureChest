package com.gyzer.Command.Sub;

import com.gyzer.Command.CommandProvider;
import com.gyzer.Data.Treasure;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;

public class SetCommand extends CommandProvider {
    public SetCommand( ) {
        super("", "set", 2, true);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String id = args[1];
        if (sender instanceof Player) {
            Treasure treasure = treasureChest.getTreasuresManager().getTreasure(id);
            if (treasure != null) {
                Player p = (Player) sender;
                treasureChest.getCachesManager().setTreasureChest(p.getLocation(),treasure);
                //
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new CommandTabBuilder().addTab(treasureChest.getTreasuresManager().getTreasures(),1, Collections.singletonList("set"),0).build(args);
    }
}
