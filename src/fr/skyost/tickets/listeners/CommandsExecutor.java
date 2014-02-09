package fr.skyost.tickets.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketPriority;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.threads.RemoteControl;
import fr.skyost.tickets.threads.SocketListener;
import fr.skyost.tickets.utils.SocketAddress;
import fr.skyost.tickets.utils.Utils;

public class CommandsExecutor implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		final String cmdName = cmd.getName();
		final String senderName = sender.getName();
		if(Skyotickets.config.Log_Use) {
			Utils.log(Utils.date() + " " + senderName + " has performed Skyotickets command : /" + cmdName + " " + Joiner.on(' ').join(args));
		}
		if(cmdName.equalsIgnoreCase("ticket")) {
			if(sender instanceof Player) {
				try {
					if(!sender.hasPermission("ticket.request")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					if(args.length <= 1) {
						return false;
					}
					final File playerDir = Skyotickets.getPlayerDir(senderName);
					if(playerDir.exists()) {
						if(playerDir.listFiles().length == Skyotickets.config.MaxTicketsByPlayer) {
							sender.sendMessage(Skyotickets.messages.Messages_14);
							return true;
						}
					}
					final Location location = ((Player)sender).getLocation();
					if(!Utils.isTicketPriority(args[0])) {
						sender.sendMessage(ChatColor.RED + "Priorities :");
						for(final TicketPriority priority : TicketPriority.values()) {
							sender.sendMessage(ChatColor.RED + priority.name());
						}
						return true;
					}
					final StringBuilder stringBuilder = new StringBuilder();
					for(int i = 1; i != args.length; i++) {
						stringBuilder.append(args[i] + " ");
					}
					String message = stringBuilder.toString();
					message = message.substring(0, message.length() - 1);
					final String world = location.getWorld().getName();
					final String x = String.valueOf(location.getBlockX());
					final String y = String.valueOf(location.getBlockY());
					final String z = String.valueOf(location.getBlockX());
					final String broadcast = Skyotickets.messages.Messages_1.replaceAll("/player/", senderName).replaceAll("/ticket/", message).replaceAll("/world/", world).replaceAll("/x/", x).replaceAll("/y/", y).replaceAll("/z/", z).replaceAll("/priority/", args[0].toUpperCase());
					if(Skyotickets.useRemoteDatabase) {
						new RemoteControl(sender, "skyotickets create " + senderName + " " + args[0] + " #" + message + "#" + world + "," + x + "," + y + "," + z).start();
						return true;
					}
					else if(!SocketListener.skyotickets.isEmpty()) {
						for(final SocketAddress remote : SocketListener.skyotickets) {
							new RemoteControl(remote.inetAddress, remote.port, sender, false, "skyotickets broadcast " + broadcast).start();
						}
					}
					new Ticket(TicketPriority.valueOf(args[0]), senderName, message, new String[]{location.getWorld().getName(), String.valueOf(location.getBlockX()), String.valueOf(location.getBlockY()), String.valueOf(location.getBlockZ())});
					sender.sendMessage(Skyotickets.messages.Messages_2);
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Please do this from the game !");
			}
		}
		else if(cmdName.equalsIgnoreCase("mytickets")) {
			if(sender instanceof Player) {
				try {
					if(!sender.hasPermission("ticket.mytickets")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					if(Skyotickets.useRemoteDatabase) {
						new RemoteControl(sender, "skyotickets view " + senderName).start();
						return true;
					}
					final ArrayList<Ticket> tickets = Skyotickets.getPlayerTickets(senderName);
					if(tickets == null) {
						sender.sendMessage(Skyotickets.messages.Messages_12);
						return true;
					}
					for(final Ticket ticket : tickets) {
						sender.sendMessage(ticket.getFormattedString());
						sender.sendMessage(ChatColor.GOLD + "-------------------------------");
					}
					sender.sendMessage(Skyotickets.messages.Messages_16);
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Please do this from the game !");
			}
		}
		else {
			final Ticket ticket;
			final Player player;
			if(args.length == 0) {
				return false;
			}
			switch(args[0].toLowerCase()) {
			case "view" :
				try {
					if(Skyotickets.useRemoteDatabase) {
						new RemoteControl(sender, "skyotickets view " + (args.length > 1 ? args[1] : "") + (args.length > 2 ? " " + args[2] : "")).start();
						return true;
					}
					if(args.length < 3) {
						if(args.length == 2) {
							if(!sender.hasPermission("ticket.view.player")) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
								return true;
							}
							sender.sendMessage(ChatColor.BOLD + args[1]);
							final ArrayList<Ticket> tickets = Skyotickets.getPlayerTickets(args[1]);
							if(tickets == null) {
								sender.sendMessage(Skyotickets.messages.Messages_12);
								return true;
							}
							for(final Ticket playerTickets : tickets) {
								sender.sendMessage(playerTickets.getFormattedString());
								sender.sendMessage(ChatColor.GOLD + "-------------------------------");
							}
						}
						else {
							if(!sender.hasPermission("ticket.view.all")) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
								return true;
							}
							final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
							if(tickets == null) {
								sender.sendMessage(Skyotickets.messages.Messages_12);
								return true;
							}
							for(final Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
								sender.sendMessage(ChatColor.BOLD + entry.getKey());
								for(final Ticket playersTickets : entry.getValue()) {
									sender.sendMessage(playersTickets.getFormattedString());
									sender.sendMessage(ChatColor.GOLD + "-------------------------------");
								}
							}
						}
						sender.sendMessage(Skyotickets.messages.Messages_16);
						return true;
					}
					if(!sender.hasPermission("ticket.view.ticket")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					ticket = Skyotickets.getTicket(args[1], args[2]);
					if(ticket == null) {
						sender.sendMessage(Skyotickets.messages.Messages_7);
						return true;
					}
					sender.sendMessage(ticket.getFormattedString());
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
				break;
			case "delete":
				try {
					if(args.length < 3) {
						if(args.length == 2) {
							if(!sender.hasPermission("ticket.delete.player")) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
								return true;
							}
							if(Skyotickets.useRemoteDatabase) {
								new RemoteControl(sender, "skyotickets delete " + senderName + args[1]).start();
								return true;
							}
							final File playerDir = Skyotickets.getPlayerDir(args[1]);
							if(playerDir.exists()) {
								Utils.delete(playerDir);
							}
						}
						else {
							if(!sender.hasPermission("ticket.delete.all")) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
								return true;
							}
							if(Skyotickets.useRemoteDatabase) {
								new RemoteControl(sender, "skyotickets delete " + senderName).start();
								return true;
							}
							if(Skyotickets.ticketsFolder.exists()) {
								for(final File file : Skyotickets.ticketsFolder.listFiles()) {
									Utils.delete(file);
								}
							}
						}
						sender.sendMessage(Skyotickets.messages.Messages_10);
						return true;
					}
					if(!sender.hasPermission("ticket.delete.ticket")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					player = Bukkit.getPlayer(args[1]);
					final String message = Skyotickets.messages.Messages_11.replaceAll("/player/", senderName);
					if(Skyotickets.useRemoteDatabase) {
						final RemoteControl remoteControl = new RemoteControl(sender, false, "skyotickets delete " + senderName + " " + args[1] + " " + args[2]);
						remoteControl.start();
						final String response = remoteControl.getResponse();
						if(response.equals("true")) {
							sender.sendMessage(Skyotickets.messages.Messages_10);
							if(player != null) {
								player.sendMessage(message);
							}
							else if(SocketListener.skyotickets.isEmpty()) {
								new RemoteControl(sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
						else {
							sender.sendMessage(response);
						}
						return true;
					}
					ticket = Skyotickets.getTicket(args[1], args[2]);
					if(ticket == null) {
						sender.sendMessage(Skyotickets.messages.Messages_7);
						return true;
					}
					if(ticket.getOwner().equals(senderName)) {
						Utils.delete(ticket.getFile());
						final File playerDir = Skyotickets.getPlayerDir(ticket.getPlayer());
						if(playerDir.list().length == 0) {
							Utils.delete(playerDir);
						}
						sender.sendMessage(Skyotickets.messages.Messages_10);
						if(player != null) {
							player.sendMessage(message);
						}
						else if(!SocketListener.skyotickets.isEmpty()) {
							for(final SocketAddress remote : SocketListener.skyotickets) {
								new RemoteControl(remote.inetAddress, remote.port, sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_6);
					}
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
				break;
			case "claim":
				try {
					if(!sender.hasPermission("ticket.claim.ticket")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					if(args.length < 3) {
						sender.sendMessage(ChatColor.RED + "/mtickets claim [player] [id].");
						return true;
					}
					player = Bukkit.getPlayer(args[1]);
					final String message = Skyotickets.messages.Messages_4.replaceAll("/player/", senderName);
					if(Skyotickets.useRemoteDatabase) {
						final RemoteControl remoteControl = new RemoteControl(sender, false, "skyotickets claim " + senderName + " " + args[1] + " " + args[2]);
						remoteControl.start();
						final String response = remoteControl.getResponse();
						if(response.equals("true")) {
							sender.sendMessage(Skyotickets.messages.Messages_3.replaceAll("/player/", args[1]));
							if(player != null) {
								player.sendMessage(message);
							}
							else if(SocketListener.skyotickets.isEmpty()) {
								new RemoteControl(sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
						else {
							sender.sendMessage(response);
						}
						return true;
					}
					ticket = Skyotickets.getTicket(args[1], args[2]);
					if(ticket == null) {
						sender.sendMessage(Skyotickets.messages.Messages_7);
						return true;
					}
					if(ticket.setOwner(senderName)) {
						ticket.setStatus(TicketStatus.TAKEN);
						ticket.saveToFile();
						sender.sendMessage(Skyotickets.messages.Messages_3.replaceAll("/player/", args[1]));
						if(player != null) {
							player.sendMessage(message);
						}
						else if(!SocketListener.skyotickets.isEmpty()) {
							for(final SocketAddress remote : SocketListener.skyotickets) {
								new RemoteControl(remote.inetAddress, remote.port, sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_5.replaceAll("/player/", ticket.getOwner()));
					}
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
				break;
			case "status":
				try {
					if(!sender.hasPermission("ticket.status.ticket")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					if(args.length < 4) {
						sender.sendMessage(ChatColor.RED + "/mtickets view [player] [id] [status].");
						return true;
					}
					if(!Utils.isTicketStatus(args[3])) {
						sender.sendMessage(ChatColor.RED + "Status :");
						for(final TicketStatus available : TicketStatus.values()) {
							sender.sendMessage(ChatColor.RED + available.name());
						}
						return true;
					}
					player = Bukkit.getPlayer(args[1]);
					final String message = Skyotickets.messages.Messages_9.replaceAll("/player/", senderName).replaceAll("/status/", args[3].toUpperCase());
					if(Skyotickets.useRemoteDatabase) {
						final RemoteControl remoteControl = new RemoteControl(sender, "skyotickets status " + senderName + " " + args[1] + " " + args[2] + " " + args[3]);
						remoteControl.start();
						final String response = remoteControl.getResponse();
						if(remoteControl.getResponse().equals("true")) {
							sender.sendMessage(Skyotickets.messages.Messages_8.replaceAll("/status/", args[3].toUpperCase()));
							if(player != null) {
								player.sendMessage(message);
							}
							else if(SocketListener.skyotickets.isEmpty()) {
								new RemoteControl(sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
						else {
							sender.sendMessage(response);
						}
						return true;
					}
					ticket = Skyotickets.getTicket(args[1], args[2]);
					if(ticket == null) {
						sender.sendMessage(Skyotickets.messages.Messages_7);
						return true;
					}
					if(ticket.getOwner().equals(senderName)) {
						final TicketStatus status = TicketStatus.valueOf(args[3]);
						ticket.setStatus(status);
						ticket.saveToFile();
						sender.sendMessage(Skyotickets.messages.Messages_8.replaceAll("/status/", status.name()));
						if(player != null) {
							player.sendMessage(message);
						}
						else if(!SocketListener.skyotickets.isEmpty()) {
							for(final SocketAddress remote : SocketListener.skyotickets) {
								new RemoteControl(remote.inetAddress, remote.port, sender, false, "skyotickets send " + args[1] + " " + message).start();
							}
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_6);
					}
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
				break;
			case "teleport":
			case "tp":
				try {
					if(sender instanceof Player) {
						if(!sender.hasPermission("ticket.teleport.ticket")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
							return true;
						}
						if(args.length < 3) {
							sender.sendMessage(ChatColor.RED + "/mtickets teleport (or tp) [player] [id].");
							return true;
						}
						final String[] location;
						if(Skyotickets.useRemoteDatabase) {
							final RemoteControl remoteControl = new RemoteControl(sender, false, "skyotickets location " + args[1] + " " + args[2]);
							remoteControl.start();
							final String response = remoteControl.getResponse();
							location = response.split(",");
							if(location.length != 4) {
								sender.sendMessage(response);
								return true;
							}
						}
						else {
							ticket = Skyotickets.getTicket(args[1], args[2]);
							if(ticket == null) {
								sender.sendMessage(Skyotickets.messages.Messages_7);
								return true;
							}
							location = ticket.getLocation();
						}
						if(Joiner.on("").join(location).contains(Skyotickets.config.NoData)) {
							sender.sendMessage(Skyotickets.messages.Messages_18);
							return true;
						}
						final World world = Bukkit.getWorld(location[0]);
						if(world == null) {
							sender.sendMessage(Skyotickets.messages.Messages_17);
							return true;
						}
						((Player)sender).teleport(new Location(world, Integer.parseInt(location[1]), Integer.parseInt(location[2]), Integer.parseInt(location[3])));
					}
					else {
						sender.sendMessage(ChatColor.RED + "Please do this from the game !");
					}
				}
				catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
					if(Skyotickets.config.Log_Use) {
						Utils.log(Utils.date() + " " + senderName + " " + ex);
					}
					ex.printStackTrace();
				}
				break;
			default:
				return false;
			}
		}
		return true;
	}

}