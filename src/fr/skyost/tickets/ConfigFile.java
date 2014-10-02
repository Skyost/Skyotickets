package fr.skyost.tickets;

import java.io.File;
import java.util.Arrays;

import fr.skyost.tickets.utils.Skyoconfig;

public class ConfigFile extends Skyoconfig {
	
	@ConfigOptions(name = "strings.nobody")
	public String nobody = "Nobody";
	@ConfigOptions(name = "strings.formatted-ticket")
	public String formattedString = "[ID : /id/]/n/[PRIORITY : /priority/]/n/[STATUS : /status/]/n/[DATE : /date/]/n/[PLAYER : /player/]/n/[MESSAGE : /message/]/n/[WORLD : /world/, X : /x/, Y : /y/, Z : /z/]/n/[CLAIMED BY : /owners/]";
	@ConfigOptions(name = "strings.date-format")
	public String dateFormat = "MM/dd/yyyy HH:mm:ss";
	
	@ConfigOptions(name = "settings.play-sound")
	public boolean playSound = true;
	@ConfigOptions(name = "settings.tickets-directory")
	public String ticketsDir;
	@ConfigOptions(name = "settings.max-tickets-player")
	public int maxTicketsByPlayer = -1;
	@ConfigOptions(name = "settings.logs.use")
	public boolean logUse = false;
	@ConfigOptions(name = "settings.logs.file")
	public String logFile;
	
	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;
	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	
    public ConfigFile(final File dataFolder) {
    	super(new File(dataFolder, "config.yml"), Arrays.asList("Skyotickets Configuration", "http://dev.bukkit.org/bukkit-plugins/skyotickets for more informations"));
		ticketsDir = new File(dataFolder + File.separator + "tickets").getPath();
		logFile = new File(dataFolder, "log.txt").getPath();
    }

}