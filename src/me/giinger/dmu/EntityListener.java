package me.giinger.dmu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

	public final Drugs plugin;

	public EntityListener(Drugs plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void EntityDeath(EntityDeathEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player p = (Player) e.getEntity();
			if (plugin.drunk.contains(p.getName()))
				plugin.drunk.remove(p.getName());
			if (plugin.onDrugs.contains(p.getName()))
				plugin.onDrugs.remove(p.getName());
			if (plugin.heartattack.contains(p.getName()))
				plugin.heartattack.remove(p.getName());
			if (plugin.isJump.contains(p.getName()))
				plugin.isJump.remove(p.getName());
			if (plugin.noplace.contains(p.getName()))
				plugin.noplace.remove(p.getName());
		}
	}

}
