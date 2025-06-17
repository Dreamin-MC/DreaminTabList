package fr.dreamin.dreaminTabList.api.player;

import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Manages TabList functionality for a specific player.
 * 
 * <p>Each player has their own PlayerTabManager that controls what they see
 * in their tab list. This allows for per-player customization of the tab
 * list content, including player-specific profiles that only appear for
 * certain players.
 * 
 * <p>The player's tab list consists of:
 * <ul>
 *   <li>Global profiles (visible to all players)</li>
 *   <li>Player-specific profiles (only visible to this player)</li>
 *   <li>Header and footer (can be player-specific)</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * PlayerTabManager manager = api.getPlayerManager(player);
 * 
 * // Hide the tab for this player
 * manager.hideTab();
 * 
 * // Add a player-specific profile
 * TabProfile personalProfile = api.getProfileManager()
 *     .createProfile()
 *     .name("PersonalBot")
 *     .displayName(Component.text("§aYour Personal Assistant"))
 *     .build();
 * manager.addProfile(personalProfile);
 * 
 * // Set custom header/footer for this player
 * manager.setHeaderAndFooter(
 *     Component.text("§6Welcome " + player.getName()),
 *     Component.text("§7Your rank: VIP")
 * );
 * }</pre>
 * 
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public interface PlayerTabManager {
    
    /**
     * Gets the player this manager is for.
     * 
     * @return the player, never null
     * @since 0.0.1
     */
    @NotNull
    Player getPlayer();
    
    /**
     * Gets the UUID of the player this manager is for.
     * 
     * @return the player's UUID, never null
     * @since 0.0.1
     */
    @NotNull
    UUID getPlayerUUID();
    
    /**
     * Adds a profile to this player's tab view.
     * 
     * <p>The profile will only be visible to this specific player.
     * If a profile with the same UUID already exists in this player's
     * view, it will be replaced.
     * 
     * <p>Note: This does not affect global profiles or other players' views.
     * 
     * @param profile the profile to add, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void addProfile(@NotNull TabProfile profile);
    
    /**
     * Removes a profile from this player's tab view by UUID.
     * 
     * <p>This only removes player-specific profiles. Global profiles
     * cannot be removed this way - use the global profile manager instead.
     * 
     * @param profileId the UUID of the profile to remove, must not be null
     * @throws IllegalArgumentException if profileId is null
     * @since 0.0.1
     */
    void removeProfile(@NotNull UUID profileId);
    
    /**
     * Removes a profile from this player's tab view.
     * 
     * <p>This is a convenience method equivalent to calling
     * {@link #removeProfile(UUID)} with the profile's UUID.
     * 
     * @param profile the profile to remove, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void removeProfile(@NotNull TabProfile profile);
    
    /**
     * Updates an existing profile in this player's tab view.
     * 
     * <p>The profile with the same UUID will be replaced with the
     * updated version. If no profile with the same UUID exists,
     * this behaves like {@link #addProfile(TabProfile)}.
     * 
     * @param profile the updated profile, must not be null
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    void updateProfile(@NotNull TabProfile profile);
    
    /**
     * Gets all profiles visible to this player.
     * 
     * <p>This includes both global profiles and player-specific profiles.
     * The returned collection is a copy and can be safely modified.
     * 
     * @return collection of visible profiles, never null but may be empty
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> getVisibleProfiles();
    
    /**
     * Gets only the player-specific profiles for this player.
     * 
     * <p>This excludes global profiles and only returns profiles that
     * were added specifically for this player.
     * 
     * @return collection of player-specific profiles, never null but may be empty
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> getPlayerSpecificProfiles();
    
    /**
     * Gets the number of profiles visible to this player.
     * 
     * @return the total number of visible profiles (global + player-specific)
     * @since 0.0.1
     */
    int getVisibleProfileCount();
    
    /**
     * Finds a visible profile by UUID.
     * 
     * <p>Searches through all profiles visible to this player
     * (both global and player-specific).
     * 
     * @param uuid the UUID to search for, must not be null
     * @return the profile with the specified UUID, or null if not found
     * @throws IllegalArgumentException if uuid is null
     * @since 0.0.1
     */
    @Nullable
    TabProfile findProfile(@NotNull UUID uuid);
    
    /**
     * Finds a visible profile by name.
     * 
     * <p>Searches through all profiles visible to this player.
     * The search is case-sensitive.
     * 
     * @param name the name to search for, must not be null
     * @return the profile with the specified name, or null if not found
     * @throws IllegalArgumentException if name is null
     * @since 0.0.1
     */
    @Nullable
    TabProfile findProfile(@NotNull String name);
    
    /**
     * Finds visible profiles matching a predicate.
     * 
     * <p>Returns all profiles visible to this player that match
     * the specified condition.
     * 
     * @param predicate the condition to match, must not be null
     * @return collection of matching profiles, never null but may be empty
     * @throws IllegalArgumentException if predicate is null
     * @since 0.0.1
     */
    @NotNull
    Collection<TabProfile> findProfiles(@NotNull Predicate<TabProfile> predicate);
    
    /**
     * Hides the tab list for this player.
     * 
     * <p>The player will not see any entries in their tab list until
     * {@link #showTab()} is called. This overrides the global tab
     * visibility setting for this specific player.
     * 
     * @since 0.0.1
     * @see #showTab()
     * @see #isTabHidden()
     */
    void hideTab();
    
    /**
     * Shows the tab list for this player.
     * 
     * <p>All visible profiles will be sent to the player. This overrides
     * the global tab visibility setting for this specific player.
     * 
     * @since 0.0.1
     * @see #hideTab()
     * @see #isTabHidden()
     */
    void showTab();
    
    /**
     * Checks if the tab list is hidden for this player.
     * 
     * <p>This returns true if the tab is hidden either globally or
     * specifically for this player.
     * 
     * @return true if the tab is hidden, false otherwise
     * @since 0.0.1
     * @see #hideTab()
     * @see #showTab()
     */
    boolean isTabHidden();
    
    /**
     * Sets header and footer for this player.
     * 
     * <p>This overrides any global header/footer settings for this
     * specific player. The header appears above the player list
     * and the footer appears below it.
     * 
     * @param header the header component, null to remove header
     * @param footer the footer component, null to remove footer
     * @since 0.0.1
     * @see #removeHeaderAndFooter()
     * @see #getHeader()
     * @see #getFooter()
     */
    void setHeaderAndFooter(@Nullable Component header, @Nullable Component footer);
    
    /**
     * Sets only the header for this player.
     * 
     * <p>The footer remains unchanged.
     * 
     * @param header the header component, null to remove header
     * @since 0.0.1
     */
    void setHeader(@Nullable Component header);
    
    /**
     * Sets only the footer for this player.
     * 
     * <p>The header remains unchanged.
     * 
     * @param footer the footer component, null to remove footer
     * @since 0.0.1
     */
    void setFooter(@Nullable Component footer);
    
    /**
     * Removes header and footer for this player.
     * 
     * <p>This is equivalent to calling {@link #setHeaderAndFooter(Component, Component)}
     * with null values for both parameters.
     * 
     * @since 0.0.1
     * @see #setHeaderAndFooter(Component, Component)
     */
    void removeHeaderAndFooter();
    
    /**
     * Gets the current header for this player.
     * 
     * @return the header component, or null if no header is set
     * @since 0.0.1
     */
    @Nullable
    Component getHeader();
    
    /**
     * Gets the current footer for this player.
     * 
     * @return the footer component, or null if no footer is set
     * @since 0.0.1
     */
    @Nullable
    Component getFooter();
    
    /**
     * Resets the tab to vanilla Minecraft behavior for this player.
     * 
     * <p>This will:
     * <ul>
     *   <li>Remove all custom profiles (both global and player-specific)</li>
     *   <li>Show all online players with their default appearance</li>
     *   <li>Remove custom header and footer</li>
     *   <li>Reset tab visibility to shown</li>
     * </ul>
     * 
     * <p>Note: This only affects this specific player. Other players
     * will continue to see their customized tab lists.
     * 
     * @since 0.0.1
     */
    void resetToVanilla();
    
    /**
     * Refreshes the tab list for this player.
     * 
     * <p>This will resend all visible profiles to the player.
     * This can be useful to fix synchronization issues or apply
     * configuration changes.
     * 
     * @since 0.0.1
     */
    void refresh();
    
    /**
     * Removes all player-specific profiles for this player.
     * 
     * <p>Global profiles will remain visible. This only clears
     * profiles that were added specifically for this player.
     * 
     * @since 0.0.1
     */
    void clearPlayerSpecificProfiles();
    
    /**
     * Checks if this player manager is valid and the player is online.
     * 
     * <p>A manager becomes invalid when the player disconnects.
     * Operations on invalid managers may throw exceptions.
     * 
     * @return true if the manager is valid and player is online, false otherwise
     * @since 0.0.1
     */
    boolean isValid();
}

