package fr.dreamin.dreaminTabList.player.tab;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class TabList {

  private PlayerTabList playerTabList;

  private boolean hideTab = DreaminTabList.getCodex().isHideTab();

  public TabList(PlayerTabList playerTabList) {
    this.playerTabList = playerTabList;
    if (this.hideTab) removeAllPlayer();
    if (DreaminTabList.getCodex().isHeaderFooterEnabled()) Bukkit.getScheduler().runTaskLater(DreaminTabList.getInstance(), this::setHeaderAndFooter, 20L);
  }

  public void setHeaderAndFooter() {
    this.playerTabList.getPlayer().sendPlayerListHeaderAndFooter(DreaminTabList.getCodex().getHeaders(), DreaminTabList.getCodex().getFooters());
  }

  public void removeAllPlayer() {
    DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().forEach(pTabList -> removePlayer(pTabList.getPlayer()));
  }

  public void removePlayer(Player player) {
    UserProfile profile = PacketEvents.getAPI().getPlayerManager().getUser(player).getProfile();
    if (profile == null) return;
    WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(profile.getUUID());
    PacketEvents.getAPI().getPlayerManager().sendPacket(this.playerTabList.getPlayer(), packet);
  }

  public void addAllPlayer() {
    DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().forEach(pTabList -> addPlayer(pTabList.getPlayer()));
  }

  public void addPlayer(Player player) {
    UserProfile profile = PacketEvents.getAPI().getPlayerManager().getUser(player).getProfile();
    WrapperPlayServerPlayerInfoUpdate.PlayerInfo info = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile);

    WrapperPlayServerPlayerInfoUpdate update = new WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, info);
    PacketEvents.getAPI().getPlayerManager().sendPacket(this.playerTabList.getPlayer(), update);
  }


}
