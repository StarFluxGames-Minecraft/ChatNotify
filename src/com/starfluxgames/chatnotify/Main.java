package com.starfluxgames.chatnotify;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public ConfigManager cfgm;
	public List<Player> online_players = new ArrayList<Player>(); 
	
	public boolean require_at_symbol = false;
	
	@Override
	public void onEnable(){
		loadConfigManager();
		setupDefaultConfig();
		loadConfig();
		new cmd_cnreload(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void setupDefaultConfig()
	{
		if (!cfgm.maincfg.contains("notify-sound"))
		{
			cfgm.maincfg.set("notify-sound", "ENTITY_ENDER_DRAGON_GROWL");
		}
		if (!cfgm.maincfg.contains("require-at-symbol"))
		{
			cfgm.maincfg.set("require-at-symbol", true);
		}
		cfgm.saveConfig();
		cfgm.reloadConfig();
	}
	
	public void loadConfig()
	{
		require_at_symbol = cfgm.maincfg.getBoolean("require-at-symbol");
	}
	
	public void loadConfigManager()
	{
		cfgm = new ConfigManager();
		cfgm.setup();
		cfgm.saveConfig();
		cfgm.reloadConfig();
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		String message = e.getMessage();
		Player p = null;
		for (int i = 0; i < online_players.size(); i++) {
			if (require_at_symbol)
			{
				if (message.toLowerCase().contains("@" + online_players.get(i).getName().toLowerCase()))
				{
					p = online_players.get(i);
					break;
				}
			}
			else
			{
				if (message.toLowerCase().contains(online_players.get(i).getName().toLowerCase()))
				{
					p = online_players.get(i);
					break;
				}
			}
		}
		
		if (p != null)
		{
			try
			{
				p.playSound(p.getLocation(), Sound.valueOf(cfgm.maincfg.getString("notify-sound")), 1f, 1f);
			}catch(Exception ex)
			{
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);	
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if (!online_players.contains(e.getPlayer())) online_players.add(e.getPlayer());
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if (online_players.contains(e.getPlayer())) online_players.remove(e.getPlayer());
	}
}
