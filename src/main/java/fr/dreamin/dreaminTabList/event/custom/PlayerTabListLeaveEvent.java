package fr.dreamin.dreaminTabList.event.custom;

import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.event.core.DreaminTabListEvent;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerTabListLeaveEvent extends DreaminTabListEvent {

  private @NotNull final Player player;
  private @Nullable final PlayerTabList playerTabList;

  /**
   * Event when player leave a server
   *
   * @param player leave the server
   */
  public PlayerTabListLeaveEvent(@NotNull Player player) {
    super();
    this.player = player;
    this.playerTabList = DreaminTabList.getPlayerTabListManager().getPlayer(player);
  }

}
