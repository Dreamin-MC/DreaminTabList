package fr.dreamin.dreaminTabList.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all TabList API events.
 *
 * <p>This abstract class serves as the foundation for all events fired by
 * the DreaminTabList API. It provides common functionality and ensures
 * consistency across all API events.
 *
 * <p>All TabList API events extend this class and can be listened to using
 * the standard Bukkit event system:
 *
 * <pre>{@code
 * @EventHandler
 * public void onTabListEvent(TabListEvent event) {
 *     // Handle any TabList API event
 * }
 *
 * @EventHandler
 * public void onSpecificEvent(PlayerTabJoinEvent event) {
 *     // Handle specific event type
 * }
 * }</pre>
 *
 * <p>Events are fired at various points during TabList operations:
 * <ul>
 *   <li>When players join or leave the TabList system</li>
 *   <li>When profiles are added, updated, or removed</li>
 *   <li>When tab visibility changes</li>
 *   <li>When configuration is reloaded</li>
 * </ul>
 *
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class TabListEvent extends Event {

  private static final HandlerList handlers = new HandlerList();

  /**
   * Creates a new TabList event.
   *
   * <p>This constructor creates a synchronous event that will be
   * processed on the main server thread.
   *
   * @since 0.0.1
   */
  public TabListEvent() {
    super();
  }

  /**
   * Creates a new TabList event with specified async behavior.
   *
   * <p>Async events are processed off the main thread and should
   * not modify game state directly.
   *
   * @param isAsync true if this event is asynchronous, false for synchronous
   * @since 0.0.1
   */
  public TabListEvent(boolean isAsync) {
    super(isAsync);
  }

  /**
   * Gets the handlers for this event.
   *
   * <p>This method is required by the Bukkit event system and
   * returns the handler list for TabList events.
   *
   * @return the handler list, never null
   * @since 0.0.1
   */
  @Override
  @NotNull
  public HandlerList getHandlers() {
    return handlers;
  }

  /**
   * Gets the static handler list for TabList events.
   *
   * <p>This method is required by the Bukkit event system for
   * proper event registration and handling.
   *
   * @return the static handler list, never null
   * @since 0.0.1
   */
  @NotNull
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Gets the timestamp when this event was created.
   *
   * <p>The timestamp is in milliseconds since the Unix epoch
   * (January 1, 1970, 00:00:00 GMT).
   *
   * @return the event creation timestamp
   * @since 0.0.1
   */
  public long getTimestamp() {
    return System.currentTimeMillis();
  }

  /**
   * Gets a string representation of this event.
   *
   * <p>The string includes the event class name and key properties.
   * Subclasses may override this method to provide more specific
   * information.
   *
   * @return a string representation of this event
   * @since 0.0.1
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
      "async=" + isAsynchronous() +
      ", timestamp=" + getTimestamp() +
      "}";
  }
}

