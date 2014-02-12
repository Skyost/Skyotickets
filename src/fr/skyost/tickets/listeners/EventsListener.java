package fr.skyost.tickets.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.threads.RemoteControl;

public class EventsListener implements Listener {
	
	@EventHandler
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if(player.hasPermission("ticket.view.ticket")) {
			try {
				if(Skyotickets.useRemoteDatabase) {
					new RemoteControl(player, "skyotickets player-join " + player.getName()).start();
					return;
				}
				final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
				if(tickets == null) {
					player.sendMessage(Skyotickets.messages.Messages_13);
					return;
				}
				final String playerName = player.getName();
				List<String> owners;
				final ArrayList<String> newTickets = new ArrayList<String>();
				for(final Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
					for(final Ticket ticket : entry.getValue()) {
						owners = ticket.getOwners();
						if(owners.contains(playerName) || (ticket.getStatus() == TicketStatus.OPEN && (owners.size() == 1 && owners.get(0).equals(Skyotickets.config.NoOwner)))) {
							newTickets.add(ticket.getFormattedString());
						}
					}
				}
				for(final String ticket : newTickets) {
					player.sendMessage(ticket);
					player.sendMessage(ChatColor.GOLD + "-------------------------------");
				}
				player.sendMessage(Skyotickets.messages.Messages_15.replaceAll("/n/", String.valueOf(newTickets.size())));
				player.sendMessage(Skyotickets.messages.Messages_16);
			}
			catch(Exception ex) {
				player.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
				ex.printStackTrace();
			}
		}
	}

}