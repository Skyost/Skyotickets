package fr.skyost.tickets.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.Ticket.TicketPriority;
import fr.skyost.tickets.Ticket.TicketStatus;

/**
 * A super-ultra useful class.
 * 
 * @author Skyost
 */

public class Utils {
	
	private static final File logFile = new File(Skyotickets.config.Log_File);
	
	/**
	 * Delete a file or a folder.
	 * 
	 * @param path The path of the file.
	 */
	
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
	
	/**
	 * Check if the string is a ticket priority.
	 * 
	 * @param string The string.
	 * 
	 * @return <b>true</b> If the string is a ticket priority.
	 * <br><b>false</b> If the string is not a ticket priority.
	 */
	
	public static final boolean isTicketPriority(final String string) {
		for(final TicketPriority priority : TicketPriority.values()) {
			if(priority.name().equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the string is a ticket status.
	 * 
	 * @param string The string.
	 * 
	 * @return <b>true</b> If the string is a ticket status.
	 * <br><b>false</b> If the string is not a ticket status.
	 */
	
	public static final boolean isTicketStatus(final String string) {
		for(final TicketStatus status : TicketStatus.values()) {
			if(status.name().equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the current date.
	 * 
	 * @return The current date.
	 */
	
	public static final String date() {
		return new SimpleDateFormat(Skyotickets.config.DateFormat).format(new Date());
	}
	
	/**
	 * Log some messages to the default file.
	 * 
	 * @param messages Every messages you want to log.
	 */
	
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
	
	/**
	 * Colourize a String.
	 * 
	 * @param string The String you want to colourize.
	 */
	
	public static final String colourize(final String string) {
		return (" " + string).replaceAll("([^\\\\](\\\\\\\\)*)&(.)", "$1§$3").replaceAll("([^\\\\](\\\\\\\\)*)&(.)", "$1§$3").replaceAll("(([^\\\\])\\\\((\\\\\\\\)*))&(.)", "$2$3&$5").replaceAll("\\\\\\\\", "\\\\").trim();
	}

}