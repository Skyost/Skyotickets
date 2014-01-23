package fr.skyost.tickets.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.Ticket;
import fr.skyost.tickets.Ticket.TicketStatus;
import fr.skyost.tickets.utils.Utils;

public class SocketListener extends Thread {
	
	private CommandSender console;
	private String password;
	private final List<InetAddress> authCache = new ArrayList<InetAddress>();
	
	private InetAddress inetAdress;
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter sender;
	private BufferedReader reader;
	private String line;
	private String[] command;
	private String response;
	final StringBuilder stringBuilder = new StringBuilder();
	
	public SocketListener(final String host, final int port, final String password, final CommandSender console) throws IOException {
		this.console = console;
		this.password = password;
		if(Skyotickets.config.Socket_Log) {
			console.sendMessage("[Skyotickets] Creating a server socket on " + host + ":" + port + "...");
		}
		serverSocket = new ServerSocket(port, 10, InetAddress.getByName(host));
		if(Skyotickets.config.Socket_Log) {
			console.sendMessage("[Skyotickets] Created with success !");
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				socket = serverSocket.accept();
				sender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				line = reader.readLine();
				command = line.toLowerCase().split(" ");
				inetAdress = socket.getInetAddress();
				if(Skyotickets.config.Socket_Log) {
					console.sendMessage("[Skyotickets] Command performed from '" + inetAdress.getHostAddress() + "' : '" + line + "'. Sending data...");
				}
				if(command.length != 0) {
					response = "Command not found./n/Available commands : auth [password], view <player> <id>, delete <player> <id>, claim [player] [id] or status [player] [id].";
				}
				if(command[0].equals("auth")) {
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
				else {
					if(!authCache.contains(inetAdress)) {
						response = "You need to be authenticated. The command must be 'auth [password]'.";
						break;
					}
					Player player;
					Ticket ticket;
					try {
						switch(command[0]) {
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
									final File playerDir = Skyotickets.getPlayerDir(command[1]);
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
								final File playerDir = Skyotickets.getPlayerDir(ticket.getPlayer());
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
							response = "Command not found./n/Available commands : auth [password], view <player> <id>, delete <player> <id>, claim [player] [id] or status [player] [id].";
							break;
						}
					}
					catch(IOException ex) {
						response = ex.getLocalizedMessage();
						ex.printStackTrace();
					}
				}
				sender.println(ChatColor.stripColor(response));
				sender.flush();
				if(Skyotickets.config.Socket_Log) {
					console.sendMessage("[Skyotickets] Sent !");
				}
				stringBuilder.setLength(0);
			}
		}
		catch(SocketException ex) {
			if(ex.getMessage().endsWith("socket closed")) {
				if(Skyotickets.config.Socket_Log) {
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
	
	public void disable() throws IOException {
		serverSocket.setReuseAddress(true);
		serverSocket.close();
	}

}
