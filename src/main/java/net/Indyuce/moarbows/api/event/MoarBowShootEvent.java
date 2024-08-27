package net.Indyuce.moarbows.api.event;

import net.Indyuce.moarbows.bow.ArrowData;
import net.Indyuce.moarbows.bow.MoarBow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class MoarBowShootEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final ArrowData arrow;

    private boolean cancelled = false;

    public MoarBowShootEvent(ArrowData arrow) {
        super(arrow.getShooter());

        this.arrow = arrow;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ArrowData getArrowData() {
        return arrow;
    }

    public MoarBow getBow() {
        return arrow.getBow();
    }

    public Arrow getArrow() {
        return arrow.getArrow();
    }

    public int getBowLevel() {
        return arrow.getLevel();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
