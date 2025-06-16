package fr.dreamin.dreaminTabList.api;

import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.api.profile.TabProfileManager;
import fr.dreamin.dreaminTabList.api.exceptions.PlayerNotFoundException;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main API interface for DreaminTabList functionality.
 * 
 * <p>This interface provides access to all TabList management features including:
 * <ul>
 *   <li>Global profile management</li>
 *   <li>Player-specific tab management</li>
 *   <li>Tab visibility control</li>
 *   <li>Header and footer management</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * TabListAPI api = TabListAPIFactory.getAPI();
 * 
 * // Hide tab for all players
 * api.hideTabForAll();
 * 
 * // Set header and footer
 * api.setHeaderAndFooterForAll(
 *     Component.text("§6Welcome to the Server"),
 *     Component.text("§7Have fun!")
 * );
 * 
 * // Get player-specific manager
 * PlayerTabManager playerManager = api.getPlayerManager(player);
 * playerManager.hideTab();
 * }</pre>
 * 
 * @author DreaminTabList API
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TabListAPI {
    
    /**
     * Gets the global profile manager for managing tab profiles.
     * 
     * <p>The profile manager allows you to create, modify, and manage
     * profiles that can be displayed in players' tab lists. These profiles
     * can represent real players or fake entries.
     * 
     * @return the global profile manager, never null
     * @since 1.0.0
     */
    @NotNull
    TabProfileManager getProfileManager();
    
    /**
     * Gets the player-specific tab manager for a player.
     * 
     * <p>Each player has their own tab manager that controls what they see
     * in their tab list. This allows for per-player customization of the
     * tab list content.
     * 
     * @param player the player to get the manager for, must not be null
     * @return the player's tab manager, never null
     * @throws PlayerNotFoundException if the player is not found in the system
     * @throws IllegalArgumentException if player is null
     * @since 1.0.0
     */
    @NotNull
    PlayerTabManager getPlayerManager(@NotNull Player player);
    
    /**
     * Hides the tab list for all online players.
     * 
     * <p>This will send packets to all players to hide their tab list.
     * Players will not see any entries in their tab list until
     * {@link #showTabForAll()} is called.
     * 
     * <p>Note: This operation is performed asynchronously to avoid
     * blocking the main thread when dealing with many players.
     * 
     * @since 1.0.0
     * @see #showTabForAll()
     * @see #isTabHiddenForAll()
     */
    void hideTabForAll();
    
    /**
     * Shows the tab list for all online players.
     * 
     * <p>This will restore the tab list visibility for all players.
     * All global profiles and player-specific profiles will be
     * sent to the respective players.
     * 
     * <p>Note: This operation is performed asynchronously to avoid
     * blocking the main thread when dealing with many players.
     * 
     * @since 1.0.0
     * @see #hideTabForAll()
     * @see #isTabHiddenForAll()
     */
    void showTabForAll();
    
    /**
     * Checks if the tab list is currently hidden for all players.
     * 
     * @return true if the tab is hidden for all players, false otherwise
     * @since 1.0.0
     * @see #hideTabForAll()
     * @see #showTabForAll()
     */
    boolean isTabHiddenForAll();
    
    /**
     * Sets header and footer for all online players.
     * 
     * <p>The header appears above the player list in the tab menu,
     * and the footer appears below it. Both can contain multiple lines
     * and support all Minecraft formatting codes and Adventure components.
     * 
     * <p>Example:
     * <pre>{@code
     * api.setHeaderAndFooterForAll(
     *     Component.text("§6§lMy Server")
     *         .append(Component.newline())
     *         .append(Component.text("§7Welcome!")),
     *     Component.text("§7Players: " + Bukkit.getOnlinePlayers().size())
     * );
     * }</pre>
     * 
     * @param header the header component to display, null to remove header
     * @param footer the footer component to display, null to remove footer
     * @since 1.0.0
     * @see #removeHeaderAndFooterForAll()
     */
    void setHeaderAndFooterForAll(@Nullable Component header, @Nullable Component footer);
    
    /**
     * Removes header and footer for all online players.
     * 
     * <p>This is equivalent to calling {@link #setHeaderAndFooterForAll(Component, Component)}
     * with null values for both parameters.
     * 
     * @since 1.0.0
     * @see #setHeaderAndFooterForAll(Component, Component)
     */
    void removeHeaderAndFooterForAll();
    
    /**
     * Gets the current number of players being managed by this API.
     * 
     * <p>This includes all online players that have been registered
     * with the TabList system.
     * 
     * @return the number of managed players
     * @since 1.0.0
     */
    int getManagedPlayerCount();
    
    /**
     * Gets the API version string.
     * 
     * <p>The version follows semantic versioning (MAJOR.MINOR.PATCH).
     * 
     * @return the version string, never null
     * @since 1.0.0
     */
    @NotNull
    String getVersion();
    
    /**
     * Checks if the API is currently enabled and functional.
     * 
     * <p>The API may be disabled if the underlying plugin is being
     * shut down or if there are critical errors.
     * 
     * @return true if the API is enabled, false otherwise
     * @since 1.0.0
     */
    boolean isEnabled();
    
    /**
     * Reloads the API configuration from the plugin's config file.
     * 
     * <p>This will reload settings such as default tab visibility,
     * header/footer configuration, and other global settings.
     * 
     * <p>Note: This operation may cause a brief interruption in
     * tab list functionality as settings are reapplied.
     * 
     * @since 1.0.0
     */
    void reloadConfiguration();
}

