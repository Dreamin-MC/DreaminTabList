package fr.dreamin.dreaminTabList.event.custom.playerUpdate;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.api.TabListAPIFactory;
import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.event.core.DreaminTabListCancelEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Event fired when a PlayerInfoUpdate packet with ADD_PLAYER action is sent.
 *
 * <p>This event allows plugins to intercept and modify player addition packets
 * before they are sent to clients. It provides access to the PlayerTabManager
 * for the target player and the packet contents.
 *
 * <p>Example usage:
 * <pre>{@code
 * @EventHandler
 * public void onPlayerAdd(PacketPlayerUpdateAddEvent event) {
 *     PlayerTabManager manager = event.getPlayerTabManager();
 *     if (manager != null) {
 *         // Modify or cancel the packet
 *         event.setCancelled(true);
 *     }
 * }
 * }</pre>
 *
 * @author Dreamin
 * @since 0.0.3
 */
@Getter
public class PacketPlayerUpdateEvent extends DreaminTabListCancelEvent {

  private @Nullable PlayerTabManager playerTabManager;
  private @NotNull List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfos = new ArrayList<>();
  private @NotNull EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions;
  private @NotNull UUID playerUUID;
  private @Nullable Player player;

  /**
   * Event when packet playerUpdate with ADD_PLAYER action is sent.
   *
   * @param playerUUID the player's UUID to whom the packet will be sent
   * @param packet the packet being sent
   */
  public PacketPlayerUpdateEvent(@NotNull UUID playerUUID, @NotNull WrapperPlayServerPlayerInfoUpdate packet) {
    super();
    this.playerUUID = playerUUID;
    this.playerInfos.addAll(packet.getEntries());
    this.actions = packet.getActions().clone();

    // Retrieve the player from UUID
    this.player = Bukkit.getPlayer(playerUUID);

    // Retrieve the PlayerTabManager
    this.playerTabManager = getPlayerTabManagerFromUUID(playerUUID);
  }

  /**
   * Retrieves the PlayerTabManager for the given UUID.
   *
   * <p>This method attempts to get the PlayerTabManager using multiple approaches:
   * <ol>
   *   <li>Via the TabListAPI if available</li>
   *   <li>Via the legacy system as fallback</li>
   * </ol>
   *
   * @param uuid the player's UUID
   * @return the PlayerTabManager, or null if not found or player is offline
   */
  @Nullable
  private PlayerTabManager getPlayerTabManagerFromUUID(@NotNull UUID uuid) {
    try {
      // First, try to get the player
      Player targetPlayer = Bukkit.getPlayer(uuid);
      if (targetPlayer == null || !targetPlayer.isOnline()) return null;

      // Try to get via API if available
      if (DreaminTabList.getInstance().isAPIAvailable()) {
        try {
          return TabListAPIFactory.getAPI().getPlayerManager(targetPlayer);
        } catch (Exception e) {
          DreaminTabList.getInstance().getLogger().warning(
            "Failed to get PlayerTabManager via API for " + targetPlayer.getName() + ": " + e.getMessage()
          );
        }
      }

      // Fallback: Try to get via legacy system
      try {
        if (DreaminTabList.getPlayerTabListManager() != null) {
          var legacyManager = DreaminTabList.getPlayerTabListManager().getPlayer(targetPlayer);
          if (legacyManager != null) {
            // If we have a legacy manager but need API manager, try to get it
            if (DreaminTabList.getInstance().isAPIAvailable()) return TabListAPIFactory.getAPI().getPlayerManager(targetPlayer);
          }
        }
      } catch (Exception e) {
        DreaminTabList.getInstance().getLogger().warning(
          "Failed to get PlayerTabManager via legacy system for " + targetPlayer.getName() + ": " + e.getMessage()
        );
      }

    } catch (Exception e) {
      DreaminTabList.getInstance().getLogger().severe(
        "Unexpected error while retrieving PlayerTabManager for UUID " + uuid + ": " + e.getMessage()
      );
    }

    return null;
  }

  /**
   * Gets the UUID of the player to whom the packet is being sent.
   *
   * @return the player's UUID, never null
   */
  @NotNull
  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  /**
   * Gets the player to whom the packet is being sent.
   *
   * @return the player, or null if offline
   */
  @Nullable
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Gets the PlayerTabManager for the target player.
   *
   * <p>This manager can be used to interact with the player's tab list
   * configuration and state.
   *
   * @return the PlayerTabManager, or null if not available
   */
  @Nullable
  public PlayerTabManager getPlayerTabManager() {
    return this.playerTabManager;
  }

  /**
   * Gets the list of player info entries in the packet.
   *
   * <p>This list contains all the players being added to the tab list.
   * The list is mutable and can be modified to change the packet contents.
   *
   * @return the list of player info entries, never null
   */
  @NotNull
  public List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> getPlayerInfos() {
    return this.playerInfos;
  }

  /**
   * Gets the number of players being added in this packet.
   *
   * @return the number of player entries
   */
  public int getPlayerCount() {
    return this.playerInfos.size();
  }

  /**
   * Gets the list of actions from the packet
   *
   * @return the list of actions
   */
  public EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> getActions() {
    return this.actions;
  }

  /**
   * Checks if the target player is online.
   *
   * @return true if the player is online, false otherwise
   */
  public boolean isPlayerOnline() {
    return this.player != null && this.player.isOnline();
  }

  /**
   * Checks if a PlayerTabManager is available for the target player.
   *
   * @return true if a PlayerTabManager is available, false otherwise
   */
  public boolean hasPlayerTabManager() {
    return this.playerTabManager != null;
  }

  /**
   * Gets the name of the target player.
   *
   * @return the player's name, or "Unknown" if player is offline
   */
  @NotNull
  public String getPlayerName() {
    return this.player != null ? this.player.getName() : "Unknown";
  }

  /**
   * Adds a player info entry to the packet.
   *
   * @param playerInfo the player info to add
   * @throws IllegalArgumentException if playerInfo is null
   * @return true if the player insert, false already insert
   */
  public boolean addPlayerInfo(@NotNull WrapperPlayServerPlayerInfoUpdate.PlayerInfo playerInfo) {
    if (playerInfo == null) throw new IllegalArgumentException("PlayerInfo cannot be null");

    return this.playerInfos.add(playerInfo);
  }

  /**
   * Removes a player info entry from the packet by UUID.
   *
   * @param uuid the UUID of the player to remove
   * @return true if a player was removed, false otherwise
   */
  public boolean removePlayerInfo(@NotNull UUID uuid) {
    return this.playerInfos.removeIf(info -> info.getGameProfile().getUUID().equals(uuid));
  }

  /**
   * Removes a player info entry from the packet by UUID.
   *
   * @param name the name of the player to remove
   * @return true if a player was removed, false otherwise
   */
  public boolean removePlayerInfo(@NotNull String name) {
    return this.playerInfos.removeIf(info -> info.getGameProfile().getName().equals(name));
  }

  /**
   * Clears all player info entries from the packet.
   */
  public void clearPlayerInfos() {
    this.playerInfos.clear();
  }

  /**
   * Add a action to the list for the packet
   *
   * @param action the action to add
   * @return true if action insert, false already insert
   */
  public boolean addAction(WrapperPlayServerPlayerInfoUpdate.Action action) {
    return this.actions.add(action);
  }

  /**
   * Remove a action to the list for the packet
   *
   * @param action the action to remove
   * @return true if action removed, false not in the list
   */
  public boolean removeAction(WrapperPlayServerPlayerInfoUpdate.Action action) {
    return this.actions.remove(action);
  }

  /**
   * Clears all action from the packet
   */
  public void clearActions() {
    this.actions.clear();
  }

}