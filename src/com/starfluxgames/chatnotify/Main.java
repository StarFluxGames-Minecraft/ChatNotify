package com.starfluxgames.chatnotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {

	public ConfigManager cfgm;
	public List<Player> online_players = new ArrayList<Player>();
	public HashMap<UUID, Long> lastMessage = new HashMap<UUID, Long>();

	public boolean require_at_symbol = false;
	public Long sendDelay = 0L;

	@Override
	public void onEnable() {
		loadConfigManager();
		setupDefaultConfig();
		loadConfig();
		new cmd_cnreload(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void setupDefaultConfig() {
		if (!cfgm.maincfg.contains("notify-sound")) {
			cfgm.maincfg.set("notify-sound", "ENTITY_ENDER_DRAGON_GROWL");
		}
		if (!cfgm.maincfg.contains("require-at-symbol")) {
			cfgm.maincfg.set("require-at-symbol", true);
		}
		if (!cfgm.maincfg.contains("ping-delay-seconds")) {
			cfgm.maincfg.set("ping-delay-seconds", 5);
		}
		cfgm.saveConfig();
		cfgm.reloadConfig();
	}

	public void loadConfig() {
		require_at_symbol = cfgm.maincfg.getBoolean("require-at-symbol");
		sendDelay = cfgm.maincfg.getLong("ping-delay-seconds") * 1000;
	}

	public void loadConfigManager() {
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
			if (require_at_symbol) {
				if (message.toLowerCase().contains("@" + online_players.get(i).getName().toLowerCase())) {
					p = online_players.get(i);
					break;
				}
			} else {
				if (message.toLowerCase().contains(online_players.get(i).getName().toLowerCase())) {
					p = online_players.get(i);
					break;
				}
			}
		}

		if (p != null) {
			if (lastMessage.containsKey(e.getPlayer().getUniqueId()))
			{
				if ((System.currentTimeMillis() - lastMessage.get(e.getPlayer().getUniqueId())) < 5000)
				{
					if (!e.getPlayer().hasPermission("chatnotify.bypassdelay"))
					{
						e.getPlayer().sendMessage(ChatColor.RED + "Please wait before you ping another player!");
						return;		
					}
				}
				
				lastMessage.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				
				try {
					p.playSound(p.getLocation(), Sound.valueOf(cfgm.maincfg.getString("notify-sound")), 1f, 1f);
				} catch (Exception ex) {
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!online_players.contains(e.getPlayer()))
			online_players.add(e.getPlayer());
		if (!lastMessage.containsKey(e.getPlayer().getUniqueId())) {
			lastMessage.put(e.getPlayer().getUniqueId(), 0L);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (online_players.contains(e.getPlayer()))
			online_players.remove(e.getPlayer());
		if (lastMessage.containsKey(e.getPlayer().getUniqueId())) {
			lastMessage.remove(e.getPlayer().getUniqueId());
		}
	}
}
