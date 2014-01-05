package fr.skyost.tickets;

import java.io.File;
import fr.skyost.tickets.utils.Config;

public class ConfigFile extends Config {
	
	public String FormattedString = "[STATUS : /status/]/n/[ID : /id/]/n/[DATE : /date/]/n/[PLAYER : /player/]/n/[MESSAGE : /message/]/n/[WORLD : /world/, X : /x/, Y : /y/, Z /z/]/n/[CLAIMED BY : /owner/]";
	public String DateFormat = "MM-dd-yyyy HH:mm:ss";
	public String TicketsFolder;
	public int MaxTicketsByPlayer = -1;
	public boolean EnableUpdater = true;
	
    public ConfigFile(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n              Skyotickets Configuration               #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/skyotickets #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
		
		TicketsFolder = new File(dataFolder + System.getProperty("file.separator", "\\") + "tickets").getPath();
    }

}