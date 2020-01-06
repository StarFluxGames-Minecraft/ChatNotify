package com.starfluxgames.chatnotify;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class cmd_cnreload implements CommandExecutor{

	private Main plugin;
	
	public cmd_cnreload(Main plugin)
	{
		this.plugin = plugin;
		plugin.getCommand("cnreload").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("chatnotify.reload") || sender.isOp())
		{
			plugin.cfgm.reloadConfig();
			plugin.loadConfig();
			sender.sendMessage(ChatColor.GREEN + "Config Reloaded");	
		}
		
		return false;
	}
	
}