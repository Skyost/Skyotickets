package fr.skyost.tickets.commands.subcommands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.tickets.utils.Utils;

public class MTicketsView implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"view"};
	}

	@Override
	public final boolean mustBePlayer() {
		return false;
	}

	@Override
	public final String getPermission() {
		return null;
	}

	@Override
	public final int getMinArgsLength() {
		return 0;
	}

	@Override
	public final String getUsage() {
		return "<player> <id>";
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) throws IOException {
		if(args.length == 0) {
			if(!sender.hasPermission("ticket.view.all")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			final HashMap<UUID, Ticket[]> tickets = SkyoticketsAPI.getTickets();
			if(tickets == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			for(final Entry<UUID, Ticket[]> entry : tickets.entrySet()) {
				final OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
				sender.sendMessage(ChatColor.BOLD + (player == null ? Skyotickets.config.nobody : player.getName()));
				for(final Ticket playersTickets : entry.getValue()) {
					sender.sendMessage(playersTickets.getFormattedString());
					sender.sendMessage(ChatColor.GOLD + "-------------------------------");
				}
			}
		}
		else if(args.length == 1) {
			if(!sender.hasPermission("ticket.view.player")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			sender.sendMessage(ChatColor.BOLD + args[0]);
			final OfflinePlayer player = Utils.getPlayerByArgument(args[0]);
			if(player == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			final Ticket[] tickets = SkyoticketsAPI.getPlayerTickets(player.getUniqueId());
			if(tickets == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			for(final Ticket playerTickets : tickets) {
				sender.sendMessage(playerTickets.getFormattedString());
				sender.sendMessage(ChatColor.GOLD + "-------------------------------");
			}
		}
		else {
			if(!sender.hasPermission("ticket.view.ticket")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			final OfflinePlayer player = Utils.getPlayerByArgument(args[0]);
			if(player == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			final Ticket ticket = SkyoticketsAPI.getTicket(player.getUniqueId(), args[2]);
			if(ticket == null) {
				sender.sendMessage(Skyotickets.messages.message7);
				return true;
			}
			sender.sendMessage(ticket.getFormattedString());
		}
		return true;
	}

}
