package autoshut.autoshut;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalTime;

public class GetTime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (command.getName().equalsIgnoreCase("getTime")){
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp())
                    player.sendMessage(ChatColor.DARK_RED + "[AutoShut] : " + getTime());
                else
                    player.sendMessage(ChatColor.DARK_RED + "You must be op to use this command!");
                return true;
            } else {
                sender.sendMessage("[AutoShut] : " + getTime());
                return true;
            }
        }

        return false;
    }

    private LocalTime getTime(){
        return LocalTime.now();
    }
}
