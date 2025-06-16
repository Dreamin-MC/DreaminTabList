package fr.dreamin.dreaminTabList.event.custom;

import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import fr.dreamin.dreaminTabList.event.core.DreaminTabListEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerTabListJoinEvent extends DreaminTabListEvent {

  private @NotNull final Player player;
  private @Nullable final PlayerTabList playerTabList;

  /**
   * Event when player join a server
   *
   * @param player join the player
   */
  public PlayerTabListJoinEvent(@NotNull Player player, PlayerTabList playerTabList) {
    super();
    this.player = player;
    this.playerTabList = playerTabList;

  }

}
