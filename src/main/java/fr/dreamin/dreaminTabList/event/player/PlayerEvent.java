package fr.dreamin.dreaminTabList.event.player;

import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListJoinEvent;
import fr.dreamin.dreaminTabList.event.custom.PlayerTabListLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    DreaminTabList.getInstance().callEvent(new PlayerTabListJoinEvent(player));
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    DreaminTabList.getInstance().callEvent(new PlayerTabListLeaveEvent(event.getPlayer()));
  }

}
