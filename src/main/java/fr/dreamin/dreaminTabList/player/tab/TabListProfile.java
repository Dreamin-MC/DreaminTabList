package fr.dreamin.dreaminTabList.player.tab;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import fr.dreamin.api.minecraft.MojangAPI;
import fr.dreamin.api.minecraft.SkinProperty;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
public class TabListProfile {

  private String lastName, name;
  private Component displayName;
  private UUID uuid;
  private String group;
  private int latency = 0;
  private GameMode gameMode = GameMode.SURVIVAL;
  private boolean listed = true;
  private int priority = 0;
  private int order = 0;
  private boolean showHat = true;
  private List<TextureProperty> lastSkin, skinProperties = new ArrayList<>();

  private boolean vanillaSynced = false;

  public TabListProfile(String name, Component displayName, String group) {
    this.name = name;
    this.displayName = displayName;
    this.uuid = UUID.nameUUIDFromBytes(("fake:" + name).getBytes());
    this.group = group;
  }

  public TabListProfile(String name, Component displayName) {
    this.name = name;
    this.displayName = displayName;
    this.uuid = UUID.nameUUIDFromBytes(("fake:" + name).getBytes());
  }

  public TabListProfile(Player player, String group) {
    this.name = player.getName();
    this.displayName = Component.text(player.getName());
    this.uuid = player.getUniqueId();
    this.group = group;
    this.vanillaSynced = true;

    addSkin(player.getName());
  }

  public TabListProfile(Player player) {
    this.name = player.getName();
    this.displayName = Component.text(player.getName());
    this.uuid = player.getUniqueId();
    this.vanillaSynced = true;

    addSkin(player.getName());
  }

  public UserProfile buildUserProfile() {
    UserProfile profile = new UserProfile(this.uuid, this.name);
    profile.getTextureProperties().addAll(this.skinProperties);
    return profile;
  }

  public void addSkin(String name) {
    try {
      SkinProperty skin = MojangAPI.getSkinPropertyByName(name);
      this.skinProperties.add(new TextureProperty(skin.getName(), skin.getValue(), skin.getSignature()));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void setName(String name) {
    this.lastName = this.name;
    this.name = name;
  }

  public boolean hasNameChanged() {
    return lastName == null || !lastName.equals(this.name);
  }

  public boolean hasSkinChanged() {
    if (lastSkin == null || lastSkin.size() != this.skinProperties.size()) return true;
    for (int i = 0; i < lastSkin.size(); i++) {
      TextureProperty a = lastSkin.get(i);
      TextureProperty b = this.skinProperties.get(i);
      if (!Objects.equals(a.getValue(), b.getValue()) || !Objects.equals(a.getSignature(), b.getSignature())) {
        return true;
      }
    }
    return false;
  }

  public void snapshotState() {
    this.lastName = this.name;
    this.lastSkin = new ArrayList<>(this.skinProperties);
  }
}
