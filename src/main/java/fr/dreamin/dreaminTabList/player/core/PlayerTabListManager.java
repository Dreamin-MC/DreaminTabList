package fr.dreamin.dreaminTabList.player.core;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PlayerTabListManager {

  private final Set<PlayerTabList> playerTabListSet = new HashSet<>();

  public void addPlayer(@NotNull PlayerTabList playerTabList) {
    this.playerTabListSet.add(playerTabList);
  }

  public void removePlayer(@NotNull Player player) {
    PlayerTabList playerTabList = getPlayer(player);
    if (playerTabList != null) this.playerTabListSet.remove(playerTabList);
  }

  // #################################################################
  // ---------------------- SEARCH METHODS --------------------------
  // #################################################################

  /**
   * Get the PlayerTabList from Player
   *
   * @param player to search
   * @return the PlayerTabList
   */
  public @Nullable PlayerTabList getPlayer(Player player) {
    return this.playerTabListSet.stream().filter(playerList -> playerList.getPlayer().equals(player)).findFirst().orElse(null);
  }

  // #################################################################
  // ---------------------- FUNCTION METHODS -------------------------
  // #################################################################

  public void hideTabForAll() {
    this.playerTabListSet.forEach(playerTabList -> {
      playerTabList.getTabList().setHideTab(true);
      playerTabList.getTabList().removeAllPlayer();
    });
  }

  public void showTabForAll() {
    this.playerTabListSet.forEach(playerTabList -> {
      playerTabList.getTabList().setHideTab(false);
      playerTabList.getTabList().addAllPlayer();
    });
  }

  public void setHeaderAndFooterForAll() {
    this.playerTabListSet.forEach(playerTabList -> playerTabList.getTabList().setHeaderAndFooter());
  }

  public void removeHeaderAndFooterForAll() {
    this.playerTabListSet.forEach(playerTabList -> playerTabList.getPlayer().sendPlayerListHeaderAndFooter(Component.empty(), Component.empty()));
  }

}
