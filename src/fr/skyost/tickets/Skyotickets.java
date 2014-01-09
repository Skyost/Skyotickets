package fr.skyost.tickets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.listeners.CommandsExecutor;
import fr.skyost.tickets.listeners.EventsListener;
import fr.skyost.tickets.utils.MetricsLite;
import fr.skyost.tickets.utils.Skyupdater;

public class Skyotickets extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	public static File ticketsFolder;
	
	@Override
	public final void onEnable() {
		try {
			config = new ConfigFile(this.getDataFolder());
			config.init();
			messages = new MessagesFile(this);
			messages.init();
			ticketsFolder = new File(config.TicketsFolder);
			if(!ticketsFolder.exists()) {
				ticketsFolder.mkdir();
			}
			final CommandExecutor executor = new CommandsExecutor();
			PluginCommand command = this.getCommand("ticket");
			command.setUsage(ChatColor.RED + "/ticket <text>.");
			command.setExecutor(executor);
			this.getCommand("mytickets").setExecutor(executor);
			command = this.getCommand("mtickets");
			command.setUsage(ChatColor.RED + "/mtickets view <player> <id>, delete <player> <id>, claim [player] [id], status [player] [id] or teleport (or tp) [player] [id].");
			command.setExecutor(executor);
			Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
			if(config.EnableUpdater) {
				new Skyupdater(this, 71984, this.getFile(), true, true);
			}
			new MetricsLite(this).start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final Ticket getTicket(final String player, final String id) throws IOException {
		final File playerDir = getPlayerDir(player);
		if(!playerDir.exists()) {
			return null;
		}
		final File ticketFile = new File(playerDir, id);
		if(!ticketFile.exists()) {
			return null;
		}
		final BufferedReader reader = new BufferedReader(new FileReader(ticketFile));
		final StringBuilder builder = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}
		final String[] ticketData = builder.toString().split("#");
		final Ticket ticket = new Ticket(ticketData[3], new String[]{ticketData[5], ticketData[6], ticketData[7], ticketData[8]}, ticketData[4], TicketStatus.valueOf(ticketData[1]), ticketData[2], ticketData[0], false);
		if(!ticketData[9].equals("nobody")) {
			ticket.setOwner(ticketData[9]);
		}
		reader.close();
		return ticket;
	}
	
	public static final ArrayList<Ticket> getPlayerTickets(final String player) throws IOException {
		final File playerDir = getPlayerDir(player);
		if(!playerDir.exists()) {
			return null;
		}
		final File[] ticketsFiles = playerDir.listFiles();
		if(ticketsFiles.length == 0) {
			return null;
		}
		BufferedReader reader = null;
		final ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		String[] ticketData;
		Ticket ticket;
		for(final File ticketFile : ticketsFiles) {
			reader = new BufferedReader(new FileReader(ticketFile));
			final StringBuilder builder = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			ticketData = builder.toString().split("#");
			ticket = new Ticket(ticketData[3], new String[]{ticketData[5], ticketData[6], ticketData[7], ticketData[8]}, ticketData[4], TicketStatus.valueOf(ticketData[1]), ticketData[2], ticketData[0], false);
			if(!ticketData[9].equals("nobody")) {
				ticket.setOwner(ticketData[9]);
			}
			tickets.add(ticket);
		}
		reader.close();
		return tickets;
	}
	
	public static final HashMap<String, ArrayList<Ticket>> getTickets() throws IOException {
		final File[] playersDir = ticketsFolder.listFiles();
		if(playersDir.length == 0) {
			return null;
		}
		final HashMap<String, ArrayList<Ticket>> tickets = new HashMap<String, ArrayList<Ticket>>();
		ArrayList<Ticket> playersTickets;
		for(final File playerDir : playersDir) {
			playersTickets = getPlayerTickets(playerDir.getName());
			if(playersTickets != null) {
				tickets.put(playerDir.getName(), playersTickets);
			}
		}
		return tickets;
	}
	
	public static final File getPlayerDir(final String player) {
		final File playerDir = new File(Skyotickets.ticketsFolder + System.getProperty("file.separator", "\\") + player);
		return playerDir;
	}

}