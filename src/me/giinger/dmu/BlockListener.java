package me.giinger.dmu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

	public final Drugs plugin;

	public BlockListener(Drugs plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = (Player) event.getPlayer();
		if (plugin.noplace.contains(p.getName())) {
			event.setCancelled(true);
		}
	}

}
