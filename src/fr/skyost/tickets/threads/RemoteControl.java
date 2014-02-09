package fr.skyost.tickets.threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.utils.Utils;

public class RemoteControl extends Thread {
	
	public static final HashMap<InetAddress, Integer> skyotickets = new HashMap<InetAddress, Integer>();
	
	private String response;
	
	private final InetAddress host;
	private final int port;
	private final CommandSender sender;
	private final String[] commands;
	private final boolean sendOutput;
	
	public RemoteControl(final CommandSender sender, final String... commands) throws UnknownHostException {
		this.host = InetAddress.getByName(Skyotickets.config.Remote_Host);
		this.port = Skyotickets.config.Remote_Port;
		this.sender = sender;
		this.commands = commands;
		this.sendOutput = true;
	}
	
	public RemoteControl(final CommandSender sender, final boolean sendOutput, final String... commands) throws UnknownHostException {
		this.host = InetAddress.getByName(Skyotickets.config.Remote_Host);
		this.port = Skyotickets.config.Remote_Port;
		this.sender = sender;
		this.sendOutput = sendOutput;
		this.commands = commands;
	}
	
	public RemoteControl(final InetAddress host, final int port, final CommandSender sender, final String... commands) {
		this.host = host;
		this.port = port;
		this.sender = sender;
		this.commands = commands;
		this.sendOutput = true;
	}
	
	public RemoteControl(final InetAddress host, final int port, final CommandSender sender, final boolean sendOutput, final String... commands) {
		this.host = host;
		this.port = port;
		this.sender = sender;
		this.sendOutput = sendOutput;
		this.commands = commands;
	}
	
	@Override
	public void run() {
		try {
			for(final String command : commands) {
				final Socket socket = new Socket(host, port);
				final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				writer.println(command);
				final BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				response = receiver.readLine();
				if(response.contains("You need to be authenticated")) {
					writer.println("auth " + Skyotickets.config.Remote_Password);
					writer.println("skyotickets register " + Skyotickets.config.Remote_Host + " " + Skyotickets.config.Remote_Port);
					writer.println(command);
					response = receiver.readLine();
				}
				response = response.replaceAll("/n/", "\n");
				if(command.startsWith("skyotickets register") && response.equals("true")) {
					Skyotickets.useRemoteDatabase = true;
					sender.sendMessage("[Skyotickets] The server socket will be used as the database.");
				}
				else if(!response.contains("You are authenticated")) {
					if(sendOutput) {
						sender.sendMessage(response.replaceAll("/n/", "\n"));
					}
				}
				writer.close();
				receiver.close();
				socket.close();
			}
		}
		catch(Exception ex) {
			sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex + "'. Please notify your server admin.");
			if(Skyotickets.config.Log_Use) {
				Utils.log(Utils.date() + " " + sender == null ? "null" : sender.getName() + " " + ex);
			}
			ex.printStackTrace();
		}
	}
	
	public final String getResponse() {
		waitForThread();
		return response;
	}
	
	private final void waitForThread() {
		if(this.isAlive()) {
			try {
				this.join();
			}
			catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
