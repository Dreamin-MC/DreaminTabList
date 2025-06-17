package fr.dreamin.dreaminTabList.impl.profile;

import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileBuilder;
import fr.dreamin.dreaminTabList.api.profile.TabProfileManager;
import fr.dreamin.dreaminTabList.impl.TabListAPIImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the TabProfileManager interface.
 *
 * <p>This manager handles global profiles that are visible to all players.
 * It provides thread-safe operations for adding, removing, and updating
 * profiles, as well as efficient searching and filtering capabilities.
 *
 * <p>The manager automatically handles profile synchronization across all
 * online players when profiles are modified. It also provides change
 * tracking to optimize packet sending.
 *
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public class TabProfileManagerImpl implements TabProfileManager {

  private final TabListAPIImpl api;
  private final Map<UUID, TabProfile> globalProfiles = new ConcurrentHashMap<>();
  private final Map<String, UUID> nameToUuidMap = new ConcurrentHashMap<>();

  /**
   * Creates a new TabProfileManager implementation.
   *
   * @param api the main API instance
   */
  public TabProfileManagerImpl(@NotNull TabListAPIImpl api) {
    this.api = Objects.requireNonNull(api, "API cannot be null");
  }

  @Override
  @NotNull
  public TabProfileBuilder createProfile() {
    return new TabProfileBuilderImpl();
  }

  @Override
  @NotNull
  public TabProfile createProfileFromPlayer(@NotNull Player player) {
    if (player == null) throw new IllegalArgumentException("Player cannot be null");

    return new TabProfileImpl(player);
  }

  @Override
  public void addGlobalProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    UUID uuid = profile.getUniqueId();
    String name = profile.getName();

    // Remove old name mapping if profile already exists
    TabProfile existing = globalProfiles.get(uuid);
    if (existing != null) nameToUuidMap.remove(existing.getName());

    // Add new profile
    globalProfiles.put(uuid, profile);
    nameToUuidMap.put(name, uuid);

    // Send to all players
    api.sendProfileToAllPlayers(profile);

    // Log the addition
    api.getLogger().info("Added global profile: " + name + " (" + uuid + ")");
  }

  @Override
  public void removeGlobalProfile(@NotNull UUID profileId) {
    if (profileId == null) throw new IllegalArgumentException("Profile ID cannot be null");

    TabProfile profile = globalProfiles.remove(profileId);
    if (profile != null) {
      nameToUuidMap.remove(profile.getName());

      // Remove from all players
      api.removeProfileFromAllPlayers(profileId);

      // Log the removal
      api.getLogger().info("Removed global profile: " + profile.getName() + " (" + profileId + ")");
    }
  }

  @Override
  public void removeGlobalProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    removeGlobalProfile(profile.getUniqueId());
  }

  @Override
  public void updateGlobalProfile(@NotNull TabProfile profile) {
    if (profile == null) throw new IllegalArgumentException("Profile cannot be null");

    UUID uuid = profile.getUniqueId();
    TabProfile existing = globalProfiles.get(uuid);

    if (existing != null) {
      // Update name mapping if name changed
      if (!existing.getName().equals(profile.getName())) {
        nameToUuidMap.remove(existing.getName());
        nameToUuidMap.put(profile.getName(), uuid);
      }

    }
    // New profile
    else nameToUuidMap.put(profile.getName(), uuid);

    globalProfiles.put(uuid, profile);

    // Update for all players
    api.updateProfileForAllPlayers(profile);

    // Log the update
    api.getLogger().info("Updated global profile: " + profile.getName() + " (" + uuid + ")");
  }

  @Override
  @NotNull
  public Collection<TabProfile> getGlobalProfiles() {
    return new ArrayList<>(globalProfiles.values());
  }

  @Override
  public int getGlobalProfileCount() {
    return globalProfiles.size();
  }

  @Override
  @Nullable
  public TabProfile findProfile(@NotNull UUID uuid) {
    if (uuid == null) throw new IllegalArgumentException("UUID cannot be null");

    return globalProfiles.get(uuid);
  }

  @Override @Nullable
  public TabProfile findProfile(@NotNull String name) {
    if (name == null) throw new IllegalArgumentException("Name cannot be null");

    UUID uuid = nameToUuidMap.get(name);
    return uuid != null ? globalProfiles.get(uuid) : null;
  }

  @Override @NotNull
  public Collection<TabProfile> findProfilesByGroup(@NotNull String group) {
    if (group == null) throw new IllegalArgumentException("Group cannot be null");

    return globalProfiles.values().stream()
      .filter(profile -> Objects.equals(profile.getGroup(), group))
      .collect(Collectors.toList());
  }

  @Override @NotNull
  public Collection<TabProfile> findProfiles(@NotNull Predicate<TabProfile> predicate) {
    if (predicate == null) throw new IllegalArgumentException("Predicate cannot be null");

    return globalProfiles.values().stream()
      .filter(predicate)
      .collect(Collectors.toList());
  }

  @Override
  public boolean hasProfile(@NotNull UUID uuid) {
    if (uuid == null) throw new IllegalArgumentException("UUID cannot be null");

    return globalProfiles.containsKey(uuid);
  }

  @Override
  public boolean hasProfile(@NotNull String name) {
    if (name == null) throw new IllegalArgumentException("Name cannot be null");

    return nameToUuidMap.containsKey(name);
  }

  @Override
  public void clearGlobalProfiles() {
    // Get all UUIDs before clearing
    Set<UUID> uuids = new HashSet<>(globalProfiles.keySet());

    // Clear internal storage
    globalProfiles.clear();
    nameToUuidMap.clear();

    // Remove from all players
    api.removeProfilesFromAllPlayers(uuids);

    // Log the operation
    api.getLogger().info("Cleared all global profiles (" + uuids.size() + " profiles)");
  }

  @Override
  public void clearProfilesByGroup(@NotNull String group) {
    if (group == null) throw new IllegalArgumentException("Group cannot be null");

    // Find profiles in the group
    List<TabProfile> profilesToRemove = globalProfiles.values().stream()
      .filter(profile -> Objects.equals(profile.getGroup(), group))
      .collect(Collectors.toList());

    // Remove each profile
    for (TabProfile profile : profilesToRemove) {
      removeGlobalProfile(profile.getUniqueId());
    }

    // Log the operation
    api.getLogger().info("Cleared " + profilesToRemove.size() + " profiles from group: " + group);
  }

  @Override
  public void refreshGlobalProfiles() {
    // Resend all global profiles to all players
    for (TabProfile profile : globalProfiles.values()) {
      api.sendProfileToAllPlayers(profile);
    }

    // Log the operation
    api.getLogger().info("Refreshed " + globalProfiles.size() + " global profiles");
  }

  /**
   * Gets all global profiles as a map for internal use.
   *
   * @return the internal profiles map (read-only view)
   */
  public Map<UUID, TabProfile> getGlobalProfilesMap() {
    return Collections.unmodifiableMap(globalProfiles);
  }

  /**
   * Initializes the manager with existing profiles.
   *
   * <p>This method is used during API initialization to restore
   * profiles from the existing TabList system.
   *
   * @param profiles the profiles to initialize with
   */
  public void initializeProfiles(@NotNull Collection<TabProfile> profiles) {
    if (profiles == null) throw new IllegalArgumentException("Profiles cannot be null");

    globalProfiles.clear();
    nameToUuidMap.clear();

    for (TabProfile profile : profiles) {
      globalProfiles.put(profile.getUniqueId(), profile);
      nameToUuidMap.put(profile.getName(), profile.getUniqueId());
    }

    api.getLogger().info("Initialized profile manager with " + profiles.size() + " profiles");
  }
}

