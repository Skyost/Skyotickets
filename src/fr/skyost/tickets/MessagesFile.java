package fr.skyost.tickets;

import java.io.File;
import java.util.Arrays;

import org.bukkit.ChatColor;

import fr.skyost.tickets.utils.Skyoconfig;

public class MessagesFile extends Skyoconfig {
	
	@ConfigOptions(name = "messages.1")
	public String message1 = ChatColor.DARK_BLUE + "/player/ has made a ticket :/n/" + ChatColor.WHITE + "'/ticket/'" + ChatColor.DARK_BLUE + "/n/His location is [World : /world/, X : /x/, Y : /y/, Z : /z/.]/n/The priority is /priority/.";
	@ConfigOptions(name = "messages.2")
	public String message2 = "Your ticket has been created.";
	@ConfigOptions(name = "messages.3")
	public String message3 = ChatColor.GREEN + "You have claimed the ticket of /player/.";
	@ConfigOptions(name = "messages.4")
	public String message4 = ChatColor.GREEN + "Your ticket has been claimed by /player/. You can see the progress by using the command '/mytickets'.";
	@ConfigOptions(name = "messages.5")
	public String message5 = ChatColor.DARK_RED + "You have already claimed this ticket !";
	@ConfigOptions(name = "messages.6")
	public String message6 = ChatColor.DARK_RED + "You need to claim this ticket if you want to use this command.";
	@ConfigOptions(name = "messages.7")
	public String message7 = ChatColor.DARK_RED + "This ticket does not exists !";
	@ConfigOptions(name = "messages.8")
	public String message8 = ChatColor.GREEN + "You have changed the status of this ticket to : /status/.";
	@ConfigOptions(name = "messages.9")
	public String message9 = ChatColor.GREEN + "/player/ has changed your ticket's status to : /status/.";
	@ConfigOptions(name = "messages.10")
	public String message10 = ChatColor.GREEN + "Done !";
	@ConfigOptions(name = "messages.11")
	public String message11 = ChatColor.DARK_RED + "Your ticket has been deleted by /player/.";
	@ConfigOptions(name = "messages.12")
	public String message12 = ChatColor.DARK_RED + "Nothing to display.";
	@ConfigOptions(name = "messages.13")
	public String message13 = ChatColor.GREEN + "You have no new ticket(s), good job !";
	@ConfigOptions(name = "messages.14")
	public String message14 = ChatColor.DARK_RED + "You you cannot post a ticket anymore. Please wait that a moderator clear your ticket(s).";
	@ConfigOptions(name = "messages.15")
	public String message15 = "You have /n/ new ticket(s).";
	@ConfigOptions(name = "messages.16")
	public String message16 = "The above list is scrollable.";
	@ConfigOptions(name = "messages.17")
	public String message17 = ChatColor.DARK_RED + "This player does not have any ticket.";
	
    public MessagesFile(final File dataFolder) {
    	super(new File(dataFolder, "messages.yml"), Arrays.asList("Skyotickets Messages"));
    }

}
