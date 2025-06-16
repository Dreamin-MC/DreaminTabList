package fr.dreamin.dreaminTabList.player.core;

import fr.dreamin.dreaminTabList.player.tab.TabList;
import fr.dreamin.dreaminTabList.player.tab.TabListProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter @Setter
public class PlayerTabList {

  private final Player player;
  private final TabList tabList;
  private final TabListProfile tabListProfile;

  public PlayerTabList(Player player) {
    this.player = player;
    this.tabList = new TabList(this);
    this.tabListProfile = new TabListProfile(player);
  }

}
