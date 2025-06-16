package fr.dreamin.dreaminTabList.impl.profile;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import fr.dreamin.api.minecraft.MojangAPI;
import fr.dreamin.api.minecraft.SkinProperty;
import fr.dreamin.dreaminTabList.api.exceptions.InvalidProfileException;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implementation of the TabProfileBuilder interface.
 *
 * <p>This builder provides a fluent interface for creating TabProfile instances
 * with comprehensive validation and sensible defaults. It supports both real
 * player profiles and fake profiles with custom properties.
 *
 * <p>The builder validates all input parameters and provides helpful error
 * messages when invalid values are provided. It also handles skin loading
 * from various sources including online players and the Mojang API.
 *
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public class TabProfileBuilderImpl implements TabProfileBuilder {

  // Minecraft username validation pattern (3-16 characters, alphanumeric and underscore)
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

  // Builder state
  private UUID uuid;
  private String name;
  private Component displayName;
  private GameMode gameMode = GameMode.SURVIVAL;
  private int latency = 0;
  private boolean listed = true;
  private int sortOrder = 0;
  private boolean showHat = true;
  private String group;
  private boolean realPlayer = false;
  private boolean vanillaSynced = false;
  private List<TextureProperty> skinProperties = new ArrayList<>();

  /**
   * Creates a new empty builder with default values.
   */
  public TabProfileBuilderImpl() {
    // Default constructor with default values
  }

  /**
   * Creates a new builder pre-populated with values from an existing profile.
   *
   * @param profile the profile to copy values from
   */
  public TabProfileBuilderImpl(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    this.uuid = profile.getUniqueId();
    this.name = profile.getName();
    this.displayName = profile.getDisplayName();
    this.gameMode = profile.getGameMode();
    this.latency = profile.getLatency();
    this.listed = profile.isListed();
    this.sortOrder = profile.getSortOrder();
    this.showHat = profile.isShowHat();
    this.group = profile.getGroup();
    this.realPlayer = profile.isRealPlayer();
    this.vanillaSynced = profile.isVanillaSynced();

    // Copy skin properties if available
    if (profile instanceof TabProfileImpl) this.skinProperties = new ArrayList<>(((TabProfileImpl) profile).getSkinProperties());
  }

  @Override
  @NotNull
  public TabProfileBuilder name(@NotNull String name) {
    if (name == null) throw new IllegalArgumentException("Name cannot be null");

    if (name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");

    if (!USERNAME_PATTERN.matcher(name).matches()) {
      throw new IllegalArgumentException("Invalid username: '" + name + "'. Must be 3-16 characters, alphanumeric and underscore only.");
    }

    this.name = name;

    // Auto-generate UUID for fake profiles if not set
    if (this.uuid == null && !this.realPlayer) this.uuid = UUID.nameUUIDFromBytes(("fake:" + name).getBytes());

    // Set default display name if not set
    if (this.displayName == null) this.displayName = Component.text(name);

    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder displayName(@NotNull Component displayName) {
    if (displayName == null) throw new IllegalArgumentException("Display name cannot be null");

    this.displayName = displayName;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder displayName(@NotNull String displayName) {
    if (displayName == null) throw new IllegalArgumentException("Display name cannot be null");

    // Parse legacy color codes
    this.displayName = LegacyComponentSerializer.legacySection().deserialize(displayName);
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder gameMode(@NotNull GameMode gameMode) {
    if (gameMode == null) throw new IllegalArgumentException("Game mode cannot be null");

    this.gameMode = gameMode;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder latency(int latency) {
    if (latency < 0) throw new IllegalArgumentException("Latency cannot be negative: " + latency);

    this.latency = latency;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder listed(boolean listed) {
    this.listed = listed;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder sortOrder(int order) {
    this.sortOrder = order;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder showHat(boolean showHat) {
    this.showHat = showHat;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder group(@Nullable String group) {
    this.group = group;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder skinFromPlayer(@NotNull Player player) {
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    if (!player.isOnline()) throw new IllegalArgumentException("Player must be online to copy skin");

    return skinFromPlayer(player.getName());
  }

  @Override
  @NotNull
  public TabProfileBuilder skinFromPlayer(@NotNull String playerName) {
    if (playerName == null) throw new IllegalArgumentException("Player name cannot be null");

    if (!USERNAME_PATTERN.matcher(playerName).matches()) throw new IllegalArgumentException("Invalid player name: " + playerName);

    try {
      SkinProperty skin = MojangAPI.getSkinPropertyByName(playerName);
      this.skinProperties.clear();
      this.skinProperties.add(new TextureProperty(skin.getName(), skin.getValue(), skin.getSignature()));
    } catch (Exception e) {
      throw new InvalidProfileException("Failed to fetch skin for player '" + playerName + "'", e);
    }

    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder customSkin(@NotNull String texture, @NotNull String signature) {
    if (texture == null) throw new IllegalArgumentException("Texture cannot be null");

    if (signature == null) throw new IllegalArgumentException("Signature cannot be null");

    if (texture.trim().isEmpty()) throw new IllegalArgumentException("Texture cannot be empty");

    if (signature.trim().isEmpty()) throw new IllegalArgumentException("Signature cannot be empty");

    this.skinProperties.clear();
    this.skinProperties.add(new TextureProperty("textures", texture, signature));
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder defaultSkin() {
    this.skinProperties.clear();
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder uuid(@NotNull UUID uuid) {
    if (uuid == null) throw new IllegalArgumentException("UUID cannot be null");

    this.uuid = uuid;
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder realPlayer(boolean realPlayer) {
    this.realPlayer = realPlayer;
    this.vanillaSynced = realPlayer; // Real players are typically vanilla synced
    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder copyFrom(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    this.uuid = profile.getUniqueId();
    this.name = profile.getName();
    this.displayName = profile.getDisplayName();
    this.gameMode = profile.getGameMode();
    this.latency = profile.getLatency();
    this.listed = profile.isListed();
    this.sortOrder = profile.getSortOrder();
    this.showHat = profile.isShowHat();
    this.group = profile.getGroup();
    this.realPlayer = profile.isRealPlayer();
    this.vanillaSynced = profile.isVanillaSynced();

    // Copy skin properties if available
    if (profile instanceof TabProfileImpl) this.skinProperties = new ArrayList<>(((TabProfileImpl) profile).getSkinProperties());

    return this;
  }

  @Override
  @NotNull
  public TabProfileBuilder reset() {
    this.uuid = null;
    this.name = null;
    this.displayName = null;
    this.gameMode = GameMode.SURVIVAL;
    this.latency = 0;
    this.listed = true;
    this.sortOrder = 0;
    this.showHat = true;
    this.group = null;
    this.realPlayer = false;
    this.vanillaSynced = false;
    this.skinProperties.clear();
    return this;
  }

  @Override
  @NotNull
  public TabProfile build() {
    // Validate required fields
    if (name == null || name.trim().isEmpty()) throw new IllegalStateException("Name is required and cannot be null or empty");

    // Ensure UUID is set
    if (uuid == null) {
      if (realPlayer) throw new IllegalStateException("UUID must be explicitly set for real player profiles");

      // Generate UUID for fake profiles
      else uuid = UUID.nameUUIDFromBytes(("fake:" + name).getBytes());
    }

    // Ensure display name is set
    if (displayName == null) displayName = Component.text(name);

    // Additional validation
    if (latency < 0) throw new InvalidProfileException("Latency cannot be negative: " + latency);

    return new TabProfileImpl(this);
  }

  // Package-private getters for TabProfileImpl constructor
  UUID getUuid() { return uuid; }
  String getName() { return name; }
  Component getDisplayName() { return displayName; }
  GameMode getGameMode() { return gameMode; }
  int getLatency() { return latency; }
  boolean isListed() { return listed; }
  int getSortOrder() { return sortOrder; }
  boolean isShowHat() { return showHat; }
  String getGroup() { return group; }
  boolean isRealPlayer() { return realPlayer; }
  boolean isVanillaSynced() { return vanillaSynced; }
  List<TextureProperty> getSkinProperties() { return skinProperties; }
}

