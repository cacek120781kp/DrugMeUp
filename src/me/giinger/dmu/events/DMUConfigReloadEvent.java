package me.giinger.dmu.events;

import me.giinger.dmu.DrugMeUp;
import me.giinger.dmu.handlers.ConfigHandler;
import me.giinger.dmu.handlers.DrugHandler;
import me.giinger.dmu.handlers.PlayerHandler;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DMUConfigReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private DrugHandler dh;
    private PlayerHandler ph;
    private ConfigHandler ch;

    public DMUConfigReloadEvent() {
        this.dh = DrugMeUp.getDrugHandler();
        this.ph = DrugMeUp.getPlayerHandler();
        this.ch = DrugMeUp.getConfigHandler();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public DrugHandler getDrugHandler() {
        return dh;
    }

    public PlayerHandler getPlayerHandler() {
        return ph;
    }

    public ConfigHandler getConfigHandler() {
        return ch;
    }
}
