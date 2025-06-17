package fr.dreamin.dreaminTabList.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called when a player leaves the TabList system.
 *
 * <p>This event is fired when a player disconnects from the server and is
 * removed from the DreaminTabList system. It provides an opportunity to
 * perform cleanup operations or save player-specific TabList data.
 *
 * <p>This event is not cancellable as the player has already disconnected
 * and the removal process cannot be stopped.
 *
 * <p>Example usage:
 * <pre>{@code
 * @EventHandler
 * public void onPlayerTabLeave(PlayerTabLeaveEvent event) {
 *     Player player = event.getPlayer();
 *     UUID playerUuid = event.getPlayerUuid();
 *
 *     // Save player-specific TabList preferences
 *     savePlayerTabPreferences(playerUuid);
 *
 *     // Log the departure
 *     getLogger().info("Player " + player.getName() + " left the TabList system");
 *
 *     // Clean up any player-specific resources
 *     cleanupPlayerResources(playerUuid);
 * }
 * }</pre>
 *
 * <p><strong>Note:</strong> The player object may not be fully valid at the
 * time this event is fired, as the player is in the process of disconnecting.
 * Use the UUID for any operations that need to persist beyond the player's
 * connection.
 *
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public class PlayerTabLeaveEvent extends TabListEvent {

  /**
   * -- GETTER --
   *  Gets the player leaving the TabList system.
   *  <p><strong>Warning:</strong> The player object may not be fully valid
   *  at the time this event is fired, as the player is disconnecting.
   *  For persistent operations, use
   *  instead.
   *
   * @return the player, may be null if not available
   *
   */
  @Getter
  private final Player player;
  private final UUID playerUuid;
  private final String playerName;

  /**
   * Creates a new player tab leave event.
   *
   * @param player the player leaving the TabList system, must not be null
   * @throws IllegalArgumentException if player is null
   * @since 0.0.1
   */
  public PlayerTabLeaveEvent(@NotNull Player player) {
    super();
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    this.player = player;
    this.playerUuid = player.getUniqueId();
    this.playerName = player.getName();
  }

  /**
   * Creates a new player tab leave event with explicit UUID and name.
   *
   * <p>This constructor is useful when the player object may not be
   * fully available but the UUID and name are known.
   *
   * @param player the player leaving, may be null if not available
   * @param playerUuid the UUID of the leaving player, must not be null
   * @param playerName the name of the leaving player, must not be null
   * @throws IllegalArgumentException if playerUuid or playerName is null
   * @since 0.0.1
   */
  public PlayerTabLeaveEvent(Player player, @NotNull UUID playerUuid, @NotNull String playerName) {
    super();
    if (playerUuid == null) throw new IllegalArgumentException("Player UUID cannot be null");
    if (playerName == null) throw new IllegalArgumentException("Player name cannot be null");

    this.player = player;
    this.playerUuid = playerUuid;
    this.playerName = playerName;
  }

  /**
   * Gets the UUID of the player leaving the TabList system.
   *
   * <p>This UUID is always available and should be used for any
   * operations that need to persist beyond the player's connection.
   *
   * @return the player's UUID, never null
   * @since 0.0.1
   */
  @NotNull
  public UUID getPlayerUuid() {
    return playerUuid;
  }

  /**
   * Gets the name of the player leaving the TabList system.
   *
   * <p>This name is captured at the time of the event and is
   * always available even if the player object is not.
   *
   * @return the player's name, never null
   * @since 0.0.1
   */
  @NotNull
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Checks if the player object is available and valid.
   *
   * <p>The player object may not be available if the player has
   * already fully disconnected or if there were issues during
   * the disconnection process.
   *
   * @return true if the player object is available and valid, false otherwise
   * @since 0.0.1
   */
  public boolean isPlayerAvailable() {
    return player != null && player.isOnline();
  }

  /**
   * Gets a string representation of this event.
   *
   * @return a string representation including player information
   * @since 0.0.1
   */
  @Override
  public String toString() {
    return "PlayerTabLeaveEvent{" +
      "playerName='" + playerName + '\'' +
      ", playerUuid=" + playerUuid +
      ", playerAvailable=" + isPlayerAvailable() +
      ", timestamp=" + getTimestamp() +
      "}";
  }
}

