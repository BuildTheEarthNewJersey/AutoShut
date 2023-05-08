package autoshut.autoshut;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

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
    /*
    Again, instead of using localtime, we can try using date and timezone to
        make the server time specific to where the server is located (I.e. EST)
     */
    private String getTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(AutoShut.getCurrentTime().toLocalTime());
    }
}
