package fr.skyost.tickets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.utils.Utils;

public class MyTicketsCommandExecutor implements CommandExecutor {
	
	@Override
	public final boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		final String senderName = sender.getName();
		try {
			if(Skyotickets.config.logUse) {
				Utils.log(Utils.date() + " " + senderName + " has performed a Skyotickets command : /" + label + " " + Joiner.on(' ').join(args));
			}
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must perform this command from the game !");
				return true;
			}
			if(!sender.hasPermission("ticket.mytickets")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			final Ticket[] tickets = SkyoticketsAPI.getPlayerTickets(((Player)sender).getUniqueId());
			if(tickets == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			for(final Ticket ticket : tickets) {
				sender.sendMessage(ticket.getFormattedString());
				sender.sendMessage(ChatColor.GOLD + "-------------------------------");
			}
			sender.sendMessage(Skyotickets.messages.message16);
		}
		catch(final Exception ex) {
			sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex.getClass().getName() + "'. Please notify your server admin.");
			if(Skyotickets.config.logUse) {
				Utils.log(Utils.date() + " " + senderName + " " + ex);
			}
			ex.printStackTrace();
		}
		return true;
	}

}
