package xyz.areapvp.areapvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if(sender.hasPermission("areapvp.use")){
            sender.sendMessage("近日公開");
        }
        return true;
    }
}
