package fr.skyost.tickets;

import java.io.File;
import org.bukkit.plugin.Plugin;

import fr.skyost.tickets.utils.Config;

public class MessagesFile extends Config {
	
	public String Messages_1 = "§1/player/ has made a ticket :/n/'/ticket/'/n/His location is [World : /world/, X : /x/, Y : /y/, Z : /z/.]/n/The priority is /priority/.";
	public String Messages_2 = "Your ticket has been created.";
	public String Messages_3 = "§2You have claimed the ticket of /player/.";
	public String Messages_4 = "§2Your ticket has been claimed by /player/. You can see the progress by using the command '/mytickets'.";
	public String Messages_5 = "§4You have already claimed this ticket !";
	public String Messages_6 = "§4You need to claim this ticket if you want to use this command.";
	public String Messages_7 = "§4This ticket does not exists !";
	public String Messages_8 = "§2You have changed the status of this ticket to : /status/.";
	public String Messages_9 = "§2/player/ has changed your ticket's status to : /status/.";
	public String Messages_10 = "§2Done !";
	public String Messages_11 = "§4Your ticket has been deleted by /player/.";
	public String Messages_12 = "§4Nothing to display.";
	public String Messages_13 = "§2You have no new ticket(s), good job !";
	public String Messages_14 = "§4You you cannot post a ticket anymore. Please wait that a moderator clear your ticket(s).";
	public String Messages_15 = "You have /n/ new ticket(s).";
	public String Messages_16 = "The above list is scrollable.";
	public String Messages_17 = "§4The world specified in this ticket was not found. Please try to teleport yourself at the ticket's location.";
	public String Messages_18 = "§4This ticket has been created from a remote installation and a data is missing. You cannot be teleported.";
	
    public MessagesFile(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "messages.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n              Skyotickets Configuration               #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/skyotickets #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
    }

}
