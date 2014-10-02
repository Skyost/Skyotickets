package fr.skyost.tickets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Charsets;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.Ticket.TicketPriority;
import fr.skyost.tickets.Ticket.TicketStatus;

public class Utils {
	
	private static final File logFile = new File(Skyotickets.config.logFile);
	
	public static final void delete(final File path) {
		if(path.isDirectory()) {
			final String[] files = path.list();
			if(files.length == 0) {
				path.delete();
			}
			else {
				for(final String tmp : files) {
					File del = new File(path, tmp);
					delete(del);
				}
				if(path.list().length == 0) {
				  	path.delete();
				}
		  	}
		}
		else {
			path.delete();
		}
	}
	
	public static final boolean isTicketPriority(final String string) {
		try {
			TicketPriority.valueOf(string);
			return true;
		}
		catch(final IllegalArgumentException ex) {}
		return false;
	}
	
	public static final boolean isTicketStatus(final String string) {
		try {
			TicketStatus.valueOf(string);
			return true;
		}
		catch(final IllegalArgumentException ex) {}
		return false;
	}
	
	public static final String date() {
		return new SimpleDateFormat(Skyotickets.config.dateFormat).format(new Date());
	}
	
	public static final void log(final String... messages) {
		try {
			if(!logFile.exists()) {
				logFile.createNewFile();
			}
			final PrintWriter writer = new PrintWriter(new FileWriter(logFile, true), true);
			for(final String message : messages) {
				writer.println(message);
			}
			writer.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final String getFileContent(final File file, final String lineSeparator) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		final StringBuilder builder = new StringBuilder();
		try {
			String line = reader.readLine();
			while(line != null) {
				builder.append(line);
				if(lineSeparator != null) {
					builder.append(lineSeparator);
				}
				line = reader.readLine();
			}
		}
		finally {
			reader.close();
		}
		return builder.toString();
	}
	
	public static final void writeToFile(final File file, final String content) throws IOException {
		final FileWriter fileWriter = new FileWriter(file, false);
		final PrintWriter printWriter = new PrintWriter(fileWriter, true);
		printWriter.println(content);
		printWriter.close();
		fileWriter.close();
	}
	
	public static final OfflinePlayer getPlayerByArgument(final String arg) {
		final UUID uuid = uuidTryParse(arg);
		final OfflinePlayer player = uuid == null ? Bukkit.getOfflinePlayer(arg) : Bukkit.getOfflinePlayer(uuid);
		if(player == null && uuid == null) {
			return Bukkit.getOfflinePlayer(UUID.nameUUIDFromBytes(("OfflinePlayer:" + arg).getBytes(Charsets.UTF_8)));
		}
		return player;
	}
	
	public static final UUID uuidTryParse(final String string) {
		try {
			return UUID.fromString(string);
		}
		catch(final IllegalArgumentException ex){}
		return null;
	}
	
	public static final String locationSerialize(final Location location) {
		final JSONObject json = new JSONObject();
		json.put("world", location.getWorld().getName());
		json.put("x", location.getX());
		json.put("y", location.getY());
		json.put("z", location.getZ());
		return json.toJSONString();
	}
	
	public static final Location locationDeserialize(final String serializedLocation) {
		final JSONObject json = (JSONObject)JSONValue.parse(serializedLocation);
		return new Location(Bukkit.getWorld(json.get("world").toString()), Double.parseDouble(json.get("x").toString()), Double.parseDouble(json.get("y").toString()), Double.parseDouble(json.get("z").toString()));
	}

}