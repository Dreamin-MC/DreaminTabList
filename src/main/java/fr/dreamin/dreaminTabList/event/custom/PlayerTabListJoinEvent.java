package fr.dreamin.dreaminTabList.event.custom;

import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import fr.dreamin.dreaminTabList.event.core.DreaminTabListEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerTabListJoinEvent extends DreaminTabListEvent {

  private @NotNull final Player player;
  private @NotNull final PlayerTabList playerTabList;

  /**
   * Event when player join a server
   *
   * @param player join the player
   * @param playerTabList the PlayerTabList of the player
   */
  public PlayerTabListJoinEvent(@NotNull Player player, @NotNull PlayerTabList playerTabList) {
    super();
    this.player = player;
    this.playerTabList = playerTabList;
  }

}
