package autoshut.autoshut;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public static String timezone;
    private LocalTime shutdown; //Initialize shutdown variable, which stores the time

    private List<String> warningSeconds;

    public static ZonedDateTime getCurrentTime(){
        ZoneId z = ZoneId.of( timezone);
        return Instant.now().atZone( z );
    }

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
            timezone = this.getConfig().getString("timezone");
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
                if (getCurrentTime().getHour() == shutdown.getHour() && getCurrentTime().getMinute() == shutdown.getMinute())
                    Bukkit.shutdown();
                for (String key : warningSeconds){
                    if (((Long) ChronoUnit.SECONDS.between(getCurrentTime().toLocalTime(), shutdown)).equals(Long.parseLong(key))) {
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
                    System.out.println("[AutoShut] DEBUG: Local Time: " + getCurrentTime().toLocalTime());
                    System.out.println("[AutoShut] DEBUG: Shutdown Time: " + shutdown);
                    System.out.println("[AutoShut] DEBUG: Seconds Until Shutdown " + ChronoUnit.SECONDS.between(getCurrentTime().toLocalTime(), shutdown));
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
