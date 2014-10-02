package fr.skyost.tickets.commands.subcommands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.tickets.utils.Utils;

public class MTicketsDelete implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"delete", "remove"};
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
			if(!sender.hasPermission("ticket.delete.all")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			for(final File file : SkyoticketsAPI.getTicketsDirectory().listFiles()) {
				Utils.delete(file);
			}
		}
		else if(args.length == 1) {
			if(!sender.hasPermission("ticket.delete.player")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			final OfflinePlayer player = Utils.getPlayerByArgument(args[0]);
			if(player == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			final File playerDir = SkyoticketsAPI.getPlayerDir(player.getUniqueId());
			if(playerDir.exists()) {
				Utils.delete(playerDir);
			}
		}
		else {
			if(!sender.hasPermission("ticket.delete.ticket")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
				return true;
			}
			final OfflinePlayer player = Utils.getPlayerByArgument(args[0]);
			if(player == null) {
				sender.sendMessage(Skyotickets.messages.message12);
				return true;
			}
			final Ticket ticket = SkyoticketsAPI.getTicket(player.getUniqueId(), args[1]);
			if(ticket == null) {
				sender.sendMessage(Skyotickets.messages.message7);
				return true;
			}
			if(!(sender instanceof Player) || !Arrays.asList(ticket.getOwners()).contains(((Player)sender).getUniqueId())) {
				sender.sendMessage(Skyotickets.messages.message6);
				return true;
			}
			Utils.delete(ticket.getFile());
			final File playerDir = SkyoticketsAPI.getPlayerDir(ticket.getPlayer());
			if(playerDir.list().length == 0) {
				Utils.delete(playerDir);
			}
			sender.sendMessage(Skyotickets.messages.message10);
			if(player.isOnline()) {
				player.getPlayer().sendMessage(Skyotickets.messages.message11.replace("/player/", sender.getName()));
			}
		}
		sender.sendMessage(Skyotickets.messages.message10);
		return true;
	}

}
