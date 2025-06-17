package fr.dreamin.dreaminTabList.api.events;

import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player joins the TabList system.
 *
 * <p>This event is fired when a player connects to the server and is
 * registered with the DreaminTabList system. It provides an opportunity
 * to customize the player's initial TabList configuration.
 *
 * <p>This event is cancellable. If cancelled, the player will not be
 * registered with the TabList system and will see the default Minecraft
 * tab list behavior.
 *
 * <p>Example usage:
 * <pre>{@code
 * @EventHandler
 * public void onPlayerTabJoin(PlayerTabJoinEvent event) {
 *     Player player = event.getPlayer();
 *     PlayerTabManager manager = event.getPlayerManager();
 *
 *     // Customize tab for VIP players
 *     if (player.hasPermission("vip")) {
 *         manager.setHeaderAndFooter(
 *             Component.text("§6VIP Server"),
 *             Component.text("§7Welcome, " + player.getName())
 *         );
 *     }
 *
 *     // Cancel for certain players
 *     if (player.getName().equals("TestPlayer")) {
 *         event.setCancelled(true);
 *     }
 * }
 * }</pre>
 *
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public class PlayerTabJoinEvent extends TabListEvent implements Cancellable {

  private final Player player;
  private final PlayerTabManager playerManager;
  private boolean cancelled = false;

  /**
   * Creates a new player tab join event.
   *
   * @param player the player joining the TabList system, must not be null
   * @param playerManager the player's tab manager, must not be null
   * @throws IllegalArgumentException if player or playerManager is null
   * @since 0.0.1
   */
  public PlayerTabJoinEvent(@NotNull Player player, @NotNull PlayerTabManager playerManager) {
    super();
    if (player == null) throw new IllegalArgumentException("Player cannot be null");
    if (playerManager == null) throw new IllegalArgumentException("PlayerManager cannot be null");

    this.player = player;
    this.playerManager = playerManager;
  }

  /**
   * Gets the player joining the TabList system.
   *
   * @return the player, never null
   * @since 0.0.1
   */
  @NotNull
  public Player getPlayer() {
    return player;
  }

  /**
   * Gets the player's tab manager.
   *
   * <p>This manager can be used to customize the player's initial
   * TabList configuration before they fully join the system.
   *
   * @return the player's tab manager, never null
   * @since 0.0.1
   */
  @NotNull
  public PlayerTabManager getPlayerManager() {
    return playerManager;
  }

  /**
   * Checks if this event has been cancelled.
   *
   * <p>If cancelled, the player will not be registered with the
   * TabList system and will see default Minecraft tab behavior.
   *
   * @return true if the event is cancelled, false otherwise
   * @since 0.0.1
   */
  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Sets the cancelled state of this event.
   *
   * <p>If set to true, the player will not be registered with the
   * TabList system. This can be useful for excluding certain players
   * from custom TabList functionality.
   *
   * @param cancelled true to cancel the event, false to allow it
   * @since 0.0.1
   */
  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * Gets a string representation of this event.
   *
   * @return a string representation including player information
   * @since 0.0.1
   */
  @Override
  public String toString() {
    return "PlayerTabJoinEvent{" +
      "player=" + player.getName() +
      ", uuid=" + player.getUniqueId() +
      ", cancelled=" + cancelled +
      ", timestamp=" + getTimestamp() +
      "}";
  }
}

