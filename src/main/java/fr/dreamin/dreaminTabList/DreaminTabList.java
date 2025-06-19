package fr.dreamin.dreaminTabList;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.dreamin.api.cmd.SimpleCommand;
import fr.dreamin.dreaminTabList.api.TabListAPI;
import fr.dreamin.dreaminTabList.cmd.DreaminTabListCmd;
import fr.dreamin.dreaminTabList.config.Codex;
import fr.dreamin.dreaminTabList.event.packet.PacketEvent;
import fr.dreamin.dreaminTabList.event.player.PlayerEvent;
import fr.dreamin.dreaminTabList.impl.TabListAPIImpl;
import fr.dreamin.dreaminTabList.player.core.PlayerTabListManager;
import fr.dreamin.mctools.McTools;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main plugin class for DreaminTabList.
 *
 * <p>DreaminTabList is a comprehensive TabList management plugin for Minecraft servers
 * that provides advanced customization capabilities for player tab lists. It allows
 * server administrators and developers to:
 *
 * <ul>
 *   <li>Hide or show the tab list for all players or specific players</li>
 *   <li>Create fake players and custom entries in the tab list</li>
 *   <li>Customize player appearances with different skins and display names</li>
 *   <li>Set custom headers and footers for the tab list</li>
 *   <li>Manage player-specific tab list content</li>
 *   <li>Control tab list visibility and behavior</li>
 * </ul>
 *
 * <p>The plugin provides both a legacy internal API (for backward compatibility)
 * and a modern public API that follows best practices for plugin development.
 *
 * <p>Key features:
 * <ul>
 *   <li><strong>Packet-based:</strong> Uses PacketEvents for efficient packet manipulation</li>
 *   <li><strong>Thread-safe:</strong> Designed for concurrent access and modification</li>
 *   <li><strong>Event-driven:</strong> Fires custom events for integration with other plugins</li>
 *   <li><strong>Configurable:</strong> Extensive configuration options via config.yml</li>
 *   <li><strong>Developer-friendly:</strong> Clean API with comprehensive documentation</li>
 * </ul>
 *
 * <p>Example usage of the public API:
 * <pre>{@code
 * // Get the API instance
 * TabListAPI api = TabListAPIFactory.getAPI();
 *
 * // Hide tab for all players
 * api.hideTabForAll();
 *
 * // Create a fake player
 * TabProfile fakePlayer = api.getProfileManager()
 *     .createProfile()
 *     .name("ServerBot")
 *     .displayName(Component.text("§c[BOT] §fServer"))
 *     .gameMode(GameMode.CREATIVE)
 *     .build();
 *
 * api.getProfileManager().addGlobalProfile(fakePlayer);
 * }</pre>
 *
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
@Getter
public class DreaminTabList extends JavaPlugin {

  /**
   * The singleton instance of the plugin.
   *
   * @deprecated Use dependency injection or the public API instead of static access
   */
  @Getter @Deprecated
  private static DreaminTabList instance;

  /**
   * The configuration manager instance.
   *
   * @deprecated Use the public API configuration methods instead
   */
  @Getter @Deprecated
  private static Codex codex;

  /**
   * The legacy player tab list manager.
   *
   * @deprecated Use the public API PlayerTabManager instead
   */
  @Getter @Deprecated
  private static PlayerTabListManager playerTabListManager;

  // New API components
  private TabListAPIImpl apiImpl;

  /**
   * Called when the plugin is loaded.
   *
   * <p>This method initializes PacketEvents, which is required for the plugin
   * to function properly. PacketEvents must be loaded before the server
   * starts accepting connections.
   */
  @Override
  public void onLoad() {
    // Initialize PacketEvents
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    PacketEvents.getAPI().load();

    //load Packet from PacketEvent
    PacketEvents.getAPI().getEventManager().registerListener(new PacketEvent(), PacketListenerPriority.HIGH);

    getLogger().info("PacketEvents initialized successfully");
  }

  /**
   * Called when the plugin is enabled.
   *
   * <p>This method performs the main plugin initialization including:
   * <ul>
   *   <li>Setting up the singleton instance</li>
   *   <li>Initializing PacketEvents</li>
   *   <li>Loading configuration</li>
   *   <li>Registering event listeners</li>
   *   <li>Setting up commands</li>
   *   <li>Initializing the public API</li>
   * </ul>
   */
  @Override
  public void onEnable() {
    try {
      // Set singleton instance (for backward compatibility)
      instance = this;

      // Initialize legacy components
      playerTabListManager = new PlayerTabListManager();

      // Initialize McTools integration
      McTools.setInstance(this);

      // Initialize PacketEvents
      PacketEvents.getAPI().init();
      getLogger().info("PacketEvents API initialized");

      // Load configuration
      saveDefaultConfig();
      codex = new Codex(this);
      getLogger().info("Configuration loaded successfully");

      // Register event listeners
      getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
      getLogger().info("Event listeners registered");

      // Load commands
      loadCommands();
      getLogger().info("Commands registered");

      // Initialize the public API
      initializeAPI();

      getLogger().info("DreaminTabList v" + getDescription().getVersion() + " enabled successfully!");

    } catch (Exception e) {
      getLogger().severe("Failed to enable DreaminTabList: " + e.getMessage());
      e.printStackTrace();

      // Disable the plugin if initialization fails
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  /**
   * Called when the plugin is disabled.
   *
   * <p>This method performs cleanup operations including:
   * <ul>
   *   <li>Shutting down the public API</li>
   *   <li>Terminating PacketEvents</li>
   *   <li>Cleaning up resources</li>
   * </ul>
   */
  @Override
  public void onDisable() {
    try {
      // Shutdown the public API
      if (apiImpl != null) {
        apiImpl.shutdown();
        getLogger().info("Public API shut down");
      }

      // Terminate PacketEvents
      PacketEvents.getAPI().terminate();
      getLogger().info("PacketEvents terminated");

      getLogger().info("DreaminTabList disabled successfully");

    } catch (Exception e) {
      getLogger().severe("Error during plugin shutdown: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Fires a Bukkit event.
   *
   * <p>This method is a convenience wrapper around Bukkit's event system
   * and is used internally by the plugin to fire custom events.
   *
   * @param event the event to fire, must not be null
   * @throws IllegalArgumentException if event is null
   */
  public void callEvent(@NotNull Event event) {
    if (event == null) throw new IllegalArgumentException("Event cannot be null");
    Bukkit.getPluginManager().callEvent(event);
  }

  /**
   * Gets the public TabList API instance.
   *
   * <p>This method provides access to the modern, well-documented API
   * that should be used for all new development. The API provides
   * comprehensive TabList management capabilities with proper error
   * handling and thread safety.
   *
   * @return the TabList API instance, never null
   * @throws IllegalStateException if the API is not initialized
   * @since 0.0.1
   */
  @NotNull
  public TabListAPI getAPI() {
    if (apiImpl == null) throw new IllegalStateException("API not initialized");
    return apiImpl;
  }

  /**
   * Checks if the public API is available.
   *
   * <p>This method can be used to safely check if the API is ready
   * for use before attempting to access it.
   *
   * @return true if the API is initialized and available, false otherwise
   * @since 0.0.1
   */
  public boolean isAPIAvailable() {
    return apiImpl != null && apiImpl.isEnabled();
  }

  /**
   * Initializes the public API.
   *
   * <p>This method creates and registers the TabListAPI implementation,
   * making it available for use by other plugins and internal components.
   *
   * @throws IllegalStateException if the API is already initialized
   */
  private void initializeAPI() {
    if (apiImpl != null) throw new IllegalStateException("API already initialized");

    try {
      // Create the API implementation
      apiImpl = new TabListAPIImpl(this);

      // Initialize and register with the factory
      apiImpl.initialize();

      getLogger().info("Public API initialized and registered");

    } catch (Exception e) {
      getLogger().severe("Failed to initialize public API: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("API initialization failed", e);
    }
  }

  /**
   * Loads and registers plugin commands.
   *
   * <p>This method registers all commands provided by the plugin using
   * the SimpleCommand system. Commands include:
   * <ul>
   *   <li>/dreamintablist - Main plugin command</li>
   * </ul>
   */
  private void loadCommands() {
    try {
      SimpleCommand.createCommand("dreamintablist", new DreaminTabListCmd());

      getLogger().fine("Commands loaded successfully");

    } catch (Exception e) {
      getLogger().warning("Failed to load some commands: " + e.getMessage());
    }
  }
}

