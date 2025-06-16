package fr.dreamin.dreaminTabList.api.exceptions;

import org.bukkit.entity.Player;

import java.io.Serial;
import java.util.UUID;

/**
 * Thrown when a requested player is not found in the TabList system.
 * 
 * <p>This exception is thrown when attempting to access or manipulate
 * a player that is not currently managed by the TabList API. This can
 * happen when:
 * <ul>
 *   <li>The player has not joined the server yet</li>
 *   <li>The player has left the server</li>
 *   <li>The player was not properly registered with the system</li>
 *   <li>There was an error during player initialization</li>
 * </ul>
 * 
 * <p>Example scenario:
 * <pre>{@code
 * Player player = Bukkit.getPlayer("NonExistentPlayer");
 * if (player != null) {
 *     // This might throw PlayerNotFoundException if the player
 *     // wasn't properly registered with the TabList system
 *     PlayerTabManager manager = api.getPlayerManager(player);
 * }
 * }</pre>
 * 
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public class PlayerNotFoundException extends TabListException {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final String playerName;
    private final UUID playerUuid;
    
    /**
     * Constructs a new player not found exception with the specified player name.
     * 
     * @param playerName the name of the player that was not found
     */
    public PlayerNotFoundException(String playerName) {
        super("Player not found: " + playerName);
        this.playerName = playerName;
        this.playerUuid = null;
    }
    
    /**
     * Constructs a new player not found exception with the specified player UUID.
     * 
     * @param playerUuid the UUID of the player that was not found
     */
    public PlayerNotFoundException(UUID playerUuid) {
        super("Player not found: " + playerUuid);
        this.playerName = null;
        this.playerUuid = playerUuid;
    }
    
    /**
     * Constructs a new player not found exception with the specified player.
     * 
     * @param player the player that was not found
     */
    public PlayerNotFoundException(Player player) {
        super("Player not found: " + player.getName() + " (" + player.getUniqueId() + ")");
        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
    }
    
    /**
     * Constructs a new player not found exception with a custom message.
     * 
     * @param message the detail message explaining why the player was not found
     */
    public PlayerNotFoundException(String message, String playerName, UUID playerUuid) {
        super(message);
        this.playerName = playerName;
        this.playerUuid = playerUuid;
    }
    
    /**
     * Gets the name of the player that was not found.
     * 
     * @return the player name, or null if not available
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Gets the UUID of the player that was not found.
     * 
     * @return the player UUID, or null if not available
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }
}

