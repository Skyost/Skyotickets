package fr.skyost.tickets.listeners;

import java.io.File;
import java.io.IOException;
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
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.utils.Utils;

public class CommandsExecutor implements CommandExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		final String cmdName = cmd.getName();
		final String senderName = sender.getName();
		if(cmdName.equalsIgnoreCase("ticket")) {
			if(sender instanceof Player) {
				try {
					if(!sender.hasPermission("ticket.request")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this action.");
						return true;
					}
					if(args.length == 0) {
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
					new Ticket(senderName, new String[]{location.getWorld().getName(), String.valueOf(location.getBlockX()), String.valueOf(location.getBlockY()), String.valueOf(location.getBlockZ())}, Joiner.on(" ").join(args).replaceAll("#", "/"));
					sender.sendMessage(Skyotickets.messages.Messages_2);
				}
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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
					sender.sendMessage(ChatColor.BOLD + senderName);
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
				catch(IOException ex) {
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
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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
						player = Bukkit.getPlayer(ticket.getPlayer());
						if(player != null) {
							player.sendMessage(Skyotickets.messages.Messages_11.replaceAll("/player/", senderName));
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_6);
					}
				}
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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
					ticket = Skyotickets.getTicket(args[1], args[2]);
					if(ticket == null) {
						sender.sendMessage(Skyotickets.messages.Messages_7);
						return true;
					}
					if(ticket.setOwner(senderName)) {
						ticket.setStatus(TicketStatus.TAKEN);
						ticket.saveToFile();
						final String playerName = ticket.getPlayer();
						sender.sendMessage(Skyotickets.messages.Messages_3.replaceAll("/player/", playerName));
						player = Bukkit.getPlayer(playerName);
						if(player != null) {
							player.sendMessage(Skyotickets.messages.Messages_4.replaceAll("/player/", senderName));
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_5.replaceAll("/player/", ticket.getOwner()));
					}
				}
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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
						player = Bukkit.getPlayer(ticket.getPlayer());
						if(player != null) {
							player.sendMessage(Skyotickets.messages.Messages_9.replaceAll("/player/", senderName).replaceAll("/status/", status.name()));
						}
					}
					else {
						sender.sendMessage(Skyotickets.messages.Messages_6);
					}
				}
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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
						ticket = Skyotickets.getTicket(args[1], args[2]);
						if(ticket == null) {
							sender.sendMessage(Skyotickets.messages.Messages_7);
							return true;
						}
						final String[] location = ticket.getLocation();
						if(Joiner.on("").join(location).contains(Skyotickets.config.NoData)) {
							sender.sendMessage(Skyotickets.messages.Messages_18);
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
				catch(IOException ex) {
					sender.sendMessage(ChatColor.RED + "Error '" + ex + "' ! Please notify your server admin.");
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