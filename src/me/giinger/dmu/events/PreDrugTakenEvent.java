package me.giinger.dmu.events;

import me.giinger.dmu.Drug;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreDrugTakenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Drug drug;
    private Player player;
    private boolean cancelled;

    public PreDrugTakenEvent(Player p, Drug drug) {
        this.player = p;
        this.drug = drug;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
