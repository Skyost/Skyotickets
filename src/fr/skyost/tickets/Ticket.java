package fr.skyost.tickets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.bukkit.Bukkit;

import fr.skyost.tickets.utils.Utils;

public class Ticket {

	private String id;
	private String player;
	private String owner;
	private String[] location;
	private String message;
	private TicketStatus status;
	private String date;
	
	public enum TicketStatus {
		OPEN,
		TAKEN,
		CLOSED;
	}
	
	public Ticket(final String player, final String[] location, final String message) throws IOException {
		this.player = player;
		this.location = location;
		this.message = message;
		this.status = TicketStatus.OPEN;
		date = Utils.date();
		final File playerDir = Skyotickets.getPlayerDir(player);
		id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		broadcast();
		saveToFile();
	}
	
	public Ticket(final String player, final String[] location, final String message, final boolean broadcast) throws IOException {
		this.player = player;
		this.location = location;
		this.message = message;
		date = Utils.date();
		final File playerDir = Skyotickets.getPlayerDir(player);
		id = playerDir.exists() ? String.valueOf(playerDir.listFiles().length + 1) : "1";
		if(broadcast) {
			broadcast();
		}
	}
	
	public Ticket(final String player, final String[] location, final String message, final TicketStatus status, final String date, final String id, final boolean broadcast) throws IOException {
		this.player = player;
		this.location = location;
		this.message = message;
		this.status = status;
		this.date = date;
		this.id = id;
		if(broadcast) {
			broadcast();
		}
	}
	
	public final String getId() {
		return id;
	}
	
	public final String getPlayer() {
		return player;
	}
	
	public final String getOwner() {
		return owner == null ? "nobody" : owner;
	}
	
	public final String[] getLocation() {
		return location;
	}
	
	public final String getMessage() {
		return message;
	}
	
	public final TicketStatus getStatus() {
		return status;
	}
	
	public final String getDate() {
		return date;
	}
	
	public final void setId(final String id) {
		this.id = id;
	}
	
	public final void setPlayer(final String player) {
		this.player = player;
	}
	
	public final boolean setOwner(final String owner) {
		if(this.owner == null) {
			this.owner = owner;
			return true;
		}
		return false;
	}
	
	public final void setLocation(final String[] location) {
		this.location = location;
	}
	
	public final void setMessage(final String message) {
		this.message = message;
	}

	public final void setStatus(final TicketStatus status) {
		this.status = status;
	}
	
	public final void setDate(final String date) {
		this.date = date;
	}
	
	public final File getFile() {
		return new File(Skyotickets.getPlayerDir(player), id);
	}
	
	public final String getFormattedString() {
		return Skyotickets.config.FormattedString.replaceAll("/status/", status.name()).replaceAll("/id/", id).replaceAll("/date/", date).replaceAll("/player/", player).replaceAll("/message/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/owner/", owner == null ? "Nobody" : owner).replaceAll("/n/", "\n");
	}

	
	public final void broadcast() {
		Bukkit.broadcast(Skyotickets.messages.Messages_1.replaceAll("/player/", player).replaceAll("/ticket/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/n/", "\n"), "ticket.view.ticket");
	}
	
	public final void saveToFile() throws IOException {
		final File playerDir = Skyotickets.getPlayerDir(player);
		if(!playerDir.exists()) {
			playerDir.mkdir();
		}
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(playerDir, id)), "UTF-8"));
		writer.print(toString());
		writer.flush();
		writer.close();
	}
	
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(id);
		builder.append("#");
		builder.append(status.name());
		builder.append("#");
		builder.append(date);
		builder.append("#");
		builder.append(player);
		builder.append("#");
		builder.append(message);
		builder.append("#");
		builder.append(location[0]);
		builder.append("#");
		builder.append(location[1]);
		builder.append("#");
		builder.append(location[2]);
		builder.append("#");
		builder.append(location[3]);
		builder.append("#");
		builder.append(owner == null ? "nobody" : owner);
		return builder.toString();
	}
	
}