package fr.dreamin.dreaminTabList.player.tab;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class TabListProfile {

  private String name;
  private Component displayName;
  private UUID uuid;
  private String group;
  private int latency = 0;
  private GameMode gameMode = GameMode.SURVIVAL;
  private boolean listed = true;
  private int priority = 0;
  private int order = 0;
  private boolean showHat = true;
  private List<TextureProperty> skinProperties = new ArrayList<>();

  public TabListProfile(String name, Component displayName, String group) {
    this.name = name;
    this.displayName = displayName;
    this.uuid = UUID.randomUUID();
    this.group = group;
  }

  public TabListProfile(String name, Component displayName) {
    this.name = name;
    this.displayName = displayName;
    this.uuid = UUID.randomUUID();
  }

  public TabListProfile(Player player, String group) {
    this.name = player.getName();
    this.displayName = Component.text(player.getName());
    this.uuid = UUID.randomUUID();
    this.group = group;
  }

  public TabListProfile(Player player) {
    this.name = player.getName();
    this.displayName = Component.text(player.getName());
    this.uuid = UUID.randomUUID();
  }



}
