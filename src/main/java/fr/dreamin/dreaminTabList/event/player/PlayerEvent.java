package fr.dreamin.dreaminTabList.event.player;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListJoinEvent;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListLeaveEvent;
import fr.dreamin.dreaminTabList.impl.TabListAPIImpl;
import fr.dreamin.dreaminTabList.impl.player.PlayerTabManagerImpl;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import fr.dreamin.dreaminTabList.player.tab.TabListProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event listener for player-related events in the DreaminTabList system.
 *
 * <p>This class handles the integration between Bukkit's player events and the
 * DreaminTabList system. It manages the lifecycle of players in the tab list
 * system, including registration when players join and cleanup when they leave.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Register players with the TabList system when they join</li>
 *   <li>Initialize player-specific tab list configurations</li>
 *   <li>Handle player visibility settings (hide new players if configured)</li>
 *   <li>Clean up player data when players disconnect</li>
 *   <li>Fire custom TabList events for other plugins to listen to</li>
 * </ul>
 *
 * <p>The listener operates at normal priority to ensure it runs after most
 * other plugins have processed the join/quit events, but before any plugins
 * that might depend on the TabList system being initialized.
 *
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public class PlayerEvent implements Listener {

  /**
   * Handles player join events.
   *
   * <p>This method is called when a player joins the server and performs
   * the following operations:
   * <ol>
   *   <li>Creates a new PlayerTabList instance for the player</li>
   *   <li>Registers the player with the legacy TabList manager</li>
   *   <li>Registers the player with the new API system</li>
   *   <li>Fires a custom PlayerTabListJoinEvent</li>
   *   <li>Applies hide-player-join setting if configured</li>
   * </ol>
   *
   * <p>If the configuration option "hide-player-join" is enabled, the new
   * player will be hidden from all other players' tab lists. This can be
   * useful for creating a more controlled tab list experience.
   *
   * @param event the player join event, automatically provided by Bukkit
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onJoin(@NotNull PlayerJoinEvent event) {
    Player player = event.getPlayer();

    try {
      // Create legacy PlayerTabList instance for backward compatibility
      PlayerTabList playerTabList = new PlayerTabList(player);
      DreaminTabList.getPlayerTabListManager().addPlayer(playerTabList);

      // Register with the new API system if available
      if (DreaminTabList.getInstance().isAPIAvailable()) {
        // Cast to implementation to access internal methods
        TabListAPIImpl apiImpl = (TabListAPIImpl) DreaminTabList.getInstance().getAPI();
        PlayerTabManagerImpl apiManager = apiImpl.registerPlayer(player);

        // Log successful registration
        if (apiManager != null) DreaminTabList.getInstance().getLogger().info("Player registered with TabList API system: " + player.getName());
      }

      // Fire custom join event for other plugins to listen to
      PlayerTabListJoinEvent playerTabListJoinEvent = new PlayerTabListJoinEvent(player, playerTabList);
      DreaminTabList.getInstance().callEvent(playerTabListJoinEvent);

      // Apply hide-player-join setting if configured
      if (DreaminTabList.getCodex().isHidePlayerJoin()) {
        hidePlayerFromOthers(player);
        DreaminTabList.getInstance().getLogger().info("Hidden new player from others: " + player.getName());
      }

    } catch (Exception e) {
      // Log error but don't prevent the player from joining
      DreaminTabList.getInstance().getLogger().severe("Error processing player join for " + player.getName() + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Handles player quit events.
   *
   * <p>This method is called when a player leaves the server and performs
   * cleanup operations:
   * <ol>
   *   <li>Fires a custom PlayerTabListLeaveEvent</li>
   *   <li>Removes the player from the legacy TabList manager</li>
   *   <li>Unregisters the player from the new API system</li>
   *   <li>Cleans up any player-specific resources</li>
   * </ol>
   *
   * <p>The cleanup is performed in a specific order to ensure that other
   * plugins have a chance to access player data before it's removed.
   *
   * @param event the player quit event, automatically provided by Bukkit
   */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onLeave(@NotNull PlayerQuitEvent event) {
    Player player = event.getPlayer();

    try {
      // Fire custom leave event first so other plugins can access data
      PlayerTabListLeaveEvent playerTabListLeaveEvent = new PlayerTabListLeaveEvent(player);
      DreaminTabList.getInstance().callEvent(playerTabListLeaveEvent);

      // Remove from legacy system
      DreaminTabList.getPlayerTabListManager().removePlayer(player);

      // Unregister from new API system if available
      if (DreaminTabList.getInstance().isAPIAvailable()) {
        // Cast to implementation to access internal methods
        TabListAPIImpl apiImpl = (TabListAPIImpl) DreaminTabList.getInstance().getAPI();
        apiImpl.unregisterPlayer(player);

        DreaminTabList.getInstance().getLogger().info("Player unregistered from TabList API system: " + player.getName());
      }

    } catch (Exception e) {
      // Log error but don't prevent the player from leaving
      DreaminTabList.getInstance().getLogger().severe("Error processing player quit for " + player.getName() + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Hides a newly joined player from all other online players.
   *
   * <p>This method sends a PlayerInfoRemove packet to all other players,
   * effectively hiding the new player from their tab lists. This is used
   * when the "hide-player-join" configuration option is enabled.
   *
   * <p>The method iterates through all currently managed players and sends
   * the removal packet to each one. This ensures that the new player does
   * not appear in anyone's tab list until explicitly shown.
   *
   * @param newPlayer the player to hide from others, must not be null
   */
  private void hidePlayerFromOthers(@NotNull Player newPlayer) {
    try {
      // Send packet to all other players
      DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().forEach(playerTab -> {
        try {
          // Don't send to the new player themselves
          if (!playerTab.getPlayer().equals(newPlayer)) {
            // Get the TabListProfile of newPlayer as seen by playerTab.getPlayer()
            TabListProfile profileToHide = playerTab.getTabList().getEffectiveEntries().stream()
              .filter(profile -> profile.getUuid().equals(newPlayer.getUniqueId()))
              .findFirst()
              .orElse(null);

            if (profileToHide != null) {
              profileToHide.setListed(false);
              playerTab.getTabList().updatePlayer(profileToHide);
            }
          }
        } catch (Exception e) {
          // Log individual packet send errors but continue with others
          DreaminTabList.getInstance().getLogger().warning("Failed to hide player " + newPlayer.getName() + " from " + playerTab.getPlayer().getName() + ": " + e.getMessage());
        }
      });

    } catch (Exception e) {
      DreaminTabList.getInstance().getLogger().warning("Failed to hide new player " + newPlayer.getName() + " from others: " + e.getMessage());
    }
  }
}