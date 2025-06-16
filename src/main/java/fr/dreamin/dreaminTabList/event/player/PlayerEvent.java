package fr.dreamin.dreaminTabList.event.player;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListJoinEvent;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListLeaveEvent;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabList playerTabList = new PlayerTabList(player);
    DreaminTabList.getPlayerTabListManager().addPlayer(playerTabList);

    PlayerTabListJoinEvent playerTabListJoinEvent = new PlayerTabListJoinEvent(player, playerTabList);
    DreaminTabList.getInstance().callEvent(playerTabListJoinEvent);

    if (DreaminTabList.getCodex().isHidePlayerJoin()) {
      WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(event.getPlayer().getUniqueId());
      DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().forEach(pTab -> pTab.getTabList().getPacketUser().sendPacket(packet));
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    PlayerTabListLeaveEvent playerTabListJoinEvent = new PlayerTabListLeaveEvent(event.getPlayer());
    DreaminTabList.getInstance().callEvent(playerTabListJoinEvent);

    DreaminTabList.getPlayerTabListManager().removePlayer(event.getPlayer());
  }

}
