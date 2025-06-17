package fr.dreamin.dreaminTabList.player.tab;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Manages the tab list for a specific player, handling custom profiles, visibility, and header/footer.
 * This class uses PacketEvents to manipulate player information packets sent to the client.
 */
@Getter @Setter
public class TabList {

  /**
   * The PlayerTabList instance associated with this TabList manager.
   */
  private PlayerTabList playerTabList;
  /**
   * The PacketEvents User object for the player, used to send packets.
   */
  private User packetUser;
  /**
   * The global cache for TabList profiles.
   */
  private TabListCache globalCache;

  /**
   * A map storing player-specific TabList profiles, overriding global profiles if present.
   */
  private Map<UUID, TabListProfile> localEntries = new HashMap<>();

  /**
   * Indicates whether the tab list is currently hidden for this player.
   */
  private boolean hideTab = DreaminTabList.getCodex().isHideTab();

  /**
   * Constructs a new TabList manager for a given player.
   *
   * @param playerTabList The PlayerTabList instance for the player.
   */
  public TabList(PlayerTabList playerTabList) {
    this.playerTabList = playerTabList;
    this.packetUser = PacketEvents.getAPI().getPlayerManager().getUser(playerTabList.getPlayer());
    this.globalCache = DreaminTabList.getPlayerTabListManager().getGlobalCache();

    if (this.hideTab) hideTab();
    if (DreaminTabList.getCodex().isHeaderFooterEnabled()) Bukkit.getScheduler().runTaskLater(DreaminTabList.getInstance(), this::setHeaderAndFooter, 20L);
  }

  /**
   * Retrieves all effective TabList entries for this player, combining global and local profiles.
   *
   * @return A collection of effective TabList profiles.
   */
  public Collection<TabListProfile> getEffectiveEntries() {
    Map<UUID, TabListProfile> result = new HashMap<>(globalCache.getAll());
    result.putAll(localEntries);
    return result.values();
  }

  /**
   * Sets the header and footer for the player's tab list.
   */
  public void setHeaderAndFooter() {
    this.playerTabList.getPlayer().sendPlayerListHeaderAndFooter(DreaminTabList.getCodex().getHeaders(), DreaminTabList.getCodex().getFooters());
  }

  /**
   * Removes a player's profile from this player's tab list by setting its 'listed' status to false.
   * The profile is also removed from local entries.
   *
   * @param uuid The UUID of the player profile to remove.
   */
  public void removePlayer(UUID uuid) {
    TabListProfile profile = this.localEntries.remove(uuid);
    if (profile != null) {
      profile.setListed(false);
      updatePlayer(profile);
    }
  }

  /**
   * Adds a real player's profile to this player's tab list.
   *
   * @param player The Bukkit Player object to add.
   */
  public void addPlayer(Player player) {
    TabListProfile profile = new TabListProfile(player);
    this.localEntries.put(profile.getUuid(), profile);
    sendAdd(profile);
  }

  /**
   * Adds a fake player's profile to this player's tab list.
   *
   * @param profile The TabListProfile of the fake player to add.
   */
  public void addFakePlayer(TabListProfile profile) {
    this.localEntries.put(profile.getUuid(), profile);
    sendAdd(profile);
  }

  /**
   * Sends an ADD_PLAYER packet for a given TabListProfile to the client.
   * This makes the player visible in the tab list.
   *
   * @param profile The TabListProfile to add.
   */
  public void sendAdd(TabListProfile profile) {
    UserProfile userProfile = profile.buildUserProfile();

    WrapperPlayServerPlayerInfoUpdate.PlayerInfo info =
      new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
        userProfile,
        profile.isListed(),
        profile.getLatency(),
        profile.getGameMode(),
        profile.getDisplayName(),
        null,
        profile.getOrder(),
        profile.isShowHat()
      );

    WrapperPlayServerPlayerInfoUpdate packet =
      new WrapperPlayServerPlayerInfoUpdate(EnumSet.of(
        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT
      ), info);

    this.packetUser.sendPacket(packet);
  }

  /**
   * Updates a player's profile in the tab list. Handles full replacement if skin or name changes,
   * otherwise sends standard updates.
   *
   * @param profile The TabListProfile to update.
   */
  public void updatePlayer(@NotNull TabListProfile profile) {
    // If skin or name changes, a full REMOVE + ADD is required by the client.
    // However, we'll use UPDATE_LISTED = false then ADD_PLAYER to avoid WrapperPlayServerPlayerInfoRemove.
    boolean requiresFullReplace = profile.hasSkinChanged() || profile.hasNameChanged();

    if (requiresFullReplace) {
      // Temporarily set listed to false to hide the old entry before re-adding.
      // This avoids issues with the client not updating the GameProfile correctly.
      profile.setListed(false);
      updatePlayer(profile); // Send UPDATE_LISTED = false

      // Now re-add the player with the updated profile (which will include the new name/skin)
      // The listed status will be restored to its original value in sendAdd.
      addFakePlayer(profile); // This will send ADD_PLAYER
      return;
    }

    // Otherwise, send standard updates for latency, game mode, display name, listed status, hat, and sort order.
    UserProfile userProfile = profile.buildUserProfile();

    WrapperPlayServerPlayerInfoUpdate.PlayerInfo info =
      new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
        userProfile,
        profile.isListed(),
        profile.getLatency(),
        profile.getGameMode(),
        profile.getDisplayName(),
        null,
        profile.getOrder(),
        profile.isShowHat()
      );

    EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = EnumSet.of(
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT,
      WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LIST_ORDER
    );

    WrapperPlayServerPlayerInfoUpdate update = new WrapperPlayServerPlayerInfoUpdate(actions, info);
    this.packetUser.sendPacket(update);
  }

  /**
   * Hides the entire custom tab list for this player by setting all effective profiles' 'listed' status to false.
   */
  public void hideTab() {
    this.hideTab = true;

    getEffectiveEntries().forEach(profile -> {
      profile.setListed(false);
      updatePlayer(profile);
    });
  }

  /**
   * Shows the entire custom tab list for this player by re-adding all effective profiles.
   */
  public void showTab() {
    this.hideTab = false;

    getEffectiveEntries().forEach(this::sendAdd);
  }

  /**
   * Resets the player's tab list to the vanilla Minecraft tab list behavior.
   * This involves hiding all custom profiles and then re-adding all currently online players with vanilla settings.
   */
  public void resetToMinecraftTab() {

    getEffectiveEntries().forEach(profile -> {
      profile.setListed(false);
      updatePlayer(profile);
    });

    this.localEntries.clear();

    for (Player other : Bukkit.getOnlinePlayers()) {

      UserProfile profile = PacketEvents.getAPI().getPlayerManager().getUser(other).getProfile();

      WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
        profile,
        true,
        50,
        GameMode.SURVIVAL,
        Component.text(other.getName()),
        null,
        0,
        true
      );

      WrapperPlayServerPlayerInfoUpdate update = new WrapperPlayServerPlayerInfoUpdate(EnumSet.of(
        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_HAT
      ), info);

      this.packetUser.sendPacket(update);
    }
  }

}