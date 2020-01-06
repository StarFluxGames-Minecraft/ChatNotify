package com.starfluxgames.chatnotify;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

	private Main plugin = Main.getPlugin(Main.class);
	
	public FileConfiguration maincfg;
	public File configfile;
	
	public void setup()
	{
		if (!plugin.getDataFolder().exists())
		{
			plugin.getDataFolder().mkdir();
		}
		
		configfile = new File(plugin.getDataFolder(), "config.yml");
		
		if (!configfile.exists())
		{
			try
			{
				configfile.createNewFile();
			}catch(IOException e) {}
		}
		
		maincfg = YamlConfiguration.loadConfiguration(configfile);
		
	}
	
	
	public FileConfiguration getPlayers()
	{
		return maincfg;
	}
	
	public void saveConfig()
	{
		try
		{
			maincfg.save(configfile);
		}catch(IOException e){}
	}
	
	public void reloadConfig()
	{
		maincfg = YamlConfiguration.loadConfiguration(configfile);
	}
		
	
}
