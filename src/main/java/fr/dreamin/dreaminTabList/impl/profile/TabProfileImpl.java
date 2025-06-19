package fr.dreamin.dreaminTabList.impl.profile;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import fr.dreamin.api.minecraft.MojangAPI;
import fr.dreamin.api.minecraft.SkinProperty;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of the TabProfile interface.
 *
 * <p>This class represents an immutable profile that can be displayed in
 * a player's tab list. It contains all the necessary information for
 * rendering the profile including name, display name, game mode, latency,
 * skin data, and other properties.
 *
 * <p>Instances of this class are created using the {@link TabProfileBuilderImpl}
 * and are immutable once created. To modify a profile, use the {@link #toBuilder()}
 * method to create a new builder with the current values.
 *
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
public class TabProfileImpl implements TabProfile {

  private final UUID uuid;
  private final String name;
  private final Component displayName;
  private final GameMode gameMode;
  private final int latency;
  private final boolean listed;
  private final int sortOrder;
  private final boolean showHat;
  private final String group;
  private final boolean realPlayer;
  private final boolean vanillaSynced;
  private final List<TextureProperty> skinProperties;

  // Internal fields for change tracking
  private String lastName;
  private List<TextureProperty> lastSkin;

  /**
   * Creates a new TabProfile implementation.
   *
   * <p>This constructor is package-private and should only be called
   * by the TabProfileBuilderImpl.
   *
   * @param builder the builder containing the profile data
   */
  TabProfileImpl(TabProfileBuilderImpl builder) {
    this.uuid = builder.getUuid();
    this.name = builder.getName();
    this.displayName = builder.getDisplayName();
    this.gameMode = builder.getGameMode();
    this.latency = builder.getLatency();
    this.listed = builder.isListed();
    this.sortOrder = builder.getSortOrder();
    this.showHat = builder.isShowHat();
    this.group = builder.getGroup();
    this.realPlayer = builder.isRealPlayer();
    this.vanillaSynced = builder.isVanillaSynced();
    this.skinProperties = new ArrayList<>(builder.getSkinProperties());

    // Initialize change tracking
    this.lastName = this.name;
    this.lastSkin = new ArrayList<>(this.skinProperties);
  }

  /**
   * Creates a TabProfile from a real player.
   *
   * @param player the player to create the profile from
   * @param group the group to assign to the profile, may be null
   */
  public TabProfileImpl(@NotNull Player player, @Nullable String group) {
    this.uuid = player.getUniqueId();
    this.name = player.getName();
    this.displayName = Component.text(player.getName());
    this.gameMode = GameMode.valueOf(player.getGameMode().name());
    this.latency = player.getPing();
    this.listed = true;
    this.sortOrder = 0;
    this.showHat = true;
    this.group = group;
    this.realPlayer = true;
    this.vanillaSynced = true;
    this.skinProperties = new ArrayList<>();

    // Load skin data
    loadSkinFromPlayer(player.getName());

    // Initialize change tracking
    this.lastName = this.name;
    this.lastSkin = new ArrayList<>(this.skinProperties);
  }

  /**
   * Creates a TabProfile from a real player.
   *
   * @param player the player to create the profile from
   */
  public TabProfileImpl(@NotNull Player player) {
    this(player, null);
  }

  @Override
  @NotNull
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  @NotNull
  public String getName() {
    return name;
  }

  @Override
  @NotNull
  public Component getDisplayName() {
    return displayName;
  }

  @Override
  @NotNull
  public GameMode getGameMode() {
    return gameMode;
  }

  @Override
  public int getLatency() {
    return latency;
  }

  @Override
  public boolean isListed() {
    return listed;
  }

  @Override
  public int getSortOrder() {
    return sortOrder;
  }

  @Override
  public boolean isShowHat() {
    return showHat;
  }

  @Override
  @Nullable
  public String getGroup() {
    return group;
  }

  @Override
  public boolean isRealPlayer() {
    return realPlayer;
  }

  @Override
  public boolean isVanillaSynced() {
    return vanillaSynced;
  }

  @Override
  @Nullable
  public String getSkinTexture() {
    if (skinProperties.isEmpty()) return null;
    return skinProperties.get(0).getValue();
  }

  @Override
  @Nullable
  public String getSkinSignature() {
    if (skinProperties.isEmpty()) return null;
    return skinProperties.get(0).getSignature();
  }

  @Override
  @NotNull
  public TabProfileBuilder toBuilder() {
    return new TabProfileBuilderImpl(this);
  }

  /**
   * Builds a UserProfile for packet sending.
   *
   * <p>This method creates a PacketEvents UserProfile that can be used
   * in tab list packets. It includes the UUID, name, and skin properties.
   *
   * @return the UserProfile for packet operations
   */
  public UserProfile buildUserProfile() {
    UserProfile profile = new UserProfile(this.uuid, this.name);
    profile.getTextureProperties().addAll(this.skinProperties);
    return profile;
  }

  /**
   * Checks if the name has changed since the last snapshot.
   *
   * @return true if the name has changed, false otherwise
   */
  public boolean hasNameChanged() {
    return lastName == null || !lastName.equals(this.name);
  }

  /**
   * Checks if the skin has changed since the last snapshot.
   *
   * @return true if the skin has changed, false otherwise
   */
  public boolean hasSkinChanged() {
    if (lastSkin == null || lastSkin.size() != this.skinProperties.size()) return true;

    for (int i = 0; i < lastSkin.size(); i++) {
      TextureProperty a = lastSkin.get(i);
      TextureProperty b = this.skinProperties.get(i);
      if (!Objects.equals(a.getValue(), b.getValue()) || !Objects.equals(a.getSignature(), b.getSignature())) return true;
    }
    return false;
  }

  /**
   * Takes a snapshot of the current state for change tracking.
   *
   * <p>This method should be called after the profile has been
   * successfully sent to players to update the change tracking state.
   */
  public void snapshotState() {
    this.lastName = this.name;
    this.lastSkin = new ArrayList<>(this.skinProperties);
  }

  /**
   * Gets the skin properties for this profile.
   *
   * @return a copy of the skin properties list
   */
  public List<TextureProperty> getSkinProperties() {
    return new ArrayList<>(skinProperties);
  }

  /**
   * Loads skin data from a player name using the Mojang API.
   *
   * @param playerName the player name to load skin from
   */
  private void loadSkinFromPlayer(String playerName) {
    try {
      SkinProperty skin = MojangAPI.getSkinPropertyByName(playerName);
      this.skinProperties.add(new TextureProperty(skin.getName(), skin.getValue(), skin.getSignature()));
    } catch (Exception e) {
      // Log the error but don't throw - use default skin instead
      System.err.println("Failed to load skin for player " + playerName + ": " + e.getMessage());
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    TabProfileImpl that = (TabProfileImpl) obj;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "TabProfileImpl{" +
      "uuid=" + uuid +
      ", name='" + name + '\'' +
      ", gameMode=" + gameMode +
      ", latency=" + latency +
      ", listed=" + listed +
      ", sortOrder=" + sortOrder +
      ", group='" + group + '\'' +
      ", realPlayer=" + realPlayer +
      '}';
  }
}

