package fr.skyost.tickets;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fr.skyost.tickets.utils.Config;

public class ConfigFile extends Config {
	
	public String FormattedString = "[STATUS : /status/]/n/[ID : /id/]/n/[DATE : /date/]/n/[PLAYER : /player/]/n/[MESSAGE : /message/]/n/[WORLD : /world/, X : /x/, Y : /y/, Z : /z/]/n/[CLAIMED BY : /owner/]";
	public String DateFormat = "MM-dd-yyyy HH:mm:ss";
	public String NoData = "No data";
	public String TicketsFolder;
	public int MaxTicketsByPlayer = -1;
	public boolean EnableUpdater = true;
	
	public boolean Socket_Use = false;
	public boolean Socket_Log = true;
	public String Socket_Host = "localhost";
	public int Socket_Port = 4343;
	public ArrayList<String> Socket_WhiteListedAdress = new ArrayList<String>();
	public String Socket_Password = "password";
	public String Socket_Name = "Admin";
	
    public ConfigFile(final File dataFolder) throws UnknownHostException {
		CONFIG_FILE = new File(dataFolder, "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n              Skyotickets Configuration               #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/skyotickets #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
		
		TicketsFolder = new File(dataFolder + File.separator + "tickets").getPath();
		Socket_WhiteListedAdress.add("127.0.0.1");
    }

}