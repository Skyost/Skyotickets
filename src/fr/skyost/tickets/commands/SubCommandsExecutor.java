package fr.skyost.tickets.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import fr.skyost.tickets.Skyotickets;
import fr.skyost.tickets.utils.Utils;

public class SubCommandsExecutor implements CommandExecutor {
	
private final List<CommandInterface> commands = new ArrayList<CommandInterface>();

	/**
	 * Registers a sub-command.
	 * 
	 * @param command The sub-command.
	 */
	
	public final void registerSubCommand(final CommandInterface command) {
		if(!commands.contains(command)) {
			commands.add(command);
		}
	}
	
	/**
	 * Gets the executor of a sub-command.
	 * 
	 * @param command The sub-command's label.
	 * 
	 * @return The executor.
	 */
	
	public final CommandInterface getExecutor(final String command) {
		for(final CommandInterface commandInterface : commands) {
			if(Arrays.asList(commandInterface.getNames()).contains(command)) {
				return commandInterface;
			}
		}
		return null;
	}
	
	/**
	 * Gets an array which contains a list of sub-commands.
	 * 
	 * @return The array.
	 */
	
	public final CommandInterface[] getCommands() {
		return commands.toArray(new CommandInterface[commands.size()]);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		final String senderName = sender.getName();
		try {
			if(args.length == 0) {
				sender.sendMessage(buildUsage(label));
				return true;
			}
			final CommandInterface commandInterface = this.getExecutor(args[0]);
			if(commandInterface == null) {
				sender.sendMessage(buildUsage(label));
				return true;
			}
			if(Skyotickets.config.logUse) {
				Utils.log(Utils.date() + " " + senderName + " has performed a Skyotickets command : /" + label + " " + Joiner.on(' ').join(args));
			}
			if(commandInterface.mustBePlayer() && !(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must perform this command from the game !");
				return true;
			}
			final String permission = commandInterface.getPermission();
			if(permission != null && !sender.hasPermission(permission)) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have the permission to perform this action.");
				return true;
			}
			if(args.length - 1 < commandInterface.getMinArgsLength()) {
				sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " " + commandInterface.getUsage());
				return true;
			}
			if(commandInterface.onCommand(sender, Arrays.copyOfRange(args, 1, args.length))) {
				return true;
			}
		}
		catch(final Exception ex) {
			sender.sendMessage(ChatColor.RED + "Exception occured : '" + ex.getClass().getName() + "'. Please notify your server admin.");
			if(Skyotickets.config.logUse) {
				Utils.log(Utils.date() + " " + senderName + " " + ex);
			}
			ex.printStackTrace();
			return true;
		}
		sender.sendMessage(buildUsage(label));
		return true;
	}
	
	/**
	 * Builds a String which contains the usage of the command.
	 * 
	 * @param label The label used.
	 * 
	 * @return The usage.
	 */
	
	public final String buildUsage(final String label) {
		return ChatColor.RED + "/" + label + " [" + getUsage() + "]";
	}
	
	/**
	 * Gets the usage of the command.
	 * 
	 * @return The usage.
	 */
	
	public final String getUsage() {
		final List<String> subCommands = new ArrayList<String>();
		for(final CommandInterface command : commands) {
			final String commandUsage = command.getUsage();
			subCommands.add(command.getNames()[0] + (commandUsage == null ? "" : " " + command.getUsage()));
		}
		return Joiner.on(" | ").join(subCommands);
	}
	
	public interface CommandInterface {
		
		/**
		 * Gets the names of the sub-command.
		 * 
		 * @return The names.
		 */
		
		public String[] getNames();
		
		/**
		 * If the sender must be a Player.
		 * 
		 * @return <b>true</b> If the sender must be a Player.
		 * <br><b>false</b> If the sender can be the Console, a CommandBlock, ...
		 */
		
		public boolean mustBePlayer();
		
		/**
		 * Gets the permission of the sub-command. Can be <b>null</b>.
		 * 
		 * @return The permission.
		 */
		
		public String getPermission();
		
		/**
		 * Gets the minimum arguments length of the sub-command.
		 * 
		 * @return The minimum arguments length;
		 */
		
		public int getMinArgsLength();
		
		/**
		 * Gets the usage of the sub-command.
		 * 
		 * @return The usage.
		 */
		
		public String getUsage();
		
		/**
		 * Wrapper for <b>onCommand(...)</b> of <b>SubCommandsExecutor</b>.
		 * 
		 * @param sender The command's sender.
		 * @param args Arguments specified.
		 * 
		 * @return <b>true</b> If the command is valid.
		 * </b>false</b> If the command is not valid.
		 * 
		 * @throws Exception If something wrong occurs.
		 */
		
		public boolean onCommand(final CommandSender sender, final String[] args) throws Exception;

	}

}
