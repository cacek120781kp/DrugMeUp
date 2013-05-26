package me.giinger.dmu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EntityListener implements Listener {

    public final Drugs plugin;

    public EntityListener(Drugs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        Player p = (Player) e.getEntity();
        if (plugin.getDrunk().contains(p.getName())) {
            plugin.getDrunk().remove(p.getName());
        }
        if (plugin.getOnDrugs().contains(p.getName())) {
            plugin.getOnDrugs().remove(p.getName());
        }
        if (plugin.getHeartAttack().contains(p.getName())) {
            plugin.getHeartAttack().remove(p.getName());
        }
        if (plugin.getIsJump().contains(p.getName())) {
            plugin.getIsJump().remove(p.getName());
        }
        if (plugin.getNoPlace().contains(p.getName())) {
            plugin.getNoPlace().remove(p.getName());
        }

    }
}
