package fr.dreamin.dreaminTabList.api.profile;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Manages global TabList profiles that are visible to all players.
 * 
 * <p>The profile manager is responsible for creating, storing, and managing
 * profiles that appear in all players' tab lists. These are called "global"
 * profiles because they are shared across all players, as opposed to
 * player-specific profiles that only appear in individual players' tabs.
 * 
 * <p>Global profiles are useful for:
 * <ul>
 *   <li>Server staff or VIP players that should always be visible</li>
 *   <li>Fake players representing server information</li>
 *   <li>Placeholder entries for offline players</li>
 *   <li>Custom entries for server features</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * TabProfileManager manager = api.getProfileManager();
 * 
 * // Create a fake staff member
 * TabProfile staff = manager.createProfile()
 *     .name("ServerBot")
 *     .displayName(Component.text("§c[BOT] §fServer"))
 *     .gameMode(GameMode.CREATIVE)
 *     .latency(0)
 *     .sortOrder(-100)  // Appears at top
 *     .build();
 * 
 * manager.addGlobalProfile(staff);
 * 
 * // Create profile from real player
 * Player player = Bukkit.getPlayer("PlayerName");
 * TabProfile playerProfile = manager.createProfileFromPlayer(player);
 * manager.addGlobalProfile(playerProfile);
 * }</pre>
 * 
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
public interface TabProfileManager {
    
    /**
     * Creates a new profile builder.
     * 
     * <p>The builder allows you to configure all aspects of a profile
     * before creating the immutable TabProfile instance.
     * 
     * @return a new profile builder, never null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder createProfile();
    
    /**
     * Creates a profile from a real player.
     * 
     * <p>This creates a profile that represents the specified player,
     * copying their current name, UUID, game mode, and skin. The
     * profile will be marked as representing a real player.
     * 
     * <p>Note: The created profile is a snapshot of the player's
     * current state. It will not automatically update when the
     * player's state changes unless explicitly updated.
     * 
     * @param player the player to create a profile from, must not be null
     * @return the created profile, never null
     * @throws IllegalArgumentException if player is null
     * @since 0.0.1
     */
    @NotNull
    TabProfile createProfileFromPlayer(@NotNull Player player);
    
    /**
     * Adds a global profile visible to all players.
     * 
     * <p>The profile will be sent to all currently online players
     * and will automatically be sent to players who join later.
     * 
     * <p>If a profile with the same UUID already exists, it will
     * be replaced with the new profile.
     * 
     * @param profile the profile to add, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void addGlobalProfile(@NotNull TabProfile profile);
    
    /**
     * Removes a global profile by UUID.
     * 
     * <p>The profile will be removed from all players' tab lists
     * immediately. If no profile with the specified UUID exists,
     * this method does nothing.
     * 
     * @param profileId the UUID of the profile to remove, must not be null
     * @throws IllegalArgumentException if profileId is null
     * @since 0.0.1
     */
    void removeGlobalProfile(@NotNull UUID profileId);
    
    /**
     * Removes a global profile.
     * 
     * <p>This is a convenience method equivalent to calling
     * {@link #removeGlobalProfile(UUID)} with the profile's UUID.
     * 
     * @param profile the profile to remove, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void removeGlobalProfile(@NotNull TabProfile profile);
    
    /**
     * Updates a global profile.
     * 
     * <p>The profile with the same UUID will be replaced with the
     * updated version. All players will receive the updated profile
     * information immediately.
     * 
     * <p>If no profile with the same UUID exists, this behaves
     * like {@link #addGlobalProfile(TabProfile)}.
     * 
     * @param profile the updated profile, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void updateGlobalProfile(@NotNull TabProfile profile);
    
    /**
     * Gets all global profiles.
     * 
     * <p>Returns a copy of the current global profiles collection.
     * Modifying the returned collection will not affect the actual
     * global profiles.
     * 
     * @return collection of global profiles, never null but may be empty
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> getGlobalProfiles();
    
    /**
     * Gets the number of global profiles.
     * 
     * @return the number of global profiles
     * @since 0.0.1
     */
    int getGlobalProfileCount();
    
    /**
     * Finds a profile by UUID.
     * 
     * <p>Searches through all global profiles for one with the
     * specified UUID.
     * 
     * @param uuid the UUID to search for, must not be null
     * @return the profile with the specified UUID, or null if not found
     * @throws IllegalArgumentException if uuid is null
     * @since 0.0.1
     */
    @Nullable
    TabProfile findProfile(@NotNull UUID uuid);
    
    /**
     * Finds a profile by name.
     * 
     * <p>Searches through all global profiles for one with the
     * specified name. The search is case-sensitive.
     * 
     * @param name the name to search for, must not be null
     * @return the profile with the specified name, or null if not found
     * @throws IllegalArgumentException if name is null
     * @since 0.0.1
     */
    @Nullable
    TabProfile findProfile(@NotNull String name);
    
    /**
     * Finds profiles by group.
     * 
     * <p>Returns all global profiles that belong to the specified group.
     * 
     * @param group the group name to search for, must not be null
     * @return collection of profiles in the specified group, never null but may be empty
     * @throws IllegalArgumentException if group is null
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> findProfilesByGroup(@NotNull String group);
    
    /**
     * Finds profiles matching a predicate.
     * 
     * <p>Returns all global profiles that match the specified condition.
     * 
     * <p>Example:
     * <pre>{@code
     * // Find all creative mode profiles
     * Collection<TabProfile> creativeProfiles = manager.findProfiles(
     *     profile -> profile.getGameMode() == GameMode.CREATIVE
     * );
     * 
     * // Find all profiles with high latency
     * Collection<TabProfile> laggyProfiles = manager.findProfiles(
     *     profile -> profile.getLatency() > 500
     * );
     * }</pre>
     * 
     * @param predicate the condition to match, must not be null
     * @return collection of matching profiles, never null but may be empty
     * @throws IllegalArgumentException if predicate is null
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> findProfiles(@NotNull Predicate<TabProfile> predicate);
    
    /**
     * Checks if a profile with the specified UUID exists.
     * 
     * @param uuid the UUID to check for, must not be null
     * @return true if a profile with the UUID exists, false otherwise
     * @throws IllegalArgumentException if uuid is null
     * @since 0.0.1
     */
    boolean hasProfile(@NotNull UUID uuid);
    
    /**
     * Checks if a profile with the specified name exists.
     * 
     * @param name the name to check for, must not be null
     * @return true if a profile with the name exists, false otherwise
     * @throws IllegalArgumentException if name is null
     * @since 0.0.1
     */
    boolean hasProfile(@NotNull String name);
    
    /**
     * Removes all global profiles.
     * 
     * <p>This will clear all global profiles from all players' tab lists.
     * Use with caution as this operation cannot be undone.
     * 
     * @since 0.0.1
     */
    void clearGlobalProfiles();
    
    /**
     * Removes all global profiles in the specified group.
     * 
     * @param group the group name, must not be null
     * @throws IllegalArgumentException if group is null
     * @since 0.0.1
     */
    void clearProfilesByGroup(@NotNull String group);
    
    /**
     * Refreshes all global profiles.
     * 
     * <p>This will resend all global profiles to all online players.
     * This can be useful after configuration changes or to fix
     * synchronization issues.
     * 
     * @since 0.0.1
     */
    void refreshGlobalProfiles();
}

