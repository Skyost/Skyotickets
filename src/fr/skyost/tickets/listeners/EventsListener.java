package fr.skyost.tickets.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.Skyotickets;

public class EventsListener implements Listener {
	
	@EventHandler
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		try {
			final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
			if(tickets == null) {
				player.sendMessage(Skyotickets.messages.Messages_13);
				return;
			}
			final String playerName = player.getName();
			String owner;
			final ArrayList<String> newTickets = new ArrayList<String>();
			for(Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
				for(Ticket ticket : entry.getValue()) {
					owner = ticket.getOwner();
					if(owner.equals(playerName) || (ticket.getStatus() == TicketStatus.OPEN && owner.equals("nobody"))) {
						newTickets.add(ticket.getFormattedString());
					}
				}
			}
			for(String ticket : newTickets) {
				player.sendMessage(ticket);
				player.sendMessage(ChatColor.GOLD + "-------------------------------");
			}
			player.sendMessage(Skyotickets.messages.Messages_15.replaceAll("/n/", String.valueOf(newTickets.size())));
			player.sendMessage(Skyotickets.messages.Messages_16);
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

}