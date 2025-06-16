package fr.dreamin.dreaminTabList.impl;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.api.TabListAPI;
import fr.dreamin.dreaminTabList.api.TabListAPIFactory;
import fr.dreamin.dreaminTabList.api.events.PlayerTabJoinEvent;
import fr.dreamin.dreaminTabList.api.events.PlayerTabLeaveEvent;
import fr.dreamin.dreaminTabList.api.exceptions.PlayerNotFoundException;
import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileManager;
import fr.dreamin.dreaminTabList.impl.player.PlayerTabManagerImpl;
import fr.dreamin.dreaminTabList.impl.profile.TabProfileImpl;
import fr.dreamin.dreaminTabList.impl.profile.TabProfileManagerImpl;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Main implementation of the TabListAPI interface.
 *
 * <p>This class serves as the central coordinator for all TabList functionality,
 * managing global state, player managers, and packet operations. It provides
 * thread-safe operations and integrates with the existing DreaminTabList plugin.
 *
 * <p>The implementation handles:
 * <ul>
 *   <li>Global profile management</li>
 *   <li>Per-player tab management</li>
 *   <li>Packet sending and synchronization</li>
 *   <li>Event firing</li>
 *   <li>Configuration management</li>
 * </ul>
 *
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public class TabListAPIImpl implements TabListAPI {

  private static final String API_VERSION = "0.0.1";

  private final DreaminTabList plugin;
  /**
   * -- GETTER --
   *  Gets the plugin logger.
   *
   * @return the logger
   */
  @Getter
  private final Logger logger;
  private final TabProfileManagerImpl profileManager;
  private final Map<UUID, PlayerTabManagerImpl> playerManagers = new ConcurrentHashMap<>();

  // Global state
  private boolean enabled = true;
  private boolean globalTabHidden = false;
  private Component globalHeader;
  private Component globalFooter;

  /**
   * Creates a new TabListAPI implementation.
   *
   * @param plugin the DreaminTabList plugin instance
   */
  public TabListAPIImpl(@NotNull DreaminTabList plugin) {
    this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
    this.logger = plugin.getLogger();
    this.profileManager = new TabProfileManagerImpl(this);

    // Initialize global settings from config
    loadGlobalSettings();

    logger.info("TabListAPI v" + API_VERSION + " initialized");
  }

  @Override
  @NotNull
  public TabProfileManager getProfileManager() {
    return profileManager;
  }

  @Override
  @NotNull
  public PlayerTabManager getPlayerManager(@NotNull Player player) {
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    PlayerTabManagerImpl manager = playerManagers.get(player.getUniqueId());
    if (manager == null) throw new PlayerNotFoundException(player);

    return manager;
  }

  @Override
  public void hideTabForAll() {
    globalTabHidden = true;

    // Update all player managers
    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      manager.hideTab();
    }

    logger.info("Hidden tab for all players");
  }

  @Override
  public void showTabForAll() {
    globalTabHidden = false;

    // Update all player managers
    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      manager.showTab();
    }

    logger.info("Shown tab for all players");
  }

  @Override
  public boolean isTabHiddenForAll() {
    return globalTabHidden;
  }

  @Override
  public void setHeaderAndFooterForAll(@Nullable Component header, @Nullable Component footer) {
    this.globalHeader = header;
    this.globalFooter = footer;

    // Update all player managers
    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      manager.setHeaderAndFooter(header, footer);
    }

    logger.info("Set header and footer for all players");
  }

  @Override
  public void removeHeaderAndFooterForAll() {
    setHeaderAndFooterForAll(null, null);
  }

  @Override
  public int getManagedPlayerCount() {
    return playerManagers.size();
  }

  @Override
  @NotNull
  public String getVersion() {
    return API_VERSION;
  }

  @Override
  public boolean isEnabled() {
    return enabled && plugin.isEnabled();
  }

  @Override
  public void reloadConfiguration() {
    // Reload plugin configuration
    plugin.reloadConfig();

    // Reload global settings
    loadGlobalSettings();

    // Apply settings to all players
    applyGlobalSettingsToAllPlayers();

    logger.info("Configuration reloaded");
  }

  /**
   * Registers a player with the TabList system.
   *
   * <p>This method is called when a player joins the server and creates
   * a new PlayerTabManager for them. It also fires the PlayerTabJoinEvent.
   *
   * @param player the player to register
   * @return the created PlayerTabManager
   */
  public PlayerTabManagerImpl registerPlayer(@NotNull Player player) {
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    UUID uuid = player.getUniqueId();

    // Remove existing manager if present (shouldn't happen normally)
    PlayerTabManagerImpl existing = playerManagers.remove(uuid);
    if (existing != null) this.logger.warning("Replacing existing manager for player: " + player.getName());

    // Create new manager
    PlayerTabManagerImpl manager = new PlayerTabManagerImpl(this, player);
    playerManagers.put(uuid, manager);

    this.profileManager.addGlobalProfile(this.profileManager.createProfileFromPlayer(player));

    // Fire join event
    PlayerTabJoinEvent joinEvent = new PlayerTabJoinEvent(player, manager);
    plugin.callEvent(joinEvent);

    if (joinEvent.isCancelled()) {
      // Remove the manager if event was cancelled
      playerManagers.remove(uuid);
      this.logger.info("Player registration cancelled for: " + player.getName());
      return null;
    }

    logger.info("Registered player: " + player.getName());
    return manager;
  }

  /**
   * Unregisters a player from the TabList system.
   *
   * <p>This method is called when a player leaves the server and removes
   * their PlayerTabManager. It also fires the PlayerTabLeaveEvent.
   *
   * @param player the player to unregister
   */
  public void unregisterPlayer(@NotNull Player player) {
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    UUID uuid = player.getUniqueId();
    PlayerTabManagerImpl manager = playerManagers.remove(uuid);

    if (manager != null) {
      // Fire leave event
      PlayerTabLeaveEvent leaveEvent = new PlayerTabLeaveEvent(player);
      plugin.callEvent(leaveEvent);

      logger.info("Unregistered player: " + player.getName());
    }
  }

  /**
   * Sends a profile to all online players.
   *
   * @param profile the profile to send
   */
  public void sendProfileToAllPlayers(@NotNull TabProfile profile) {
    if (!(profile instanceof TabProfileImpl)) return;

    TabProfileImpl impl = (TabProfileImpl) profile;

    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      if (!manager.isTabHidden()) sendAddProfilePacket(manager.getPacketUser(), impl);
    }
  }

  /**
   * Updates a profile for all online players.
   *
   * @param profile the profile to update
   */
  public void updateProfileForAllPlayers(@NotNull TabProfile profile) {
    if (!(profile instanceof TabProfileImpl)) return;

    TabProfileImpl impl = (TabProfileImpl) profile;

    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      if (!manager.isTabHidden()) sendUpdateProfilePacket(manager.getPacketUser(), impl);
    }
  }

  /**
   * Removes a profile from all online players.
   *
   * @param profileId the UUID of the profile to remove
   */
  public void removeProfileFromAllPlayers(@NotNull UUID profileId) {
    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(profileId);

    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      if (!manager.isTabHidden()) manager.getPacketUser().sendPacket(packet);
    }
  }

  /**
   * Removes multiple profiles from all online players.
   *
   * @param profileIds the UUIDs of the profiles to remove
   */
  public void removeProfilesFromAllPlayers(@NotNull Collection<UUID> profileIds) {
    if (profileIds.isEmpty()) return;

    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(new ArrayList<>(profileIds));

    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      if (!manager.isTabHidden()) manager.getPacketUser().sendPacket(packet);
    }
  }

  /**
   * Resets a player to vanilla Minecraft tab behavior.
   *
   * @param player the player to reset
   */
  public void resetPlayerToVanilla(@NotNull Player player) {
    PlayerTabManagerImpl manager = playerManagers.get(player.getUniqueId());
    if (manager == null) return;

    User packetUser = manager.getPacketUser();

    // Remove all current profiles
    Collection<TabProfile> visible = manager.getVisibleProfiles();
    List<UUID> toRemove = visible.stream()
      .map(TabProfile::getUniqueId)
      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    if (!toRemove.isEmpty()) {
      WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(toRemove);
      packetUser.sendPacket(removePacket);
    }

    // Add all online players with vanilla appearance
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      UserProfile profile = new UserProfile(onlinePlayer.getUniqueId(), onlinePlayer.getName());

      WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
        profile,
        true,
        onlinePlayer.getPing(),
        GameMode.valueOf(onlinePlayer.getGameMode().name()),
        Component.text(onlinePlayer.getName()),
        null,
        0,
        true
      );

      WrapperPlayServerPlayerInfoUpdate updatePacket = new WrapperPlayServerPlayerInfoUpdate(
        EnumSet.of(
          WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
          WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
          WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
          WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
          WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
          WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT
        ),
        info
      );

      packetUser.sendPacket(updatePacket);
    }
  }

  /**
   * Sends an add profile packet to a specific user.
   *
   * @param user the packet user
   * @param profile the profile to add
   */
  public void sendAddProfilePacket(@NotNull User user, @NotNull TabProfileImpl profile) {
    UserProfile userProfile = profile.buildUserProfile();

    WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
      userProfile,
      profile.isListed(),
      profile.getLatency(),
      profile.getGameMode(),
      profile.getDisplayName(),
      null,
      profile.getSortOrder(),
      profile.isShowHat()
    );

    WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(
      EnumSet.of(
        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LIST_ORDER
      ),
      info
    );

    user.sendPacket(packet);

    // Update change tracking
    profile.snapshotState();
  }

  /**
   * Sends an update profile packet to a specific user.
   *
   * @param user the packet user
   * @param profile the profile to update
   */
  public void sendUpdateProfilePacket(@NotNull User user, @NotNull TabProfileImpl profile) {
    // Check if full replacement is needed (name or skin change)
    boolean requiresFullReplace = profile.hasNameChanged() || profile.hasSkinChanged();

    if (requiresFullReplace) {
      // Remove and re-add
      WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(profile.getUniqueId());
      user.sendPacket(removePacket);

      sendAddProfilePacket(user, profile);
      return;
    }

    // Standard update
    UserProfile userProfile = profile.buildUserProfile();

    WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
      userProfile,
      profile.isListed(),
      profile.getLatency(),
      profile.getGameMode(),
      profile.getDisplayName(),
      null,
      profile.getSortOrder(),
      profile.isShowHat()
    );

    WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(
      EnumSet.of(
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LIST_ORDER
      ),
      info
    );

    user.sendPacket(packet);

    // Update change tracking
    profile.snapshotState();
  }

  /**
   * Checks if there is a global header/footer configured.
   *
   * @return true if global header or footer is set
   */
  public boolean hasGlobalHeaderFooter() {
    return globalHeader != null || globalFooter != null;
  }

  /**
   * Gets the global header.
   *
   * @return the global header, may be null
   */
  @Nullable
  public Component getGlobalHeader() {
    return globalHeader;
  }

  /**
   * Gets the global footer.
   *
   * @return the global footer, may be null
   */
  @Nullable
  public Component getGlobalFooter() {
    return globalFooter;
  }

  /**
   * Checks if the tab is hidden globally.
   *
   * @return true if tab is hidden globally
   */
  public boolean isTabHiddenGlobally() {
    return globalTabHidden;
  }

  /**
   * Initializes the API and registers it with the factory.
   */
  public void initialize() {
    TabListAPIFactory.initialize(this);
    logger.info("TabListAPI registered with factory");
  }

  /**
   * Shuts down the API and cleans up resources.
   */
  public void shutdown() {
    enabled = false;

    // Clear all player managers
    playerManagers.clear();

    // Shutdown factory
    TabListAPIFactory.shutdown();

    logger.info("TabListAPI shut down");
  }

  /**
   * Loads global settings from the plugin configuration.
   */
  private void loadGlobalSettings() {
    if (DreaminTabList.getCodex() != null) {
      globalTabHidden = DreaminTabList.getCodex().isHideTab();

      if (DreaminTabList.getCodex().isHeaderFooterEnabled()) {
        globalHeader = DreaminTabList.getCodex().getHeaders();
        globalFooter = DreaminTabList.getCodex().getFooters();
      } else {
        globalHeader = null;
        globalFooter = null;
      }
    }
  }

  /**
   * Applies global settings to all online players.
   */
  private void applyGlobalSettingsToAllPlayers() {
    for (PlayerTabManagerImpl manager : playerManagers.values()) {
      // Apply tab visibility
      manager.setTabHiddenInternal(globalTabHidden);

      // Apply header/footer
      if (hasGlobalHeaderFooter()) {
        manager.setHeaderAndFooter(globalHeader, globalFooter);
      } else {
        manager.removeHeaderAndFooter();
      }
    }
  }
}

