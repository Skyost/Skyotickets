package fr.skyost.tickets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.utils.Utils;

/**
 * Represents a ticket from Skyotickets.
 * 
 * @author Skyost
 */

public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private TicketPriority priority;
	private TicketStatus status;
	private String date;
	private String player;
	private String message;
	private String[] location;
	private List<String> owners;
	
	/**
	 * The available tickets priorities.
	 */
	
	public enum TicketPriority {
		
		/**
		 * Must be resolved as quickly as possible.
		 */
		
		CRITICAL('A'),
		
		/**
		 * Must be resolved rapidly.
		 */
		
		HIGH('B'),
		
		/**
		 * Must be resolved but it is not urgent.
		 */
		
		MEDIUM('C'),
		
		/**
		 * Must be resolved but you can take your time.
		 */
		
		LOW('D');
		
		private final char letter;
		
		TicketPriority(final char letter) {
			this.letter = letter;
		}
		
		/**
		 * Get the priority letter.
		 */
		
		public char getLetter() {
			return letter;
		}
	}
	
	/**
	 * The available tickets status.
	 */
	
	public enum TicketStatus {
		
		/**
		 * The ticket is open.
		 */
		
		OPEN,
		
		/**
		 * The ticket is claimed by a player.
		 */
		
		TAKEN,
		
		/**
		 * The ticket is closed.
		 */
		
		CLOSED;
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param priority The ticket's priority.
	 * @param player The player which has made the ticket request.
	 * @param message The ticket's message.
	 * @param location The location of the player.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final String player, final String message, final String[] location) throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		this.id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		this.priority = priority;
		this.status = TicketStatus.OPEN;
		this.date = Utils.date();
		this.player = player;
		this.message = message;
		this.location = location;
		broadcast();
		saveToFile();
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param priority The ticket's priority.
	 * @param player The player which has made the ticket request.
	 * @param message The ticket's message.
	 * @param location The location of the player.
	 * @param withSound If you want to broadcast the message with a "pop" sound.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final String player, final String message, final String[] location, final boolean withSound) throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		this.id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		this.priority = priority;
		this.status = TicketStatus.OPEN;
		this.date = Utils.date();
		this.player = player;
		this.message = message;
		this.location = location;
		broadcast(withSound);
		saveToFile();
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param priority The ticket's priority.
	 * @param player The player which has made the ticket request.
	 * @param message The ticket's message.
	 * @param location The location of the player.
	 * @param broadcast Broadcast the current ticket on the server.
	 * @param withSound If you want to broadcast the message with a "pop" sound.
	 * @param saveToFile Save the current ticket into a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final String player, final String message, final String[] location, final boolean broadcast, final boolean withSound, final boolean saveToFile) throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		this.id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		this.priority = priority;
		this.status = TicketStatus.OPEN;
		this.date = Utils.date();
		this.player = player;
		this.message = message;
		this.location = location;
		if(broadcast) {
			broadcast(withSound);
		}
		if(saveToFile) {
			saveToFile();
		}
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param id The ticket's id.
	 * @param priority The priority of this ticket.
	 * @param status The status of this ticket.
	 * @param data The ticket's date.
	 * @param player The player which has made the ticket request.
	 * @param message The ticket's message.
	 * @param location The location of the player.
	 * @param owner The ticket's owner.
	 * @param broadcast Broadcast the current ticket on the server.
	 * @param saveToFile Save the current ticket into a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final String id, final TicketPriority priority, final TicketStatus status, final String date, final String player, final String message, final String[] location, final List<String> owners, final boolean broadcast, final boolean saveToFile) throws IOException {
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.date = date;
		this.player = player;
		this.message = message;
		this.location = location;
		this.owners = owners;
		if(broadcast) {
			broadcast();
		}
		if(saveToFile) {
			saveToFile();
		}
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param file The ticket's file.
	 * @throws IOException InputOutputException.
	 * @throws ClassNotFoundException If this is not a ticket.
	 */
	
	public Ticket(final File file) throws IOException, ClassNotFoundException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
	    final Ticket ticket = (Ticket)objectInputStream.readObject();
	    this.id = ticket.getId();
		this.priority = ticket.getPriority();
		this.status = ticket.getStatus();
		this.date = ticket.getDate();
		this.player = ticket.getPlayer();
		this.message = ticket.getMessage();
		this.location = ticket.getLocation();
		final List<String> owners = ticket.getOwners();
		if(!(owners.size() == 1 && owners.get(0).equals(Skyotickets.config.NoOwner))) {
			this.owners = ticket.getOwners();
		}
	    objectInputStream.close();
	}
	
	/**
	 * Get the id of this ticket.
	 * 
	 * @return The id.
	 */
	
	public final String getId() {
		return id;
	}
	
	/**
	 * Get the priority of this ticket.
	 * 
	 * @return The priority.
	 */
	
	public final TicketPriority getPriority() {
		return priority;
	}
	
	/**
	 * Get the status of this ticket.
	 * 
	 * @return The status.
	 */
	
	public final TicketStatus getStatus() {
		return status;
	}
	
	/**
	 * Get the date of this ticket.
	 * 
	 * @return The date.
	 */
	
	public final String getDate() {
		return date;
	}
	
	/**
	 * Get the player of this ticket.
	 * 
	 * @return The player.
	 */
	
	public final String getPlayer() {
		return player;
	}
	
	/**
	 * Get the message of this ticket.
	 * 
	 * @return The message.
	 */
	
	public final String getMessage() {
		return message;
	}
	
	/**
	 * Get the location of this ticket.
	 * 
	 * @return The location. The <b>row zero</b> is the <b>world</b>, the <b>row one</b> is the <b>X</b>, the <b>row two</b> is the <b>Y</b>, the <b>row three</b> is the <b>Z</b>.
	 */
	
	public final String[] getLocation() {
		return location;
	}
	
	/**
	 * Get the owners of this ticket.
	 * 
	 * @return The owners.
	 */
	
	public final List<String> getOwners() {
		return owners == null ? Arrays.asList(Skyotickets.config.NoOwner) : owners;
	}
	
	/**
	 * Set the id of this ticket.
	 * 
	 * @param id The id.
	 */
	
	public final void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Set the priority of this ticket.
	 * 
	 * @param priority The priority.
	 */
	
	public final void setPriority(final TicketPriority priority) {
		this.priority = priority;
	}
	
	/**
	 * Set the status of this ticket.
	 * 
	 * @param status The status.
	 */
	
	public final void setStatus(final TicketStatus status) {
		this.status = status;
	}
	
	/**
	 * Set the date of this ticket.
	 * 
	 * @param date The date.
	 */
	
	public final void setDate(final String date) {
		this.date = date;
	}
	
	/**
	 * Set the player of this ticket.
	 * 
	 * @param player The player.
	 */
	
	public final void setPlayer(final String player) {
		this.player = player;
	}
	
	/**
	 * Set the message of this ticket.
	 * 
	 * @param message The message.
	 */
	
	public final void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * Set the location of this ticket.
	 * 
	 * @param location The location. The <b>row zero</b> must be the <b>world</b>, the <b>row one</b> must be the <b>X</b>, the <b>row two</b> must be the <b>Y</b> and the <b>row three</b> must be the <b>Z</b>.
	 */
	
	public final void setLocation(final String[] location) {
		this.location = location;
	}
	
	/**
	 * Add an owner on this ticket.
	 * 
	 * @param owner The owner.
	 */
	
	public final boolean addOwner(final String owner) {
		if(owners == null) {
			owners = new ArrayList<String>();
		}
		if(owners.contains(owner)) {
			return false;
		}
		owners.add(owner);
		return true;
	}
	
	/**
	 * Set the owners of this ticket.
	 * 
	 * @param owners The owners.
	 */
	
	public final void setOwners(final List<String> owners) {
		this.owners = owners;
	}
	
	/**
	 * Get the file of this ticket.
	 * 
	 * @return The file of this ticket.
	 */
	
	public final File getFile() {
		return new File(Skyotickets.getPlayerDir(player), id);
	}
	
	/**
	 * Get the formatted string of this ticket.
	 * 
	 * @return The formatted string of this ticket.
	 */
	
	public final String getFormattedString() {
		return getFormattedString("\n");
	}
	
	/**
	 * Get the formatted string of this ticket.
	 * 
	 * @param lineSeparator The separator between each lines.
	 * 
	 * @return The formatted string of this ticket.
	 */

	public final String getFormattedString(final String lineSeparator) {
		return Utils.colourize(Skyotickets.config.FormattedString.replaceAll("/id/", id).replaceAll("/priority/", priority.name()).replaceAll("/status/", status.name()).replaceAll("/date/", date).replaceAll("/player/", player).replaceAll("/message/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/owners/", owners == null ? Skyotickets.config.NoOwner : Joiner.on(", ").join(owners)).replaceAll("/n/", lineSeparator));
	}
	
	/**
	 * Broadcast a message which says that the ticket has been created.
	 */
	
	public final void broadcast() {
		broadcast(false);
	}
	
	/**
	 * Broadcast a message which says that the ticket has been created.
	 * 
	 * @param withSound If you want to play a "pop" sound.
	 */
	
	public final void broadcast(final boolean withSound) {
		final String notification = Skyotickets.messages.Messages_1.replaceAll("/player/", player).replaceAll("/ticket/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/priority/", priority.name()).replaceAll("/n/", "\n");
		for(final Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("ticket.view.ticket")) {
				player.sendMessage(notification);
				if(withSound) {
					player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1F, 0.75F);
				}
			}
		}
	}
	
	/**
	 * Save the ticket to a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public final void saveToFile() throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		if(!playerDir.exists()) {
			playerDir.mkdir();
		}
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(playerDir, id)));
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		objectOutputStream.close();
	}
	
	/**
	 * Convert the ticket to a String.
	 */
	
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ID : " + id);
		builder.append("\n");
		builder.append("PRIORITY : " + priority.name());
		builder.append("\n");
		builder.append("STATUS : " + status.name());
		builder.append("\n");
		builder.append("DATA : " + date);
		builder.append("\n");
		builder.append("PLAYER : " + player);
		builder.append("\n");
		builder.append("MESSAGE : " + message);
		builder.append("\n");
		builder.append("WORLD : " + location[0]);
		builder.append("\n");
		builder.append("X : " + location[1]);
		builder.append("\n");
		builder.append("Y : " + location[2]);
		builder.append("\n");
		builder.append("Z : " + location[3]);
		builder.append("\n");
		builder.append("OWNERS : " + Joiner.on(", ").join(owners));
		return builder.toString();
	}
	
}