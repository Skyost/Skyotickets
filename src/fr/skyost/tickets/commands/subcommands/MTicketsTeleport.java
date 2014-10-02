package fr.skyost.tickets.commands.subcommands;

import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.tickets.utils.Utils;

public class MTicketsTeleport implements CommandInterface {

	@Override
	public final String[] getNames() {
		return new String[]{"teleport"};
	}

	@Override
	public final boolean mustBePlayer() {
		return true;
	}

	@Override
	public final String getPermission() {
		return "ticket.teleport.ticket";
	}

	@Override
	public final int getMinArgsLength() {
		return 2;
	}

	@Override
	public final String getUsage() {
		return "[player] [id]";
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) throws IOException {
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
		if(ticket.addOwner(((Player)sender).getUniqueId())) {
			sender.sendMessage(Skyotickets.messages.message5);
			return true;
		}
		((Player)sender).teleport(ticket.getLocation());
		return true;
	}

}
