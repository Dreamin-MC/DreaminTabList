package fr.dreamin.dreaminTabList.api.profile;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import fr.dreamin.dreaminTabList.api.exceptions.InvalidProfileException;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Builder for creating and modifying TabProfile instances.
 * 
 * <p>This builder provides a fluent interface for constructing TabProfile
 * objects with validation and sensible defaults. All methods return the
 * builder instance to allow method chaining.
 * 
 * <p>Example usage:
 * <pre>{@code
 * TabProfile profile = api.getProfileManager()
 *     .createProfile()
 *     .name("TestPlayer")
 *     .displayName(Component.text("§6Test Player"))
 *     .gameMode(GameMode.CREATIVE)
 *     .latency(50)
 *     .listed(true)
 *     .sortOrder(100)
 *     .skinFromPlayer("Notch")
 *     .build();
 * }</pre>
 * 
 * <p>Default values:
 * <ul>
 *   <li>Game Mode: {@link GameMode#SURVIVAL}</li>
 *   <li>Latency: 0ms</li>
 *   <li>Listed: true</li>
 *   <li>Sort Order: 0</li>
 *   <li>Show Hat: true</li>
 *   <li>Group: null</li>
 * </ul>
 * 
 * @author Dreamin
 * @version 0.0.2
 * @since 0.0.1
 */
public interface TabProfileBuilder {
    
    /**
     * Sets the name of the profile.
     * 
     * <p>The name must be a valid Minecraft username (3-16 characters,
     * alphanumeric and underscore only). For fake profiles, the name
     * will be used to generate a UUID.
     * 
     * @param name the profile name, must not be null or empty
     * @return this builder for method chaining
     * @throws IllegalArgumentException if name is null, empty, or invalid
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder name(@NotNull String name);
    
    /**
     * Sets the display name of the profile.
     * 
     * <p>The display name is what appears in the tab list and can
     * contain formatting, colors, and other Adventure components.
     * If not set, the display name will default to a plain text
     * component of the profile name.
     * 
     * @param displayName the display name component, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if displayName is null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder displayName(@NotNull Component displayName);
    
    /**
     * Sets the display name of the profile from a string.
     * 
     * <p>This is a convenience method that converts the string to a
     * Component. The string can contain legacy color codes (§) or
     * MiniMessage format.
     * 
     * @param displayName the display name string, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if displayName is null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder displayName(@NotNull String displayName);
    
    /**
     * Sets the game mode of the profile.
     * 
     * <p>The game mode affects the icon displayed next to the player's
     * name in the tab list.
     * 
     * @param gameMode the game mode, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if gameMode is null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder gameMode(@NotNull GameMode gameMode);
    
    /**
     * Sets the latency of the profile.
     * 
     * <p>The latency determines the connection bars displayed in the
     * tab list. Must be non-negative.
     * 
     * @param latency the latency in milliseconds, must be >= 0
     * @return this builder for method chaining
     * @throws IllegalArgumentException if latency is negative
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder latency(int latency);
    
    /**
     * Sets whether the profile is listed in the tab.
     * 
     * <p>Unlisted profiles are part of the system but not visible
     * to players in the tab list.
     * 
     * @param listed true to show in tab list, false to hide
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder listed(boolean listed);
    
    /**
     * Sets the sort order of the profile.
     * 
     * <p>Lower values appear higher in the tab list. Profiles with
     * the same sort order are sorted alphabetically by name.
     * 
     * @param order the sort order value
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder sortOrder(int order);
    
    /**
     * Sets whether the player hat/helmet should be shown.
     * 
     * @param showHat true to show the hat layer, false to hide it
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder showHat(boolean showHat);
    
    /**
     * Sets the group this profile belongs to.
     * 
     * <p>Groups can be used for organizing profiles and applying
     * group-specific behavior.
     * 
     * @param group the group name, or null to remove from any group
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder group(@Nullable String group);
    
    /**
     * Sets the skin of the profile by copying from an online player.
     * 
     * <p>This will fetch the skin data from the specified player and
     * apply it to this profile. The player must be online.
     * 
     * @param player the player to copy the skin from, must not be null and must be online
     * @return this builder for method chaining
     * @throws IllegalArgumentException if player is null or not online
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder skinFromPlayer(@NotNull Player player);
    
    /**
     * Sets the skin of the profile by player name.
     * 
     * <p>This will attempt to fetch the skin data for the specified
     * player name from Mojang's servers. This operation may be slow
     * and should be used sparingly.
     * 
     * @param playerName the player name to get skin from, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if playerName is null or invalid
     * @throws InvalidProfileException if the skin cannot be fetched
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder skinFromPlayer(@NotNull String playerName);
    
    /**
     * Sets a custom skin for the profile.
     * 
     * <p>Both texture and signature must be provided and valid.
     * The texture should be a base64-encoded skin texture value,
     * and the signature should be the corresponding cryptographic signature.
     * 
     * @param texture the skin texture value, must not be null
     * @param signature the skin signature, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if texture or signature is null or invalid
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder customSkin(@NotNull String texture, @NotNull String signature);
    
    /**
     * Removes any custom skin, reverting to the default skin.
     * 
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder defaultSkin();
    
    /**
     * Sets a custom UUID for this profile.
     * 
     * <p>Normally, UUIDs are generated automatically based on the name
     * for fake profiles or taken from the player for real profiles.
     * This method allows overriding that behavior.
     * 
     * <p><strong>Warning:</strong> Using duplicate UUIDs can cause
     * conflicts and unexpected behavior.
     * 
     * @param uuid the custom UUID, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if uuid is null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder uuid(@NotNull UUID uuid);
    
    /**
     * Marks this profile as representing a real player.
     * 
     * <p>Real player profiles are automatically synchronized with
     * the player's actual state. This should only be used when
     * creating profiles for actual online players.
     * 
     * @param realPlayer true if this represents a real player
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder realPlayer(boolean realPlayer);
    
    /**
     * Copies all values from another profile.
     * 
     * <p>This will overwrite any values previously set in this builder
     * with the values from the specified profile.
     * 
     * @param profile the profile to copy from, must not be null
     * @return this builder for method chaining
     * @throws IllegalArgumentException if profile is null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder copyFrom(@NotNull TabProfile profile);
    
    /**
     * Resets all values to their defaults.
     * 
     * <p>This clears any previously set values and returns the builder
     * to its initial state. The name must be set again before building.
     * 
     * @return this builder for method chaining
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder reset();
    
    /**
     * Builds the TabProfile instance.
     * 
     * <p>This validates all the configured values and creates an
     * immutable TabProfile instance. The builder can be reused
     * after calling this method.
     * 
     * <p>Required fields that must be set before building:
     * <ul>
     *   <li>Name (via {@link #name(String)})</li>
     * </ul>
     * 
     * @return the created profile, never null
     * @throws InvalidProfileException if the profile configuration is invalid
     * @throws IllegalStateException if required fields are not set
     * @since 0.0.1
     */
    @NotNull
    TabProfile build();
}

