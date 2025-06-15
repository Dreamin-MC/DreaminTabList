package fr.dreamin.dreaminTabList.event.core;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class DreaminTabListEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  /**
   * Default constructor (sync event)
   */
  public DreaminTabListEvent() {
    super();
  }

  /**
   * Constructor with specification of async mode
   *
   * @param isAsync true if event is async, else false
   */
  public DreaminTabListEvent(boolean isAsync) {
    super(isAsync);
  }

  /**
   * Get the list of handlers for this event
   *
   * @return the list of handlers
   */
  @NotNull
  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  /**
   * Get the static list of handlers
   *
   * @return the static list of handlers
   */
  @NotNull
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

}
