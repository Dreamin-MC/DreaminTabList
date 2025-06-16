package examples.advanced;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import fr.dreamin.dreaminTabList.api.TabListAPI;
import fr.dreamin.dreaminTabList.api.TabListAPIFactory;
import fr.dreamin.dreaminTabList.api.events.PlayerTabJoinEvent;
import fr.dreamin.dreaminTabList.api.events.PlayerTabLeaveEvent;
import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced examples of using the DreaminTabList API.
 *
 * <p>This class demonstrates complex use cases and advanced features of the
 * TabList API, including event handling, dynamic updates, permission-based
 * customization, and integration with other systems.
 *
 * <p>These examples are designed for experienced developers who want to
 * leverage the full power of the API for sophisticated tab list management.
 *
 * @author Dreamin
 * @version 0.0.1
 */
public class AdvancedExamples implements Listener {

  private final JavaPlugin plugin;
  private final TabListAPI api;
  private final Map<UUID, BukkitTask> playerTasks = new ConcurrentHashMap<>();
  private final Map<String, TabProfile> staffProfiles = new ConcurrentHashMap<>();

  public AdvancedExamples(JavaPlugin plugin) {
    this.plugin = plugin;
    this.api = TabListAPIFactory.getAPI();

    // Register this class as an event listener
    plugin.getServer().getPluginManager().registerEvents(this, plugin);

    // Initialize staff profiles
    initializeStaffProfiles();

    // Start periodic updates
    startPeriodicUpdates();
  }

  /**
   * Example 1: Event-driven tab customization.
   *
   * <p>This example shows how to listen to TabList events and customize
   * the tab experience based on player actions and permissions.
   */
  @EventHandler
  public void onPlayerTabJoin(PlayerTabJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabManager manager = event.getPlayerManager();

    try {
      // Customize based on player permissions
      if (player.hasPermission("server.vip")) setupVIPPlayer(player, manager);
      else if (player.hasPermission("server.staff")) setupStaffPlayer(player, manager);
      else setupRegularPlayer(player, manager);

      // Add player-specific information
      addPlayerSpecificProfiles(player, manager);

      plugin.getLogger().info("Customized tab for player: " + player.getName());

    } catch (Exception e) {
      plugin.getLogger().warning("Failed to customize tab for " + player.getName() + ": " + e.getMessage());
    }
  }

  @EventHandler
  public void onPlayerTabLeave(PlayerTabLeaveEvent event) {
    UUID playerUuid = event.getPlayerUuid();

    // Clean up any player-specific tasks
    BukkitTask task = playerTasks.remove(playerUuid);
    if (task != null) task.cancel();

    plugin.getLogger().info("Cleaned up tab data for: " + event.getPlayerName());
  }

  /**
   * Example 2: Dynamic tab updates with real-time information.
   *
   * <p>This example demonstrates how to create profiles that update
   * automatically with real-time server information.
   */
  public void example2_DynamicTabUpdates() {
    TabProfileManager profileManager = api.getProfileManager();

    // Create a server info profile that updates every 5 seconds
    BukkitTask updateTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
      try {
        // Get current server stats
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        double tps = getTPS(); // You would implement this method

        // Create or update server info profile
        TabProfile serverInfo = profileManager.createProfile()
          .name("ServerInfo")
          .displayName(Component.text("§6§l[SERVER INFO]")
            .append(Component.newline())
            .append(Component.text("§7Players: §f" + onlinePlayers + "/" + maxPlayers))
            .append(Component.newline())
            .append(Component.text("§7Memory: §f" + usedMemory + "MB"))
            .append(Component.newline())
            .append(Component.text("§7TPS: §f" + String.format("%.1f", tps))))
          .gameMode(GameMode.SPECTATOR)
          .latency(0)
          .sortOrder(-1000) // Always at the top
          .build();

        profileManager.updateGlobalProfile(serverInfo);

      } catch (Exception e) {
        plugin.getLogger().warning("Failed to update server info profile: " + e.getMessage());
      }
    }, 0L, 100L); // Update every 5 seconds

    // Store task for cleanup
    playerTasks.put(UUID.randomUUID(), updateTask);
  }

  /**
   * Example 3: Permission-based tab groups and sorting.
   *
   * <p>This example shows how to organize players in the tab list based
   * on their permissions and roles, with custom sorting and grouping.
   */
  public void example3_PermissionBasedGroups() {
    TabProfileManager profileManager = api.getProfileManager();

    // Create group headers
    createGroupHeader(profileManager, "§c§lSTAFF", -1000);
    createGroupHeader(profileManager, "§6§lVIP", -500);
    createGroupHeader(profileManager, "§7§lPLAYERS", 0);

    // Update all online players with appropriate groups
    for (Player player : Bukkit.getOnlinePlayers()) {
      try {
        PlayerTabManager playerManager = api.getPlayerManager(player);

        // Determine player group and sort order
        String group;
        int sortOrder;
        Component displayName;

        if (player.hasPermission("server.admin")) {
          group = "staff";
          sortOrder = -900;
          displayName = Component.text("§c[ADMIN] §f" + player.getName());
        } else if (player.hasPermission("server.moderator")) {
          group = "staff";
          sortOrder = -800;
          displayName = Component.text("§9[MOD] §f" + player.getName());
        } else if (player.hasPermission("server.vip")) {
          group = "vip";
          sortOrder = -400;
          displayName = Component.text("§6[VIP] §f" + player.getName());
        } else {
          group = "players";
          sortOrder = 100;
          displayName = Component.text("§7" + player.getName());
        }

        // Create updated profile for this player
        TabProfile playerProfile = profileManager.createProfileFromPlayer(player)
          .toBuilder()
          .displayName(displayName)
          .group(group)
          .sortOrder(sortOrder)
          .build();

        profileManager.updateGlobalProfile(playerProfile);

      } catch (Exception e) {
        plugin.getLogger().warning("Failed to update group for " +
          player.getName() + ": " + e.getMessage());
      }
    }
  }

  /**
   * Example 4: Custom tab animations and effects.
   *
   * <p>This example demonstrates how to create animated tab entries
   * with changing colors, text, and other visual effects.
   */
  public void example4_TabAnimations() {
    TabProfileManager profileManager = api.getProfileManager();

    // Create an animated welcome message
    final String[] animationFrames = {
      "§6§l>>> §fWELCOME §6§l<<<",
      "§e§l>>> §fWELCOME §e§l<<<",
      "§f§l>>> §fWELCOME §f§l<<<",
      "§e§l>>> §fWELCOME §e§l<<<"
    };

    final int[] frameIndex = {0};

    BukkitTask animationTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
      try {
        String currentFrame = animationFrames[frameIndex[0]];

        TabProfile animatedProfile = profileManager.createProfile()
          .name("WelcomeMessage")
          .displayName(Component.text(currentFrame))
          .gameMode(GameMode.SPECTATOR)
          .latency(0)
          .sortOrder(-2000)
          .build();

        profileManager.updateGlobalProfile(animatedProfile);

        // Move to next frame
        frameIndex[0] = (frameIndex[0] + 1) % animationFrames.length;

      } catch (Exception e) {
        plugin.getLogger().warning("Failed to update animation: " + e.getMessage());
      }
    }, 0L, 10L); // Update every 0.5 seconds

    // Store task for cleanup
    playerTasks.put(UUID.randomUUID(), animationTask);
  }

  /**
   * Example 5: Integration with external data sources.
   *
   * <p>This example shows how to integrate the tab list with external
   * data sources like databases, APIs, or other plugins.
   */
  public void example5_ExternalDataIntegration() {
    TabProfileManager profileManager = api.getProfileManager();

    // Simulate fetching data from an external source
    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
      try {
        // Simulate API call or database query
        Map<String, Object> externalData = fetchExternalData();

        // Process the data on the main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
          try {
            // Create profiles based on external data
            for (Map.Entry<String, Object> entry : externalData.entrySet()) {
              String playerName = entry.getKey();
              Map<String, Object> playerData = (Map<String, Object>) entry.getValue();

              Component displayName = Component.text("§b[EXTERNAL] §f" + playerName)
                .append(Component.newline())
                .append(Component.text("§7Rank: §f" + playerData.get("rank")))
                .append(Component.newline())
                .append(Component.text("§7Score: §f" + playerData.get("score")));

              TabProfile externalProfile = profileManager.createProfile()
                .name("ext_" + playerName)
                .displayName(displayName)
                .gameMode(GameMode.ADVENTURE)
                .latency((Integer) playerData.get("ping"))
                .sortOrder(-1500)
                .group("external")
                .build();

              profileManager.addGlobalProfile(externalProfile);
            }

            plugin.getLogger().info("Integrated " + externalData.size() + " external profiles");

          } catch (Exception e) {
            plugin.getLogger().warning("Failed to process external data: " + e.getMessage());
          }
        });

      } catch (Exception e) {
        plugin.getLogger().warning("Failed to fetch external data: " + e.getMessage());
      }
    });
  }

  /**
   * Example 6: Advanced player-specific customization.
   *
   * <p>This example demonstrates sophisticated per-player customization
   * based on player data, preferences, and behavior.
   */
  public void example6_AdvancedPlayerCustomization(Player player) {
    try {
      PlayerTabManager playerManager = api.getPlayerManager(player);

      // Get player-specific data (you would implement these methods)
      String playerLanguage = getPlayerLanguage(player);
      boolean playerPrefersMinimal = getPlayerPreference(player, "minimal_tab");
      int playerLevel = getPlayerLevel(player);

      // Customize header based on language
      Component header = getLocalizedHeader(playerLanguage, player);

      // Customize footer based on preferences
      Component footer;
      if (playerPrefersMinimal) footer = Component.text("§7Level " + playerLevel);
      else {
        footer = Component.text("§7Level " + playerLevel)
          .append(Component.newline())
          .append(Component.text("§7Playtime: " + getPlayerPlaytime(player)))
          .append(Component.newline())
          .append(Component.text("§7Last seen: " + getPlayerLastSeen(player)));
      }

      playerManager.setHeaderAndFooter(header, footer);

      // Add player-specific profiles based on their friends/guild
      addFriendsToTab(player, playerManager);
      addGuildMembersToTab(player, playerManager);

      plugin.getLogger().info("Applied advanced customization for: " + player.getName());

    } catch (Exception e) {
      plugin.getLogger().warning("Failed to apply advanced customization for " + player.getName() + ": " + e.getMessage());
    }
  }

  // Helper methods for the examples

  private void setupVIPPlayer(Player player, PlayerTabManager manager) {
    Component vipHeader = Component.text("§6§lVIP SERVER", NamedTextColor.GOLD, TextDecoration.BOLD)
      .append(Component.newline())
      .append(Component.text("§7Welcome back, " + player.getName() + "!"));

    Component vipFooter = Component.text("§7Thank you for supporting us!")
      .append(Component.newline())
      .append(Component.text("§6VIP perks are active"));

    manager.setHeaderAndFooter(vipHeader, vipFooter);
  }

  private void setupStaffPlayer(Player player, PlayerTabManager manager) {
    Component staffHeader = Component.text("§c§lSTAFF PANEL", NamedTextColor.RED, TextDecoration.BOLD)
      .append(Component.newline())
      .append(Component.text("§7Welcome, " + player.getName()));

    Component staffFooter = Component.text("§7Staff tools available")
      .append(Component.newline())
      .append(Component.text("§cRemember the rules!"));

    manager.setHeaderAndFooter(staffHeader, staffFooter);
  }

  private void setupRegularPlayer(Player player, PlayerTabManager manager) {
    Component regularHeader = Component.text("§a§lWELCOME", NamedTextColor.GREEN, TextDecoration.BOLD)
      .append(Component.newline())
      .append(Component.text("§7Hello, " + player.getName() + "!"));

    Component regularFooter = Component.text("§7Enjoy your stay!")
      .append(Component.newline())
      .append(Component.text("§7Type /help for commands"));

    manager.setHeaderAndFooter(regularHeader, regularFooter);
  }

  private void addPlayerSpecificProfiles(Player player, PlayerTabManager manager) {
    // Add a personal assistant bot for this player
    TabProfile personalBot = api.getProfileManager().createProfile()
      .name("PersonalBot_" + player.getName())
      .displayName(Component.text("§a[YOUR BOT] §fAssistant"))
      .gameMode(GameMode.SPECTATOR)
      .latency(0)
      .sortOrder(-100)
      .build();

    manager.addProfile(personalBot);
  }

  private void initializeStaffProfiles() {
    // This would typically load from a configuration file or database
    String[] staffMembers = {"Admin1", "Moderator1", "Helper1"};

    for (String staffName : staffMembers) {
      TabProfile staffProfile = api.getProfileManager().createProfile()
        .name(staffName)
        .displayName(Component.text("§c[STAFF] §f" + staffName))
        .gameMode(GameMode.CREATIVE)
        .latency(0)
        .sortOrder(-900)
        .group("staff")
        .build();

      staffProfiles.put(staffName, staffProfile);
    }
  }

  private void startPeriodicUpdates() {
    // Update tab every minute with fresh information
    plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
      updateServerStatistics();
      updatePlayerCounts();
    }, 0L, 1200L); // Every minute
  }

  private void createGroupHeader(TabProfileManager profileManager, String headerText, int sortOrder) {
    TabProfile header = profileManager.createProfile()
      .name("header_" + sortOrder)
      .displayName(Component.text(headerText))
      .gameMode(GameMode.SPECTATOR)
      .latency(0)
      .sortOrder(sortOrder)
      .listed(false) // Headers are usually not listed
      .build();

    profileManager.addGlobalProfile(header);
  }

  // Placeholder methods (you would implement these based on your needs)
  private double getTPS() { return 20.0; }
  private Map<String, Object> fetchExternalData() { return new HashMap<>(); }
  private String getPlayerLanguage(Player player) { return "en"; }
  private boolean getPlayerPreference(Player player, String preference) { return false; }
  private int getPlayerLevel(Player player) { return 1; }
  private String getPlayerPlaytime(Player player) { return "1h 30m"; }
  private String getPlayerLastSeen(Player player) { return "Now"; }
  private Component getLocalizedHeader(String language, Player player) {
    return Component.text("§6Welcome, " + player.getName() + "!");
  }
  private void addFriendsToTab(Player player, PlayerTabManager manager) { /* Implementation */ }
  private void addGuildMembersToTab(Player player, PlayerTabManager manager) { /* Implementation */ }
  private void updateServerStatistics() { /* Implementation */ }
  private void updatePlayerCounts() { /* Implementation */ }
}

