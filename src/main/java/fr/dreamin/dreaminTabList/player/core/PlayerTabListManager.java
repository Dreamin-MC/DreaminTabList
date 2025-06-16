package fr.dreamin.dreaminTabList.player.core;

import fr.dreamin.dreaminTabList.player.tab.TabList;
import fr.dreamin.dreaminTabList.player.tab.TabListCache;
import fr.dreamin.dreaminTabList.player.tab.TabListProfile;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PlayerTabListManager {

  private final Set<PlayerTabList> playerTabListSet = new HashSet<>();
  private final TabListCache globalCache = new TabListCache();

  /**
   * Add Player to the list
   *
   * @param playerTabList the player add
   */
  public void addPlayer(@NotNull PlayerTabList playerTabList) {
    this.globalCache.add(new TabListProfile(playerTabList.getPlayer()));

    this.playerTabListSet.add(playerTabList);
  }

  /**
   * Remove player to the list
   *
   * @param player the player remove
   */
  public void removePlayer(@NotNull Player player) {
    PlayerTabList playerTabList = getPlayer(player);
    this.globalCache.remove(player.getUniqueId());

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

  /**
   * Get TabList to specific player
   *
   * @param player to search
   * @return the tabList of the player
   */
  public @Nullable TabList getTabList(Player player) {
    PlayerTabList playerTabList = getPlayer(player);
    if (playerTabList == null) return null;
    return playerTabList.getTabList();
  }

  /**
   * Function to hide tab for all player
   */
  public void hideTabForAll() {
    this.playerTabListSet.forEach(playerTabList -> {
      playerTabList.getTabList().setHideTab(true);
      playerTabList.getTabList().hideTab();
    });
  }

  /**
   * Function to show tab for all player
   */
  public void showTabForAll() {
    this.playerTabListSet.forEach(playerTabList -> {
      playerTabList.getTabList().setHideTab(false);
      playerTabList.getTabList().showTab();
    });
  }

  /**
   * Function to set header and footer for all player
   */
  public void setHeaderAndFooterForAll() {
    this.playerTabListSet.forEach(playerTabList -> playerTabList.getTabList().setHeaderAndFooter());
  }

  /**
   * Function to remove header and footer for all player
   */
  public void removeHeaderAndFooterForAll() {
    this.playerTabListSet.forEach(playerTabList -> playerTabList.getPlayer().sendPlayerListHeaderAndFooter(Component.empty(), Component.empty()));
  }

}
