package fr.dreamin.dreaminTabList.player.core;

import fr.dreamin.dreaminTabList.player.tab.TabList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter @Setter
public class PlayerTabList {

  private final Player player;
  private TabList tabList;

  public PlayerTabList(Player player) {
    this.player = player;
    this.tabList = new TabList(this);
  }

}
