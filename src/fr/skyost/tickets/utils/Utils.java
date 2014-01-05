package fr.skyost.tickets.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.Ticket.TicketStatus;

public class Utils {
	
	public static final void delete(File path) {
		if(path.isDirectory()) {
			final String[] files = path.list();
			if(files.length == 0) {
				path.delete();
			}
			else {
				for(String tmp : files) {
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
	
	public static final boolean isTicketStatus(String string) {
		for(TicketStatus status : TicketStatus.values()) {
			if(status.name().equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static String date() {
		return new SimpleDateFormat(Skyotickets.config.DateFormat).format(new Date());
	}

}
