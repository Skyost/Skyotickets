package fr.skyost.tickets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.skyost.tickets.utils.Utils;

public class SkyoticketsAPI {
	
	/**
	 * Gets the tickets' directory.
	 * 
	 * @return The tickets' directory.
	 */
	
	public static final File getTicketsDirectory() {
		final File ticketDirectory = new File(Skyotickets.config.ticketsDir);
		if(!ticketDirectory.exists()) {
			ticketDirectory.mkdir();
		}
		return ticketDirectory;
	}
	
	/**
	 * Gets the player's directory.
	 * 
	 * @param uuid The player's uuid.
	 * 
	 * @return The player's directory.
	 */
	
	public static final File getPlayerDir(final UUID uuid) {
		return new File(getTicketsDirectory() + File.separator + uuid.toString());
	}
	
	/**
	 * Gets a ticket by its ID.
	 * 
	 * @param uuid The player who sends this tickets.
	 * @param id The ticket's ID.
	 * 
	 * @return The ticket.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public static final Ticket getTicket(final UUID uuid, final int id) throws IOException {
		return getTicket(uuid, String.valueOf(id));
	}
	
	/**
	 * Gets a ticket by its ID.
	 * 
	 * @param uuid The player who sends this tickets.
	 * @param id The ticket's ID.
	 * 
	 * @return The ticket.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public static final Ticket getTicket(final UUID uuid, final String id) throws IOException {
		final File playerDir = getPlayerDir(uuid);
		if(!playerDir.exists()) {
			return null;
		}
		final File ticketFile = new File(playerDir, id);
		if(!ticketFile.exists()) {
			return null;
		}
		return Ticket.readFromFile(ticketFile);
	}
	
	/**
	 * Gets the tickets of a player.
	 * 
	 * @param uuid The player's uuid.
	 * 
	 * @return His ticket.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public static final Ticket[] getPlayerTickets(final UUID uuid) throws IOException {
		final File playerDir = getPlayerDir(uuid);
		if(!playerDir.exists()) {
			return null;
		}
		final File[] ticketsFiles = playerDir.listFiles();
		if(ticketsFiles.length == 0) {
			return null;
		}
		final List<Ticket> tickets = new ArrayList<Ticket>();
		for(final File ticketFile : ticketsFiles) {
			tickets.add(Ticket.readFromFile(ticketFile));
		}
		return tickets.toArray(new Ticket[tickets.size()]);
	}
	
	/**
	 * Gets all tickets.
	 * 
	 * @return A map containing all tickets :
	 * <br><b>Key :</b> The player's uuid.
	 * 
	 * @throws IOException
	 */
	
	public static final HashMap<UUID, Ticket[]> getTickets() throws IOException {
		final File[] playersDir = getTicketsDirectory().listFiles();
		if(playersDir.length == 0) {
			return null;
		}
		final HashMap<UUID, Ticket[]> tickets = new HashMap<UUID, Ticket[]>();
		for(final File playerDir : playersDir) {
			final UUID uuid = Utils.uuidTryParse(playerDir.getName());
			if(uuid == null) {
				continue;
			}
			final Ticket[] playersTickets = getPlayerTickets(uuid);
			if(playersTickets != null) {
				tickets.put(uuid, playersTickets);
			}
		}
		return tickets;
	}
	
}
