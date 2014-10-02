package fr.skyost.tickets;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import fr.skyost.tickets.commands.MyTicketsCommandExecutor;
import fr.skyost.tickets.commands.SubCommandsExecutor;
import fr.skyost.tickets.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.tickets.commands.TicketCommandExecutor;
import fr.skyost.tickets.commands.subcommands.MTicketsClaim;
import fr.skyost.tickets.commands.subcommands.MTicketsDelete;
import fr.skyost.tickets.commands.subcommands.MTicketsStatus;
import fr.skyost.tickets.commands.subcommands.MTicketsTeleport;
import fr.skyost.tickets.commands.subcommands.MTicketsView;
import fr.skyost.tickets.listeners.EventsListener;
import fr.skyost.tickets.utils.MetricsLite;
import fr.skyost.tickets.utils.Skyupdater;

public class Skyotickets extends JavaPlugin {
	
	public static ConfigFile config;
	public static MessagesFile messages;
	
	@Override
	public final void onEnable() {
		try {
			config = new ConfigFile(this.getDataFolder());
			config.load();
			messages = new MessagesFile(this.getDataFolder());
			messages.load();
			final File ticketsFolder = new File(config.ticketsDir);
			if(!ticketsFolder.exists()) {
				ticketsFolder.mkdir();
			}
			final PluginCommand ticket = this.getCommand("ticket");
			ticket.setUsage(ChatColor.RED + ticket.getUsage());
			ticket.setExecutor(new TicketCommandExecutor());
			final PluginCommand mytickets = this.getCommand("mytickets");
			mytickets.setUsage(ChatColor.RED + mytickets.getUsage());
			mytickets.setExecutor(new MyTicketsCommandExecutor());
			final PluginCommand mtickets = this.getCommand("mtickets");
			final SubCommandsExecutor mticketsExecutor = new SubCommandsExecutor();
			for(final CommandInterface subCommand : new CommandInterface[]{new MTicketsView(), new MTicketsDelete(), new MTicketsClaim(), new MTicketsStatus(), new MTicketsTeleport()}) {
				mticketsExecutor.registerSubCommand(subCommand);
			}
			mtickets.setUsage(ChatColor.RED + mticketsExecutor.buildUsage("mtickets"));
			mtickets.setExecutor(mticketsExecutor);
			Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
			if(config.enableUpdater) {
				new Skyupdater(this, 71984, this.getFile(), true, true);
			}
			if(config.enableMetrics) {
				new MetricsLite(this).start();
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}