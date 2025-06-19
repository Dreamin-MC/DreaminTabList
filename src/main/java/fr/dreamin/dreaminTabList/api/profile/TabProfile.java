package fr.dreamin.dreaminTabList.api.profile;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a profile in the TabList.
 * 
 * <p>A TabProfile contains all the information needed to display an entry
 * in a player's tab list. Profiles can represent real players or fake entries
 * created for display purposes.
 * 
 * <p>Profiles are immutable once created. To modify a profile, use the
 * {@link #toBuilder()} method to create a new builder with the current values.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Create a new profile
 * TabProfile profile = api.getProfileManager()
 *     .createProfile()
 *     .name("TestPlayer")
 *     .displayName(Component.text("§6Test Player"))
 *     .gameMode(GameMode.CREATIVE)
 *     .latency(50)
 *     .build();
 * 
 * // Modify an existing profile
 * TabProfile updated = profile.toBuilder()
 *     .displayName(Component.text("§cUpdated Name"))
 *     .latency(25)
 *     .build();
 * }</pre>
 * 
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
public interface TabProfile {
    
    /**
     * Gets the unique identifier of this profile.
     * 
     * <p>For real players, this will be their Minecraft UUID.
     * For fake profiles, this will be a generated UUID based on the name.
     * 
     * @return the UUID, never null
     * @since 0.0.1
     */
    @NotNull
    UUID getUniqueId();
    
    /**
     * Gets the name of this profile.
     * 
     * <p>This is the name that appears in the tab list and is used
     * for sorting and identification purposes. For real players,
     * this is their Minecraft username.
     * 
     * @return the profile name, never null
     * @since 0.0.1
     */
    @NotNull
    String getName();
    
    /**
     * Gets the display name component of this profile.
     * 
     * <p>The display name is what actually appears in the tab list
     * and can contain formatting, colors, and other Adventure components.
     * If no custom display name is set, this will be a plain text
     * component of the profile name.
     * 
     * @return the display name component, never null
     * @since 0.0.1
     */
    @NotNull
    Component getDisplayName();
    
    /**
     * Gets the game mode of this profile.
     * 
     * <p>The game mode affects how the player appears in the tab list,
     * including the icon displayed next to their name.
     * 
     * @return the game mode, never null
     * @since 0.0.1
     */
    @NotNull
    GameMode getGameMode();
    
    /**
     * Gets the latency (ping) of this profile in milliseconds.
     * 
     * <p>The latency determines the connection bars displayed in the
     * tab list. Values typically range from 0 to 1000+ milliseconds.
     * 
     * <p>Latency ranges:
     * <ul>
     *   <li>0-150ms: 5 bars (excellent)</li>
     *   <li>150-300ms: 4 bars (good)</li>
     *   <li>300-600ms: 3 bars (fair)</li>
     *   <li>600-1000ms: 2 bars (poor)</li>
     *   <li>1000+ms: 1 bar (very poor)</li>
     * </ul>
     * 
     * @return the latency in milliseconds, always non-negative
     * @since 0.0.1
     */
    int getLatency();
    
    /**
     * Checks if this profile is listed in the tab.
     * 
     * <p>Unlisted profiles are still part of the tab list system
     * but are not visible to players. This can be useful for
     * administrative or hidden entries.
     * 
     * @return true if the profile is visible in the tab list, false otherwise
     * @since 0.0.1
     */
    boolean isListed();
    
    /**
     * Gets the sort order of this profile.
     * 
     * <p>Profiles are sorted in the tab list based on this value.
     * Lower values appear higher in the list. Profiles with the
     * same sort order are sorted alphabetically by name.
     * 
     * @return the sort order value
     * @since 0.0.1
     */
    int getSortOrder();
    
    /**
     * Checks if the player hat/helmet should be shown.
     * 
     * <p>When true, the player's helmet or hat layer of their skin
     * will be displayed in the tab list. When false, only the base
     * skin layer is shown.
     * 
     * @return true if the hat should be shown, false otherwise
     * @since 0.0.1
     */
    boolean isShowHat();
    
    /**
     * Gets the group this profile belongs to.
     * 
     * <p>Groups can be used for organizing profiles and applying
     * group-specific formatting or behavior. This is optional
     * and may be null if no group is assigned.
     * 
     * @return the group name, or null if no group is assigned
     * @since 0.0.1
     */
    @Nullable
    String getGroup();
    
    /**
     * Checks if this profile represents a real player.
     * 
     * <p>Real player profiles are automatically synchronized with
     * the actual player's state (online status, game mode, etc.).
     * Fake profiles are manually managed and do not change unless
     * explicitly updated.
     * 
     * @return true if this represents a real player, false if it's a fake profile
     * @since 0.0.1
     */
    boolean isRealPlayer();
    
    /**
     * Checks if this profile is currently synchronized with vanilla Minecraft data.
     * 
     * <p>For real players, this indicates whether the profile data
     * is automatically kept in sync with the player's actual state.
     * For fake profiles, this is always false.
     * 
     * @return true if synchronized with vanilla data, false otherwise
     * @since 0.0.1
     */
    boolean isVanillaSynced();
    
    /**
     * Gets the skin texture value for this profile.
     * 
     * <p>This is the base64-encoded texture data that defines
     * the player's skin appearance. May be null if using the
     * default skin or if skin data is not available.
     * 
     * @return the skin texture value, or null if not set
     * @since 0.0.1
     */
    @Nullable
    String getSkinTexture();
    
    /**
     * Gets the skin signature for this profile.
     * 
     * <p>This is the cryptographic signature that validates the
     * skin texture data. Required when using custom skin textures.
     * May be null if using the default skin.
     * 
     * @return the skin signature, or null if not set
     * @since 0.0.1
     */
    @Nullable
    String getSkinSignature();
    
    /**
     * Creates a builder for modifying this profile.
     * 
     * <p>Since profiles are immutable, this is the way to create
     * a modified version of an existing profile. The builder will
     * be pre-populated with all current values from this profile.
     * 
     * <p>Example:
     * <pre>{@code
     * TabProfile updated = existingProfile.toBuilder()
     *     .displayName(Component.text("§cNew Name"))
     *     .latency(100)
     *     .build();
     * }</pre>
     * 
     * @return a new builder with current values, never null
     * @since 0.0.1
     */
    @NotNull
    TabProfileBuilder toBuilder();
    
    /**
     * Checks if this profile is equal to another object.
     * 
     * <p>Two profiles are considered equal if they have the same UUID.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     * @since 0.0.1
     */
    @Override
    boolean equals(Object obj);
    
    /**
     * Returns the hash code for this profile.
     * 
     * <p>The hash code is based on the profile's UUID.
     * 
     * @return the hash code
     * @since 0.0.1
     */
    @Override
    int hashCode();
    
    /**
     * Returns a string representation of this profile.
     * 
     * <p>The string includes the profile name, UUID, and key properties.
     * 
     * @return a string representation of this profile
     * @since 0.0.1
     */
    @Override
    String toString();
}

