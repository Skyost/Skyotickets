package fr.skyost.tickets.commands;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketPriority;
import fr.skyost.tickets.utils.Utils;

public class TicketCommandExecutor implements CommandExecutor {
	
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
			if(args.length <= 1) {
				return false;
			}
			final UUID uuid = ((Player)sender).getUniqueId();
			final File playerDir = SkyoticketsAPI.getPlayerDir(uuid);
			if(playerDir.exists() && playerDir.listFiles().length == Skyotickets.config.maxTicketsByPlayer) {
				sender.sendMessage(Skyotickets.messages.message14);
				return true;
			}
			if(!Utils.isTicketPriority(args[0])) {
				sender.sendMessage(ChatColor.RED + "Priorities :");
				for(final TicketPriority priority : TicketPriority.values()) {
					sender.sendMessage(ChatColor.RED + priority.name());
				}
				return true;
			}
			final Location location = ((Player)sender).getLocation();
			new Ticket(TicketPriority.valueOf(args[0]), uuid, Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)), location, true, Skyotickets.config.playSound, true);
			sender.sendMessage(Skyotickets.messages.message2);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex.getClass().getName() + "'. Please notify your server's admin.");
			if(Skyotickets.config.logUse) {
				Utils.log(Utils.date() + " " + senderName + " " + ex);
			}
		}
		return true;
	}

}
