package fr.dreamin.dreaminTabList.impl.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.impl.TabListAPIImpl;
import fr.dreamin.dreaminTabList.impl.profile.TabProfileImpl;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the PlayerTabManager interface.
 *
 * <p>This manager handles tab list functionality for a specific player,
 * including player-specific profiles, tab visibility, and header/footer
 * management. It provides thread-safe operations and efficient packet
 * handling using PacketEvents.
 *
 * <p>Each player has their own instance of this manager, allowing for
 * complete customization of what each player sees in their tab list.
 *
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public class PlayerTabManagerImpl implements PlayerTabManager {

  private final TabListAPIImpl api;
  private final Player player;
  private final UUID playerUuid;
  /**
   * -- GETTER --
   *  Gets the PacketEvents user for this player.
   *
   * @return the packet user
   */
  @Getter
  private final User packetUser;

  // Player-specific state
  private final Map<UUID, TabProfile> playerSpecificProfiles = new ConcurrentHashMap<>();
  private boolean tabHidden = false;
  private Component header;
  private Component footer;

  /**
   * Creates a new PlayerTabManager implementation.
   *
   * @param api the main API instance
   * @param player the player this manager is for
   */
  public PlayerTabManagerImpl(@NotNull TabListAPIImpl api, @NotNull Player player) {
    this.api = Objects.requireNonNull(api, "API cannot be null");
    this.player = Objects.requireNonNull(player, "Player cannot be null");
    this.playerUuid = player.getUniqueId();
    this.packetUser = PacketEvents.getAPI().getPlayerManager().getUser(player);

    // Initialize with global settings
    this.tabHidden = api.isTabHiddenGlobally();

    // Apply initial header/footer if configured
    if (api.hasGlobalHeaderFooter()) {
      this.header = api.getGlobalHeader();
      this.footer = api.getGlobalFooter();
      sendHeaderAndFooter();
    }
  }

  @Override
  @NotNull
  public Player getPlayer() {
    return player;
  }

  @Override
  @NotNull
  public UUID getPlayerUUID() {
    return playerUuid;
  }

  @Override
  public void addProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    UUID uuid = profile.getUniqueId();
    playerSpecificProfiles.put(uuid, profile);

    // Send to player if tab is visible
    if (!tabHidden) sendAddProfile(profile);

    api.getLogger().fine("Added player-specific profile for " + player.getName() + ": " + profile.getName());
  }

  @Override
  public void removeProfile(@NotNull UUID profileId) {
    if (profileId == null) throw new IllegalArgumentException("Profile ID cannot be null");

    TabProfile removed = playerSpecificProfiles.remove(profileId);
    if (removed != null) {
      // Remove from player's view
      sendRemoveProfile(profileId);

      api.getLogger().fine("Removed player-specific profile for " + player.getName() + ": " + removed.getName());
    }
  }

  @Override
  public void removeProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    removeProfile(profile.getUniqueId());
  }

  @Override
  public void updateProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    UUID uuid = profile.getUniqueId();
    playerSpecificProfiles.put(uuid, profile);

    // Update for player if tab is visible
    if (!tabHidden) sendUpdateProfile(profile);

    api.getLogger().fine("Updated player-specific profile for " + player.getName() + ": " + profile.getName());
  }

  @Override @NotNull
  public Collection<TabProfile> getVisibleProfiles() {
    Collection<TabProfile> visible = new ArrayList<>();

    // Add global profiles
    visible.addAll(api.getProfileManager().getGlobalProfiles());

    // Add player-specific profiles
    visible.addAll(playerSpecificProfiles.values());

    return visible;
  }

  @Override @NotNull
  public Collection<TabProfile> getPlayerSpecificProfiles() {
    return new ArrayList<>(playerSpecificProfiles.values());
  }

  @Override
  public int getVisibleProfileCount() {
    return api.getProfileManager().getGlobalProfileCount() + playerSpecificProfiles.size();
  }

  @Override @Nullable
  public TabProfile findProfile(@NotNull UUID uuid) {
    if (uuid == null) throw new IllegalArgumentException("UUID cannot be null");

    // Check player-specific profiles first
    TabProfile profile = playerSpecificProfiles.get(uuid);
    if (profile != null) return profile;

    // Check global profiles
    return api.getProfileManager().findProfile(uuid);
  }

  @Override @Nullable
  public TabProfile findProfile(@NotNull String name) {
    if (name == null) throw new IllegalArgumentException("Name cannot be null");

    // Check player-specific profiles first
    for (TabProfile profile : playerSpecificProfiles.values()) {
      if (profile.getName().equals(name)) return profile;
    }

    // Check global profiles
    return api.getProfileManager().findProfile(name);
  }

  @Override @NotNull
  public Collection<TabProfile> findProfiles(@NotNull Predicate<TabProfile> predicate) {
    if (predicate == null) throw new IllegalArgumentException("Predicate cannot be null");

    return getVisibleProfiles().stream()
      .filter(predicate)
      .collect(Collectors.toList());
  }

  @Override
  public void hideTab() {
    if (tabHidden) return; // Already hidden

    tabHidden = true;

    // Remove all visible profiles from player's view
    Collection<TabProfile> visible = getVisibleProfiles();
    List<UUID> uuids = visible.stream()
      .map(TabProfile::getUniqueId)
      .collect(Collectors.toList());

    if (!uuids.isEmpty()) {
      WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(uuids);
      packetUser.sendPacket(packet);
    }

    api.getLogger().fine("Hidden tab for player: " + player.getName());
  }

  @Override
  public void showTab() {
    if (!tabHidden) return; // Already shown

    tabHidden = false;

    // Send all visible profiles to player
    Collection<TabProfile> visible = getVisibleProfiles();
    for (TabProfile profile : visible) {
      sendAddProfile(profile);
    }

    api.getLogger().fine("Shown tab for player: " + player.getName());
  }

  @Override
  public boolean isTabHidden() {
    return tabHidden;
  }

  @Override
  public void setHeaderAndFooter(@Nullable Component header, @Nullable Component footer) {
    this.header = header;
    this.footer = footer;
    sendHeaderAndFooter();
  }

  @Override
  public void setHeader(@Nullable Component header) {
    this.header = header;
    sendHeaderAndFooter();
  }

  @Override
  public void setFooter(@Nullable Component footer) {
    this.footer = footer;
    sendHeaderAndFooter();
  }

  @Override
  public void removeHeaderAndFooter() {
    this.header = null;
    this.footer = null;
    sendHeaderAndFooter();
  }

  @Override
  @Nullable
  public Component getHeader() {
    return header;
  }

  @Override
  @Nullable
  public Component getFooter() {
    return footer;
  }

  @Override
  public void resetToVanilla() {
    // Clear player-specific profiles
    clearPlayerSpecificProfiles();

    // Remove custom header/footer
    removeHeaderAndFooter();

    // Show tab if hidden
    if (tabHidden) tabHidden = false;

    // Reset to vanilla tab list
    api.resetPlayerToVanilla(player);

    api.getLogger().fine("Reset player to vanilla tab: " + player.getName());
  }

  @Override
  public void refresh() {
    if (tabHidden) return; // No need to refresh if hidden

    // Resend all visible profiles
    Collection<TabProfile> visible = getVisibleProfiles();
    for (TabProfile profile : visible) {
      sendAddProfile(profile);
    }

    // Resend header/footer
    sendHeaderAndFooter();

    api.getLogger().fine("Refreshed tab for player: " + player.getName());
  }

  @Override
  public void clearPlayerSpecificProfiles() {
    if (playerSpecificProfiles.isEmpty()) return;

    // Remove from player's view
    List<UUID> uuids = new ArrayList<>(playerSpecificProfiles.keySet());
    playerSpecificProfiles.clear();

    if (!tabHidden && !uuids.isEmpty()) {
      WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(uuids);
      packetUser.sendPacket(packet);
    }

    api.getLogger().fine("Cleared player-specific profiles for: " + player.getName());
  }

  @Override
  public boolean isValid() {
    return player.isOnline() && packetUser != null;
  }

  /**
   * Sends a profile addition packet to the player.
   *
   * @param profile the profile to add
   */
  private void sendAddProfile(@NotNull TabProfile profile) {
    if (profile instanceof TabProfileImpl) {
      TabProfileImpl impl = (TabProfileImpl) profile;
      api.sendAddProfilePacket(packetUser, impl);
    }
  }

  /**
   * Sends a profile update packet to the player.
   *
   * @param profile the profile to update
   */
  private void sendUpdateProfile(@NotNull TabProfile profile) {
    if (profile instanceof TabProfileImpl) {
      TabProfileImpl impl = (TabProfileImpl) profile;
      api.sendUpdateProfilePacket(packetUser, impl);
    }
  }

  /**
   * Sends a profile removal packet to the player.
   *
   * @param profileId the UUID of the profile to remove
   */
  private void sendRemoveProfile(@NotNull UUID profileId) {
    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(profileId);
    packetUser.sendPacket(packet);
  }

  /**
   * Sends header and footer to the player.
   */
  private void sendHeaderAndFooter() {
    Component headerToSend = header != null ? header : Component.empty();
    Component footerToSend = footer != null ? footer : Component.empty();

    player.sendPlayerListHeaderAndFooter(headerToSend, footerToSend);
  }

  /**
   * Sets the tab hidden state without sending packets.
   *
   * <p>This method is used internally for synchronization with global settings.
   *
   * @param hidden the new hidden state
   */
  public void setTabHiddenInternal(boolean hidden) {
    this.tabHidden = hidden;
  }
}

