package fr.skyost.tickets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import fr.skyost.tickets.listeners.CommandsExecutor;
import fr.skyost.tickets.listeners.EventsListener;
import fr.skyost.tickets.threads.RemoteControl;
import fr.skyost.tickets.threads.SocketListener;
import fr.skyost.tickets.utils.MetricsLite;
import fr.skyost.tickets.utils.Skyupdater;

public class Skyotickets extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	public static File ticketsFolder;
	public static boolean useRemoteDatabase = false;
	
	private SocketListener socketListener;
	
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
			command.setUsage(ChatColor.RED + "/ticket [priority] [text].");
			command.setExecutor(executor);
			this.getCommand("mytickets").setExecutor(executor);
			command = this.getCommand("mtickets");
			command.setUsage(ChatColor.RED + "/mtickets view <player> <id>, delete <player> <id>, claim [player] [id], status [player] [id] or teleport (or tp) [player] [id].");
			command.setExecutor(executor);
			Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
			if(config.EnableUpdater) {
				new Skyupdater(this, 71984, this.getFile(), true, true);
			}
			if(config.Socket_Use) {
				if(config.Socket_Password.contains(" ")) {
					config.Socket_Password.replaceAll(" ", "");
					config.save();
				}
				socketListener = new SocketListener(config.Socket_Host, config.Socket_Port, config.Socket_Password, this.getDataFolder());
				socketListener.start();
				if(config.Remote_Use) {
					new RemoteControl(Bukkit.getConsoleSender(), "auth " + config.Remote_Password, "skyotickets register " + config.Socket_Host + " " + config.Socket_Port).start();
				}
			}
			new MetricsLite(this).start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public final void onDisable() {
		if(socketListener != null) {
			try {
				socketListener.disable();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static final Ticket getTicket(final String player, final String id) throws IOException, ClassNotFoundException {
		final File playerDir = getPlayerDir(player);
		if(!playerDir.exists()) {
			return null;
		}
		final File ticketFile = new File(playerDir, id);
		if(!ticketFile.exists()) {
			return null;
		}
		final Ticket ticket = new Ticket(ticketFile);
		return ticket;
	}
	
	public static final ArrayList<Ticket> getPlayerTickets(final String player) throws IOException, ClassNotFoundException {
		final File playerDir = getPlayerDir(player);
		if(!playerDir.exists()) {
			return null;
		}
		final File[] ticketsFiles = playerDir.listFiles();
		if(ticketsFiles.length == 0) {
			return null;
		}
		final ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		Ticket ticket;
		for(final File ticketFile : ticketsFiles) {
			ticket = new Ticket(ticketFile);
			tickets.add(ticket);
		}
		return tickets;
	}
	
	public static final HashMap<String, ArrayList<Ticket>> getTickets() throws IOException, ClassNotFoundException {
		if(!ticketsFolder.exists()) {
			ticketsFolder.mkdir();
		}
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
		if(!ticketsFolder.exists()) {
			ticketsFolder.mkdir();
		}
		return new File(ticketsFolder + File.separator + player);
	}

}