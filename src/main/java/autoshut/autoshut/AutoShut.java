package autoshut.autoshut;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoShut extends JavaPlugin {

    //Data Fields9
    private LocalTime shutdown;

    private final boolean PLAYER_ANNOUNCEMENTS = this.getConfig().getBoolean("player-announcements");
    private final boolean DEBUG = this.getConfig().getBoolean("debug");
    private final LocalTime SHUTDOWN = LocalTime.parse(getConfig().getString("shutdown-time"));
    private List<String> WARNING_SECONDS = (List<String>) getConfig().getList("warning-seconds");

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run() {
                if (LocalTime.now().getHour() == SHUTDOWN.getHour() && LocalTime.now().getMinute() == SHUTDOWN.getMinute())
                    Bukkit.shutdown();
                for (String key : WARNING_SECONDS){
                    if (((Long) ChronoUnit.SECONDS.between(LocalTime.now(), SHUTDOWN)).equals(Long.parseLong(key))) {
                        System.out.println("[AutoShut] Server will be restarting in " + key + " seconds");
                        if (PLAYER_ANNOUNCEMENTS) {
                            if (Integer.parseInt(key) > 60)
                                announce("Server restart in " + (Integer.parseInt(key)/60) + " minutes.");
                            else
                                announce("Server restart in " + key + " seconds.");
                        }
                    }
                }
            }
        }, 0L, 1*20); //repeats every second

        if (DEBUG)
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
                @Override
                public void run(){
                    System.out.println("[AutoShut] DEBUG SCREEN APPEARS EVERY 20 SECONDS");
                    System.out.println("[AutoShut] DEBUG: Local Time: " + LocalTime.now());
                    System.out.println("[AutoShut] DEBUG: Shutdown Time: " + SHUTDOWN);
                    System.out.println("[AutoShut] DEBUG: Seconds Until Shutdown " + ChronoUnit.SECONDS.between(LocalTime.now(), SHUTDOWN));
                    System.out.println("[AutoShut] DEBUG: Player Announcements: " + PLAYER_ANNOUNCEMENTS);
                    System.out.print("[AutoShut] DEBUG: Warning Intervals (Seconds) :");
                    for (String key : WARNING_SECONDS)
                        System.out.print(" " + key);
                    System.out.println();
            }
        }, 0L, 20*20); //repeats every 20 seconds
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void announce(String message) { //v1_16_R3
        for (Player player : Bukkit.getOnlinePlayers())
            player.sendTitle("", ChatColor.RED + message, 10, 80, 123);
    }
}
