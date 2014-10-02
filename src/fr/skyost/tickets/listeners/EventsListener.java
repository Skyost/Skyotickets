package fr.skyost.tickets.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.skyost.tickets.SkyoticketsAPI;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.Skyotickets;

public class EventsListener implements Listener {
	
	@EventHandler
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if(player.hasPermission("ticket.view.ticket")) {
			try {
				final HashMap<UUID, Ticket[]> allTickets = SkyoticketsAPI.getTickets();
				if(allTickets == null) {
					player.sendMessage(Skyotickets.messages.message13);
					return;
				}
				final UUID playerId = player.getUniqueId();
				final List<String> newTickets = new ArrayList<String>();
				for(final Ticket[] tickets : allTickets.values()) {
					for(final Ticket ticket : tickets) {
						final List<UUID> owners = Arrays.asList(ticket.getOwners());
						if(owners.contains(playerId) || (ticket.getStatus() == TicketStatus.OPEN && (owners.size() == 1 && owners.get(0).equals(Skyotickets.config.nobody)))) {
							newTickets.add(ticket.getFormattedString());
						}
					}
				}
				if(newTickets.size() == 0) {
					player.sendMessage(Skyotickets.messages.message13);
					return;
				}
				for(final String ticket : newTickets) {
					player.sendMessage(ticket);
					player.sendMessage(ChatColor.GOLD + "-------------------------------");
				}
				player.sendMessage(Skyotickets.messages.message15.replace("/n/", String.valueOf(newTickets.size())));
				player.sendMessage(Skyotickets.messages.message16);
			}
			catch(Exception ex) {
				player.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
				ex.printStackTrace();
			}
		}
	}

}