package fr.skyost.tickets.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketPriority;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.utils.SocketAddress;
import fr.skyost.tickets.utils.Utils;

public class SocketListener extends Thread {
	
	private final String host;
	private final int port;
	private final String password;
	private final CommandSender console;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private InetAddress inetAdress;
	private final List<InetAddress> authCache = new ArrayList<InetAddress>();
	private PrintWriter sender;
	private BufferedReader receiver;
	private String line;
	private String[] command;
	private String response;
	private final StringBuilder stringBuilder = new StringBuilder();
	
	public static List<SocketAddress> skyotickets = new ArrayList<SocketAddress>();
	private final File tempFile;
	private boolean isSkyotickets;
	
	@SuppressWarnings("unchecked")
	public SocketListener(final String host, final int port, final String password, final File dataFolder) throws IOException, ClassNotFoundException {
		this.host = host;
		this.port = port;
		this.password = password;
		this.tempFile = new File(dataFolder, "temp");
		this.console = Bukkit.getConsoleSender();
		for(final String address : Skyotickets.config.Socket_WhiteListedAddress) {
			authCache.add(InetAddress.getByName(address));
		}
		if(tempFile.exists()) {
			final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(tempFile));
			skyotickets = (List<SocketAddress>)objectInputStream.readObject();
			objectInputStream.close();
			tempFile.delete();
		}
	}
	
	@Override
	public void run() {
		try {
			if(Skyotickets.config.Socket_Print) {
				console.sendMessage("[Skyotickets] Creating a server socket on " + host + ":" + port + "...");
			}
			serverSocket = new ServerSocket(port, 10, InetAddress.getByName(host));
			if(Skyotickets.config.Socket_Print) {
				console.sendMessage("[Skyotickets] Created with success !");
			}
			while(true) {
				socket = serverSocket.accept();
				sender = new PrintWriter(socket.getOutputStream(), true);
				receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				line = receiver.readLine();
				if(line == null) {
					continue;
				}
				command = line.split(" ");
				inetAdress = socket.getInetAddress();
				isSkyotickets = command[0].equalsIgnoreCase("skyotickets");
				if(Skyotickets.config.Socket_Print) {
					if(!isSkyotickets) {
						console.sendMessage("[Skyotickets] Command performed from '" + inetAdress.getHostAddress() + "' : '" + line + "'. Sending data...");
					}
				}
				if(command.length != 0) {
					if(command[0].equalsIgnoreCase("auth")) {
						if(command.length == 1) {
							response = "What is the password ? :P";
						}
						else {
							if(command[1].equals(password)) {
								if(!authCache.contains(inetAdress)) {
									authCache.add(inetAdress);
								}
								response = "You are authenticated.";
							}
							else {
								response = "Bad password.";
							}
						}
					}
					else if(authCache.contains(inetAdress)) {
						if(isSkyotickets) {
							final Ticket ticket;
							final Player player;
							switch(command[1].toLowerCase()) {
							case "create":
								if(line.contains("#")) {
									final String[] data = line.split("#");
									String message = data[1];
									String[] location = null;
									if(data.length == 3) {
										location = data[2].split(",");
									}
									for(int i = 0; i != 4; i++) {
										if(location == null || location[i] == null) {
											stringBuilder.append(Skyotickets.config.NoData);
										}
										else {
											stringBuilder.append(location[i]);
										}
										stringBuilder.append(",");
									}
									location = stringBuilder.toString().split(",");
									new Ticket(TicketPriority.valueOf(command[3]), command[2], message, location);
									message = "skyotickets broadcast " + Skyotickets.messages.Messages_1.replaceAll("/player/", command[2]).replaceAll("/ticket/", message).replaceAll("/world/", location[0]).replaceAll("/x/", location[1]).replaceAll("/y/", location[2]).replaceAll("/z/", location[3]).replaceAll("/priority/", command[3].toUpperCase());
									for(final SocketAddress remote : SocketListener.skyotickets) {
										new PrintWriter(new Socket(remote.inetAddress, remote.port).getOutputStream(), true).println(message);
									}
									response = Skyotickets.messages.Messages_2;
								}
								break;
							case "view":
								try {
									if(command.length < 4) {
										if(command.length == 3) {
											stringBuilder.append(ChatColor.BOLD + command[2] + ChatColor.RESET + "/n/");
											final ArrayList<Ticket> tickets = Skyotickets.getPlayerTickets(command[2]);
											if(tickets == null) {
												response = Skyotickets.messages.Messages_12;
												break;
											}
											for(final Ticket playerTickets : tickets) {
												stringBuilder.append(playerTickets.getFormattedString("/n/"));
												stringBuilder.append(ChatColor.GOLD + "/n/-------------------------------/n/" + ChatColor.RESET);
											}
											response = stringBuilder.toString();
										}
										else {
											final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
											if(tickets == null) {
												response = Skyotickets.messages.Messages_12;
												break;
											}
											for(final Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
												stringBuilder.append(ChatColor.BOLD + entry.getKey() + ChatColor.RESET + "/n/");
												for(final Ticket playersTickets : entry.getValue()) {
													stringBuilder.append(playersTickets.getFormattedString("/n/"));
													stringBuilder.append(ChatColor.GOLD + "/n/-------------------------------/n/" + ChatColor.RESET);
												}
											}
										}
										stringBuilder.append(Skyotickets.messages.Messages_16);
										response = stringBuilder.toString();
										break;
									}
									ticket = Skyotickets.getTicket(command[2], command[3]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									response = ticket.getFormattedString("/n/");
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "delete":
								try {
									if(command.length < 5) {
										if(command.length == 5) {
											final File playerDir = Skyotickets.getPlayerDir(command[3]);
											if(playerDir.exists()) {
												Utils.delete(playerDir);
											}
										}
										else {
											if(Skyotickets.ticketsFolder.exists()) {
												for(final File file : Skyotickets.ticketsFolder.listFiles()) {
													Utils.delete(file);
												}
											}
										}
										response = Skyotickets.messages.Messages_10;
										break;
									}
									ticket = Skyotickets.getTicket(command[3], command[4]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.getOwner().equals(command[2])) {
										Utils.delete(ticket.getFile());
										final File playerDir = Skyotickets.getPlayerDir(ticket.getPlayer());
										if(playerDir.list().length == 0) {
											Utils.delete(playerDir);
										}
										response = "true";
									}
									else {
										response = Skyotickets.messages.Messages_6;
									}
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "claim":
								try {
									ticket = Skyotickets.getTicket(command[3], command[4]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.setOwner(command[2])) {
										ticket.setStatus(TicketStatus.TAKEN);
										ticket.saveToFile();
										response = "true";
									}
									else {
										response = Skyotickets.messages.Messages_5.replaceAll("/player/", ticket.getOwner());
									}
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "status":
								try {
									ticket = Skyotickets.getTicket(command[3], command[4]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.getOwner().equals(command[2])) {
										final TicketStatus status = TicketStatus.valueOf(command[5]);
										ticket.setStatus(status);
										ticket.saveToFile();
										response = "true";
									}
									else {
										response = Skyotickets.messages.Messages_6;
									}
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "location":
								try {
									ticket = Skyotickets.getTicket(command[2], command[3]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									response = Joiner.on(",").join(ticket.getLocation());
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "register":
								skyotickets.add(new SocketAddress(InetAddress.getByName(command[2]), Integer.parseInt(command[3])));
								response = "true";
								break;
							case "player-join":
								try {
									final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
									if(tickets == null) {
										response = Skyotickets.messages.Messages_13;
										break;
									}
									String owner;
									final ArrayList<String> newTickets = new ArrayList<String>();
									for(final Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
										for(Ticket newTicket : entry.getValue()) {
											owner = newTicket.getOwner();
											if(owner.equals(command[2]) || (newTicket.getStatus() == TicketStatus.OPEN && owner.equals(Skyotickets.config.NoOwner))) {
												newTickets.add(newTicket.getFormattedString("/n/"));
											}
										}
									}
									for(final String newTicket : newTickets) {
										stringBuilder.append(newTicket + "/n/");
										stringBuilder.append(ChatColor.GOLD + "-------------------------------/n/" + ChatColor.RESET);
									}
									stringBuilder.append(Skyotickets.messages.Messages_15.replaceAll("/n/", String.valueOf(newTickets.size())) + "/n/");
									stringBuilder.append(Skyotickets.messages.Messages_16);
									response = stringBuilder.toString();
								}
								catch(Exception ex) {
									response = ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.";
									if(Skyotickets.config.Log_Use) {
										Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
									}
									ex.printStackTrace();
								}
								break;
							case "broadcast":
								Bukkit.broadcast(line.substring(22).replaceAll("/n/", "\n"), "ticket.view.ticket");
								response = "true";
								break;
							case "send":
								String message = line.substring(18 + command[2].length());
								player = Bukkit.getPlayer(command[2]);
								if(player != null) {
									player.sendMessage(message.replaceAll("/n/", "\n"));
									response = "true";
								}
								else if(!skyotickets.isEmpty()) {
									message = "skyotickets send " + command[2] + " " + message;
									for(final SocketAddress remote : SocketListener.skyotickets) {
										new PrintWriter(new Socket(remote.inetAddress, remote.port).getOutputStream(), true).println(message);
									}
									response = "true";
								}
								else {
									response = "false";
								}
								break;
							default:
								response = "false";
								break;
							}
						}
						else {
							if(Skyotickets.config.Log_Use) {
								Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " has performed Skyotickets remote command '" + line + "'");
							}
							final Player player;
							final File playerDir;
							final Ticket ticket;
							try {
								switch(command[0].toLowerCase()) {
								case "create":
									if(command.length < 3) {
										response = "create [player] [priority] #[message]#<world,x,y,z>/n/Example : 'create Skyost #My message#Unicorn Island,10,256,10'.";
										break;
									}
									playerDir = Skyotickets.getPlayerDir(command[1]);
									if(playerDir.exists()) {
										if(playerDir.listFiles().length == Skyotickets.config.MaxTicketsByPlayer) {
											response = Skyotickets.messages.Messages_14;
											break;
										}
									}
									if(line.contains("#")) {
										final String[] data = line.split("#");
										final String message = data[1];
										String[] location = null;
										if(data.length == 3) {
											location = data[2].split(",");
										}
										for(int i = 1; i != 4; i++) {
											if(location == null || location[i] == null) {
												stringBuilder.append(Skyotickets.config.NoData);
											}
											else {
												stringBuilder.append(location[i]);
											}
											stringBuilder.append(",");
										}
										if(!Utils.isTicketPriority(command[2])) {
											stringBuilder.append("Priorities :");
											for(final TicketPriority priority : TicketPriority.values()) {
												stringBuilder.append(priority.name());
											}
											response = stringBuilder.toString();
											break;
										}
										new Ticket(TicketPriority.valueOf(command[2]), command[1], message, stringBuilder.toString().split(","));
										response = Skyotickets.messages.Messages_2;
										break;
									}
									response = "create [player] [priority] #[message]#<world,x,y,z>/n/Example : 'create Skyost #My message#Unicorn Island,10,256,10'.";
									break;
								case "view":
									if(command.length < 3) {
										if(command.length == 2) {
											final ArrayList<Ticket> tickets = Skyotickets.getPlayerTickets(command[1]);
											if(tickets == null) {
												response = Skyotickets.messages.Messages_12;
												break;
											}
											for(final Ticket playerTickets : tickets) {
												stringBuilder.append(playerTickets.getFormattedString("/n/"));
												stringBuilder.append("/n/-------------------------------/n/");
											}
											response = stringBuilder.toString();
										}
										else {
											final HashMap<String, ArrayList<Ticket>> tickets = Skyotickets.getTickets();
											if(tickets == null) {
												response = Skyotickets.messages.Messages_12;
												break;
											}
											for(final Entry<String, ArrayList<Ticket>> entry : tickets.entrySet()) {
												stringBuilder.append(entry.getKey() + " :/n/");
												for(final Ticket playersTickets : entry.getValue()) {
													stringBuilder.append(playersTickets.getFormattedString("/n/"));
													stringBuilder.append("/n/-------------------------------/n/");
												}
											}
											response = stringBuilder.toString();
										}
										break;
									}
									ticket = Skyotickets.getTicket(command[1], command[2]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									response = ticket.getFormattedString("/n/");
									break;
								case "delete":
									if(command.length < 3) {
										if(command.length == 2) {
											playerDir = Skyotickets.getPlayerDir(command[1]);
											if(playerDir.exists()) {
												Utils.delete(playerDir);
											}
										}
										else {
											if(Skyotickets.ticketsFolder.exists()) {
												for(final File file : Skyotickets.ticketsFolder.listFiles()) {
													Utils.delete(file);
												}
											}
										}
										response = Skyotickets.messages.Messages_10;
										break;
									}
									ticket = Skyotickets.getTicket(command[1], command[2]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.getOwner().equals(Skyotickets.config.Socket_Name)) {
										Utils.delete(ticket.getFile());
										playerDir = Skyotickets.getPlayerDir(ticket.getPlayer());
										if(playerDir.list().length == 0) {
											Utils.delete(playerDir);
										}
										response = Skyotickets.messages.Messages_10;
										player = Bukkit.getPlayer(ticket.getPlayer());
										if(player != null) {
											player.sendMessage(Skyotickets.messages.Messages_11.replaceAll("/player/", Skyotickets.config.Socket_Name));
										}
									}
									else {
										response = Skyotickets.messages.Messages_6;
									}
									break;
								case "claim":
									if(command.length < 3) {
										response = "claim [player] [id].";
										break;
									}
									ticket = Skyotickets.getTicket(command[1], command[2]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.setOwner(Skyotickets.config.Socket_Name)) {
										ticket.setStatus(TicketStatus.TAKEN);
										ticket.saveToFile();
										final String playerName = ticket.getPlayer();
										response = Skyotickets.messages.Messages_3.replaceAll("/player/", playerName);
										player = Bukkit.getPlayer(playerName);
										if(player != null) {
											player.sendMessage(Skyotickets.messages.Messages_4.replaceAll("/player/", Skyotickets.config.Socket_Name));
										}
									}
									else {
										response = Skyotickets.messages.Messages_5.replaceAll("/player/", ticket.getOwner());
									}
									break;
								case "status":
									if(command.length < 4) {
										response = "status [player] [id] [status]";
										break;
									}
									if(!Utils.isTicketStatus(command[3].toUpperCase())) {
										stringBuilder.append("Status :");
										for(final TicketStatus available : TicketStatus.values()) {
											stringBuilder.append(available.name());
										}
										response = stringBuilder.toString();
										break;
									}
									ticket = Skyotickets.getTicket(command[1], command[2]);
									if(ticket == null) {
										response = Skyotickets.messages.Messages_7;
										break;
									}
									if(ticket.getOwner().equals(Skyotickets.config.Socket_Name)) {
										final TicketStatus status = TicketStatus.valueOf(command[3].toUpperCase());
										ticket.setStatus(status);
										ticket.saveToFile();
										response = Skyotickets.messages.Messages_8.replaceAll("/status/", status.name());
										player = Bukkit.getPlayer(ticket.getPlayer());
										if(player != null) {
											player.sendMessage(Skyotickets.messages.Messages_9.replaceAll("/player/", Skyotickets.config.Socket_Name).replaceAll("/status/", status.name()));
										}
									}
									else {
										response = Skyotickets.messages.Messages_6;
									}
									break;
								default:
									response = "Command not found./n/Available commands : auth [password], create [player] [priority] #[message]#<world,x,y,z>, view <player> <id>, delete <player> <id>, claim [player] [id] or status [player] [id].";
									break;
								}
							}
							catch(Exception ex) {
								response = ex.getLocalizedMessage();
								if(Skyotickets.config.Log_Use) {
									Utils.log(Utils.date() + " " + inetAdress.getHostAddress() + " " + ex);
								}
								ex.printStackTrace();
							}
						}
					}
					else {
						response = "You need to be authenticated. The command must be 'auth [password]'.";
					}
				}
				else {
					response = "auth [password], create [player] [priority] #[message]#<world,x,y,z>, view <player> <id>, delete <player> <id>, claim [player] [id] or status [player] [id].";
				}
				sender.println(isSkyotickets ? response : ChatColor.stripColor(response));
				sender.flush();
				if(Skyotickets.config.Socket_Print) {
					if(!isSkyotickets) {
						console.sendMessage("[Skyotickets] Sent !");
					}
				}
				stringBuilder.setLength(0);
			}
		}
		catch(SocketException ex) {
			if(ex.getMessage().endsWith("socket closed")) {
				if(Skyotickets.config.Socket_Print) {
					console.sendMessage("[Skyotickets] Server socket closed.");
				}
			}
			else {
				ex.printStackTrace();
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public final void disable() throws IOException {
		serverSocket.setReuseAddress(true);
		serverSocket.close();
		if(!skyotickets.isEmpty()) {
			final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(tempFile));
			objectOutputStream.writeObject(skyotickets);
			objectOutputStream.flush();
			objectOutputStream.close();
		}
	}

}