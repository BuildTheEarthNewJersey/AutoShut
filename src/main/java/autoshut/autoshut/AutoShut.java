package autoshut.autoshut;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoShut extends JavaPlugin {

    //Data Fields
    private boolean playerAnnouncements;
    private boolean debug;
    private LocalTime shutdown;
    private List<String> warningSeconds;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();

        //try instantiating variables
        try{
            playerAnnouncements = this.getConfig().getBoolean("player-announcements");
            debug = this.getConfig().getBoolean("debug");
            shutdown = LocalTime.parse(getConfig().getString("shutdown-time"));
            warningSeconds = (List<String>) getConfig().getList("warning-seconds");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("[AutoShut] A fatal error has occurred in the config! Resetting to default settings and enabling debug mode.");
            playerAnnouncements = true;
            debug = true;
            shutdown = LocalTime.parse("03:30:00");
            warningSeconds = new ArrayList<String>(){{
                add("300");
                add("60");
                add("5");
            }};
        }

        //instantiate commands
        getCommand("getTime").setExecutor(new GetTime());

        //Repeating Tasks
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run() {
                if (LocalTime.now().getHour() == shutdown.getHour() && LocalTime.now().getMinute() == shutdown.getMinute())
                    Bukkit.shutdown();
                for (String key : warningSeconds){
                    if (((Long) ChronoUnit.SECONDS.between(LocalTime.now(), shutdown)).equals(Long.parseLong(key))) {
                        System.out.println("[AutoShut] Server will be restarting in " + key + " seconds");
                        if (playerAnnouncements) {
                            if (Integer.parseInt(key) > 60)
                                announce("Server restart in " + (Integer.parseInt(key)/60) + " minutes.");
                            else
                                announce("Server restart in " + key + " seconds.");
                        }
                    }
                }
            }
        }, 0L, 1*20); //repeats every second

        if (debug)
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
                @Override
                public void run(){
                    System.out.println("[AutoShut] DEBUG SCREEN APPEARS EVERY 20 SECONDS");
                    System.out.println("[AutoShut] DEBUG: Local Time: " + LocalTime.now());
                    System.out.println("[AutoShut] DEBUG: Shutdown Time: " + shutdown);
                    System.out.println("[AutoShut] DEBUG: Seconds Until Shutdown " + ChronoUnit.SECONDS.between(LocalTime.now(), shutdown));
                    System.out.println("[AutoShut] DEBUG: Player Announcements: " + playerAnnouncements);
                    System.out.print("[AutoShut] DEBUG: Warning Intervals (Seconds) :");
                    for (String key : warningSeconds)
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
