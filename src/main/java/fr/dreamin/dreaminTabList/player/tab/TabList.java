package fr.dreamin.dreaminTabList.player.tab;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
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

@Getter @Setter
public class TabList {

  private PlayerTabList playerTabList;
  private User packetUser;
  private TabListCache globalCache;

  private Map<UUID, TabListProfile> localEntries = new HashMap<>();

  private boolean hideTab = DreaminTabList.getCodex().isHideTab();

  public TabList(PlayerTabList playerTabList) {
    this.playerTabList = playerTabList;
    this.packetUser = PacketEvents.getAPI().getPlayerManager().getUser(playerTabList.getPlayer());
    this.globalCache = DreaminTabList.getPlayerTabListManager().getGlobalCache();

    if (this.hideTab) hideTab();
    if (DreaminTabList.getCodex().isHeaderFooterEnabled()) Bukkit.getScheduler().runTaskLater(DreaminTabList.getInstance(), this::setHeaderAndFooter, 20L);
  }

  public Collection<TabListProfile> getEffectiveEntries() {
    Map<UUID, TabListProfile> result = new HashMap<>(globalCache.getAll());
    result.putAll(localEntries);
    return result.values();
  }

  public void setHeaderAndFooter() {
    this.playerTabList.getPlayer().sendPlayerListHeaderAndFooter(DreaminTabList.getCodex().getHeaders(), DreaminTabList.getCodex().getFooters());
  }

  public void removePlayer(UUID uuid) {
    this.localEntries.remove(uuid);

    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(uuid);
     this.packetUser.sendPacket(packet);
  }

  public void addPlayer(Player player) {
    TabListProfile profile = new TabListProfile(player);
    this.localEntries.put(profile.getUuid(), profile);
    sendAdd(profile);
  }

  public void addFakePlayer(TabListProfile profile) {
    this.localEntries.put(profile.getUuid(), profile);
    sendAdd(profile);
  }

  public void sendAdd(TabListProfile profile) {
    UserProfile userProfile = profile.buildUserProfile(); // Doit contenir UUID, name, skinProperties

    this.playerTabList.getPlayer().sendMessage(userProfile.getName());

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

  public void updatePlayer(@NotNull TabListProfile profile) {
    // Si changement de skin ou de nom → REMOVE + ADD obligatoire
    boolean requiresFullReplace = profile.hasSkinChanged() || profile.hasNameChanged();

    if (requiresFullReplace) {
      removePlayer(profile.getUuid());
      addFakePlayer(profile); // fera ADD_PLAYER
      return;
    }

    // Sinon, envoie les updates standards
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

  public void hideTab() {
    this.hideTab = true;

    List<UUID> toRemove = getEffectiveEntries().stream().map(TabListProfile::getUuid).toList();

    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(toRemove);
    this.packetUser.sendPacket(packet);
  }

  public void showTab() {
    this.hideTab = false;

    getEffectiveEntries().forEach(this::sendAdd);
  }

  public void resetToMinecraftTab() {

    List<UUID> toRemove = getEffectiveEntries().stream().map(TabListProfile::getUuid).toList();

    WrapperPlayServerPlayerInfoRemove remove = new WrapperPlayServerPlayerInfoRemove(toRemove);
    this.packetUser.sendPacket(remove);

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
