package fr.skyost.tickets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Bukkit;

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
	private String owner;
	
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
	 * @param broadcast Broadcast the current ticket on the server.
	 * @param saveToFile Save the current ticket into a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final String player, final String message, final String[] location, final boolean broadcast, final boolean saveToFile) throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		this.id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		this.priority = priority;
		this.status = TicketStatus.OPEN;
		this.date = Utils.date();
		this.player = player;
		this.message = message;
		this.location = location;
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
	
	public Ticket(final String id, final TicketPriority priority, final TicketStatus status, final String date, final String player, final String message, final String[] location, final String owner, final boolean broadcast, final boolean saveToFile) throws IOException {
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.date = date;
		this.player = player;
		this.message = message;
		this.location = location;
		this.owner = owner;
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
		final String owner = ticket.getOwner();
		this.owner = owner.equals(Skyotickets.config.NoOwner) ? null : owner;
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
	 * Get the owner of this ticket.
	 * 
	 * @return The owner.
	 */
	
	public final String getOwner() {
		return owner == null ? Skyotickets.config.NoOwner : owner;
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
	 * Set the owner of this ticket.
	 * 
	 * @param owner The owner.
	 * 
	 * @return <b>true</b> If the owner has been set with success.
	 * <br><b>false</b> If the ticket has already an owner.
	 */
	
	public final boolean setOwner(final String owner) {
		if(this.owner == null) {
			this.owner = owner;
			return true;
		}
		return false;
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
		return Utils.colourize(Skyotickets.config.FormattedString.replaceAll("/id/", id).replaceAll("/priority/", priority.name()).replaceAll("/status/", status.name()).replaceAll("/date/", date).replaceAll("/player/", player).replaceAll("/message/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/owner/", owner == null ? Skyotickets.config.NoOwner : owner).replaceAll("/n/", lineSeparator));
	}
	
	/**
	 * Broadcast a message which says that the ticket has been created.
	 */
	
	public final void broadcast() {
		Bukkit.broadcast(Skyotickets.messages.Messages_1.replaceAll("/player/", player).replaceAll("/ticket/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/priority/", priority.name()).replaceAll("/n/", "\n"), "ticket.view.ticket");
	}
	
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
		builder.append("OWNER : " + owner == null ? Skyotickets.config.NoOwner : owner);
		return builder.toString();
	}
	
}