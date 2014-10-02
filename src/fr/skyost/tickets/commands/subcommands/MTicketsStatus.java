package fr.skyost.tickets.commands.subcommands;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.tickets.utils.Utils;

public class MTicketsStatus implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"status"};
	}

	@Override
	public final boolean mustBePlayer() {
		return false;
	}

	@Override
	public final String getPermission() {
		return "ticket.status.ticket";
	}

	@Override
	public final int getMinArgsLength() {
		return 3;
	}

	@Override
	public final String getUsage() {
		return "[player] [id] [status]";
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) throws IOException {
		if(!sender.hasPermission("ticket.status.ticket")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
			return true;
		}
		if(!Utils.isTicketStatus(args[2])) {
			sender.sendMessage(ChatColor.RED + "Status :");
			for(final TicketStatus available : TicketStatus.values()) {
				sender.sendMessage(ChatColor.RED + available.name());
			}
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
		if(!(sender instanceof Player) || !Arrays.asList(ticket.getOwners()).contains(((Player)sender).getUniqueId())) {
			sender.sendMessage(Skyotickets.messages.message6);
			return true;
		}
		final TicketStatus status = TicketStatus.valueOf(args[3]);
		ticket.setStatus(status);
		ticket.saveToFile();
		sender.sendMessage(Skyotickets.messages.message8.replace("/status/", status.name()));
		if(player.isOnline()) {
			player.getPlayer().sendMessage(Skyotickets.messages.message9.replace("/player/", sender.getName()).replace("/status/", args[2].toUpperCase()));
		}
		return true;
	}

}
