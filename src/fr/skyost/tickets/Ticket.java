package fr.skyost.tickets;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Joiner;

import fr.skyost.tickets.utils.Utils;

/**
 * Represents a ticket from Skyotickets.
 * 
 * @author Skyost
 */

public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private TicketPriority priority;
	private TicketStatus status;
	private String date;
	private UUID player;
	private String message;
	private Location location;
	private List<UUID> owners;
	
	/**
	 * Creates a new ticket instance.
	 * 
	 * @param priority The ticket's priority.
	 * @param player The player who mades the ticket request.
	 * @param message The ticket's message.
	 * @param location The player's location.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final UUID player, final String message, final Location location) throws IOException {
		this(priority, player, message, location, true, false, true);
	}
	
	/**
	 * Create a new ticket instance.
	 * 
	 * @param priority The ticket's priority.
	 * @param player The player who mades the ticket request.
	 * @param message The ticket's message.
	 * @param location The location of the player.
	 * @param withSound If you want to broadcast the message with a "pop" sound.
	 * @param broadcast Broadcast the current ticket on the server.
	 * @param saveToFile Save the current ticket into a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public Ticket(final TicketPriority priority, final UUID player, final String message, final Location location, final boolean broadcast, final boolean withSound, final boolean saveToFile) throws IOException {
		this(SkyoticketsAPI.getPlayerDir(player).exists() ? SkyoticketsAPI.getPlayerDir(player).listFiles().length + 1 : 1, priority, TicketStatus.OPEN, Utils.date(), player, message, location, broadcast, withSound, saveToFile, null);
	}
	
	private Ticket(final int id, final TicketPriority priority, final TicketStatus status, final String date, final UUID player, final String message, final Location location, final boolean broadcast, final boolean withSound, final boolean saveToFile, final List<UUID> owners) throws IOException {
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.date = date;
		this.player = player;
		this.message = message;
		this.location = location;
		if(broadcast) {
			broadcast(withSound);
		}
		if(saveToFile) {
			saveToFile();
		}
		if(owners != null && owners.size() != 0 && (owners.size() != 1 && !owners.get(0).equals(Skyotickets.config.nobody))) {
			this.owners = owners;
		}
	}
	
	/**
	 * Gets the id of this ticket.
	 * 
	 * @return The id.
	 */
	
	public final int getId() {
		return id;
	}
	
	/**
	 * Gets the priority of this ticket.
	 * 
	 * @return The priority.
	 */
	
	public final TicketPriority getPriority() {
		return priority;
	}
	
	/**
	 * Gets the status of this ticket.
	 * 
	 * @return The status.
	 */
	
	public final TicketStatus getStatus() {
		return status;
	}
	
	/**
	 * Gets the date of this ticket.
	 * 
	 * @return The date.
	 */
	
	public final String getDate() {
		return date;
	}
	
	/**
	 * Gets the player who mades this ticket.
	 * 
	 * @return The player.
	 */
	
	public final UUID getPlayer() {
		return player;
	}
	
	/**
	 * Gets the message of this ticket.
	 * 
	 * @return The message.
	 */
	
	public final String getMessage() {
		return message;
	}
	
	/**
	 * Gets the location of this ticket.
	 * 
	 * @return The location. The <b>row zero</b> is the <b>world</b>, the <b>row one</b> is the <b>X</b>, the <b>row two</b> is the <b>Y</b>, the <b>row three</b> is the <b>Z</b>.
	 */
	
	public final Location getLocation() {
		return location;
	}
	
	/**
	 * Gets the owners of this ticket.
	 * 
	 * @return The owners.
	 * <br><b>null</b> If there is no any owner !
	 */
	
	public final UUID[] getOwners() {
		if(owners == null) {
			return null;
		}
		return owners.toArray(new UUID[owners.size()]);
	}
	
	/**
	 * Sets the id of this ticket.
	 * 
	 * @param id The id.
	 */
	
	public final void setId(final int id) {
		this.id = id;
	}
	
	/**
	 * Sets the priority of this ticket.
	 * 
	 * @param priority The priority.
	 */
	
	public final void setPriority(final TicketPriority priority) {
		this.priority = priority;
	}
	
	/**
	 * Sets the status of this ticket.
	 * 
	 * @param status The status.
	 */
	
	public final void setStatus(final TicketStatus status) {
		this.status = status;
	}
	
	/**
	 * Sets the date of this ticket.
	 * 
	 * @param date The date.
	 */
	
	public final void setDate(final String date) {
		this.date = date;
	}
	
	/**
	 * Sets the player of this ticket.
	 * 
	 * @param uuid The player's uuid.
	 */
	
	public final void setPlayer(final UUID uuid) {
		this.player = uuid;
	}
	
	/**
	 * Sets the message of this ticket.
	 * 
	 * @param message The message.
	 */
	
	public final void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * Sets the location of this ticket.
	 * 
	 * @param location The location.
	 */
	
	public final void setLocation(final Location location) {
		this.location = location;
	}
	
	/**
	 * Adds an owner to this ticket.
	 * 
	 * @param owner The owner's uuid.
	 * 
	 * @return <b>true</b> If it a success.
	 * <br><b>false</b> Otherwise.
	 */
	
	public final boolean addOwner(final UUID owner) {
		if(owners == null) {
			owners = new ArrayList<UUID>();
		}
		if(owners.contains(owner)) {
			return false;
		}
		owners.add(owner);
		return true;
	}
	
	/**
	 * Sets the owners of this ticket.
	 * 
	 * @param owners The owners.
	 */
	
	public final void setOwners(final UUID[] owners) {
		this.owners = Arrays.asList(owners);
	}
	
	/**
	 * Gets ticket's file.
	 * 
	 * @return The ticket's file.
	 */
	
	public final File getFile() {
		return new File(SkyoticketsAPI.getPlayerDir(player), String.valueOf(id));
	}
	
	/**
	 * Gets the formatted string of this ticket.
	 * 
	 * @return The formatted string of this ticket.
	 */
	
	public final String getFormattedString() {
		return getFormattedString("\n");
	}
	
	/**
	 * Gets the formatted string of this ticket.
	 * 
	 * @param lineSeparator The separator between each line.
	 * 
	 * @return The ticket's formatted string.
	 */

	public final String getFormattedString(final String lineSeparator) {
		final OfflinePlayer player = Bukkit.getOfflinePlayer(this.player);
		return Skyotickets.config.formattedString.replace("/id/", String.valueOf(id)).replace("/priority/", priority.name()).replace("/status/", status.name()).replace("/date/", date).replace("/player/", player == null ? Skyotickets.config.nobody : player.getName()).replace("/message/", message).replace("/world/", location.getWorld().getName()).replace("/x/", String.valueOf(location.getBlockX())).replace("/y/", String.valueOf(location.getBlockY())).replace("/z/", String.valueOf(location.getBlockZ())).replace("/owners/", owners == null ? Skyotickets.config.nobody : Joiner.on(", ").join(owners)).replace("/n/", lineSeparator);
	}
	
	/**
	 * Broadcasts a message which says that the ticket has been created.
	 */
	
	public final void broadcast() {
		broadcast(false);
	}
	
	/**
	 * Broadcasts a message which says that the ticket has been created.
	 * 
	 * @param withSound If you want to play a "pop" sound.
	 */
	
	public final void broadcast(final boolean withSound) {
		final OfflinePlayer sender = Bukkit.getOfflinePlayer(this.player);
		final String notification = Skyotickets.messages.message1.replace("/player/", sender == null ? Skyotickets.config.nobody : sender.getName()).replace("/ticket/", message).replace("/world/", location.getWorld().getName()).replace("/x/", String.valueOf(location.getBlockX())).replace("/y/", String.valueOf(location.getBlockY())).replace("/z/", String.valueOf(location.getBlockZ())).replace("/priority/", priority.name()).replace("/n/", "\n");
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
	 * Saves the ticket to a file.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public final void saveToFile() throws IOException {
		final File playerDir = SkyoticketsAPI.getPlayerDir(player);
		if(!playerDir.exists()) {
			playerDir.mkdir();
		}
		Utils.writeToFile(getFile(), toString());
	}
	
	/**
	 * Reads the ticket from a file.
	 * 
	 * @param file The ticket's file.
	 * 
	 * @return The ticket.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	public static final Ticket readFromFile(final File file) throws IOException {
		final JSONObject json = (JSONObject)JSONValue.parse(Utils.getFileContent(file, null));
		final List<UUID> owners = new ArrayList<UUID>();
		final JSONArray serializedOwners = (JSONArray)json.get("owners");
		for(int i = 0; i != serializedOwners.size(); i++) {
			owners.add(UUID.fromString(serializedOwners.get(i).toString()));
		}
		return new Ticket(Integer.parseInt(json.get("id").toString()), TicketPriority.valueOf(json.get("priority").toString()), TicketStatus.valueOf(json.get("status").toString()), json.get("date").toString(), UUID.fromString(json.get("player").toString()), json.get("message").toString(), Utils.locationDeserialize(json.get("location").toString()), false, false, false, owners);
	}
	
	@Override
	public final String toString() {
		final JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("priority", priority.name());
		json.put("status", status.name());
		json.put("date", date);
		json.put("player", player.toString());
		json.put("message", message);
		json.put("location", Utils.locationSerialize(location));
		final JSONArray owners = new JSONArray();
		if(this.owners != null) {
			for(final UUID owner : this.owners) {
				owners.add(owner.toString());
			}
		}
		json.put("owners", owners);
		return json.toJSONString();
	}
	
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
		 * Gets the priority letter.
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
	
}