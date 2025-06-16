package examples.basic;

import fr.dreamin.dreaminTabList.api.TabListAPI;
import fr.dreamin.dreaminTabList.api.TabListAPIFactory;
import fr.dreamin.dreaminTabList.api.player.PlayerTabManager;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Basic examples of using the DreaminTabList API.
 *
 * <p>This class demonstrates the most common use cases for the TabList API,
 * including basic profile management, tab visibility control, and header/footer
 * customization.
 *
 * <p>These examples are designed to be simple and easy to understand, making
 * them perfect for developers who are just getting started with the API.
 *
 * @author Dreamin
 * @version 0.0.1
 */
public class BasicExamples {

  private final JavaPlugin plugin;
  private final TabListAPI api;

  public BasicExamples(JavaPlugin plugin) {
    this.plugin = plugin;
    this.api = TabListAPIFactory.getAPI();
  }

  /**
   * Example 1: Basic API access and information.
   *
   * <p>This example shows how to get the API instance and access basic
   * information about the API and current state.
   */
  public void example1_BasicAPIAccess() {
    // Get the API instance
    TabListAPI api = TabListAPIFactory.getAPI();

    // Check if API is available
    if (!TabListAPIFactory.isInitialized()) {
      plugin.getLogger().warning("TabList API is not available!");
      return;
    }

    // Get basic information
    String version = api.getVersion();
    boolean enabled = api.isEnabled();
    int playerCount = api.getManagedPlayerCount();

    plugin.getLogger().info("TabList API v" + version + " is " + (enabled ? "enabled" : "disabled"));
    plugin.getLogger().info("Currently managing " + playerCount + " players");
  }

  /**
   * Example 2: Hide and show tab for all players.
   *
   * <p>This example demonstrates how to control tab visibility globally
   * for all players on the server.
   */
  public void example2_GlobalTabVisibility() {
    // Hide tab for all players
    api.hideTabForAll();
    plugin.getLogger().info("Tab hidden for all players");

    // Wait a bit (in a real plugin, you might do this based on an event)
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
      // Show tab for all players
      api.showTabForAll();
      plugin.getLogger().info("Tab shown for all players");
    }, 100L); // 5 seconds delay
  }

  /**
   * Example 3: Set global header and footer.
   *
   * <p>This example shows how to set custom header and footer text that
   * will be displayed to all players in their tab list.
   */
  public void example3_GlobalHeaderFooter() {
    // Create header and footer components
    Component header = Component.text("§6§lMy Awesome Server")
      .append(Component.newline())
      .append(Component.text("§7Welcome to our community!"));

    Component footer = Component.text("§7Players online: " +
        plugin.getServer().getOnlinePlayers().size())
      .append(Component.newline())
      .append(Component.text("§7Visit: §bwww.example.com"));

    // Set header and footer for all players
    api.setHeaderAndFooterForAll(header, footer);
    plugin.getLogger().info("Header and footer set for all players");

    // Example: Remove header and footer after some time
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
      api.removeHeaderAndFooterForAll();
      plugin.getLogger().info("Header and footer removed for all players");
    }, 200L); // 10 seconds delay
  }

  /**
   * Example 4: Create a simple fake player.
   *
   * <p>This example demonstrates how to create a fake player entry that
   * will appear in all players' tab lists.
   */
  public void example4_CreateFakePlayer() {
    TabProfileManager profileManager = api.getProfileManager();

    // Create a simple fake player
    TabProfile fakePlayer = profileManager.createProfile()
      .name("ServerBot")
      .displayName(Component.text("§c[BOT] §fServer Assistant"))
      .build();

    // Add to global profiles (visible to all players)
    profileManager.addGlobalProfile(fakePlayer);
    plugin.getLogger().info("Created fake player: " + fakePlayer.getName());

    // Example: Remove the fake player after some time
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
      profileManager.removeGlobalProfile(fakePlayer);
      plugin.getLogger().info("Removed fake player: " + fakePlayer.getName());
    }, 300L); // 15 seconds delay
  }

  /**
   * Example 5: Player-specific tab management.
   *
   * <p>This example shows how to manage the tab list for a specific player,
   * including hiding the tab and setting custom header/footer.
   */
  public void example5_PlayerSpecificManagement(Player player) {
    try {
      // Get the player's tab manager
      PlayerTabManager playerManager = api.getPlayerManager(player);

      // Hide tab for this specific player
      playerManager.hideTab();
      plugin.getLogger().info("Hidden tab for player: " + player.getName());

      // Set custom header/footer for this player
      Component personalHeader = Component.text("§6Welcome, " + player.getName() + "!");
      Component personalFooter = Component.text("§7Your rank: §aVIP");

      playerManager.setHeaderAndFooter(personalHeader, personalFooter);
      plugin.getLogger().info("Set personal header/footer for: " + player.getName());

      // Show tab again after a delay
      plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
        if (playerManager.isValid()) {
          playerManager.showTab();
          plugin.getLogger().info("Shown tab for player: " + player.getName());
        }
      }, 100L); // 5 seconds delay

    } catch (Exception e) {
      plugin.getLogger().warning("Failed to manage tab for player " +
        player.getName() + ": " + e.getMessage());
    }
  }

  /**
   * Example 6: Create a fake player with custom skin.
   *
   * <p>This example demonstrates how to create a fake player with a custom
   * skin copied from an existing player.
   */
  public void example6_FakePlayerWithSkin() {
    TabProfileManager profileManager = api.getProfileManager();

    try {
      // Create a fake player with skin from "Notch"
      TabProfile fakePlayerWithSkin = profileManager.createProfile()
        .name("FakeNotch")
        .displayName(Component.text("§6[FAKE] §fNotch"))
        .skinFromPlayer("Notch") // This will fetch Notch's skin
        .build();

      // Add to global profiles
      profileManager.addGlobalProfile(fakePlayerWithSkin);
      plugin.getLogger().info("Created fake player with custom skin: " +
        fakePlayerWithSkin.getName());

    } catch (Exception e) {
      plugin.getLogger().warning("Failed to create fake player with skin: " +
        e.getMessage());
    }
  }

  /**
   * Example 7: Check and manage existing profiles.
   *
   * <p>This example shows how to search for and manage existing profiles
   * in the global profile list.
   */
  public void example7_ManageExistingProfiles() {
    TabProfileManager profileManager = api.getProfileManager();

    // Get all global profiles
    int profileCount = profileManager.getGlobalProfileCount();
    plugin.getLogger().info("Total global profiles: " + profileCount);

    // Find a specific profile by name
    TabProfile foundProfile = profileManager.findProfile("ServerBot");
    if (foundProfile != null) {
      plugin.getLogger().info("Found profile: " + foundProfile.getName());

      // Update the profile
      TabProfile updatedProfile = foundProfile.toBuilder()
        .displayName(Component.text("§a[UPDATED] §fServer Bot"))
        .build();

      profileManager.updateGlobalProfile(updatedProfile);
      plugin.getLogger().info("Updated profile: " + updatedProfile.getName());
    } else {
      plugin.getLogger().info("Profile 'ServerBot' not found");
    }

    // Check if a profile exists
    boolean hasProfile = profileManager.hasProfile("ServerBot");
    plugin.getLogger().info("Has ServerBot profile: " + hasProfile);
  }

  /**
   * Example 8: Reload configuration.
   *
   * <p>This example demonstrates how to reload the plugin configuration
   * and apply changes to all players.
   */
  public void example8_ReloadConfiguration() {
    try {
      // Reload the configuration
      api.reloadConfiguration();
      plugin.getLogger().info("Configuration reloaded successfully");

      // Check current state after reload
      boolean tabHidden = api.isTabHiddenForAll();
      plugin.getLogger().info("Tab hidden for all after reload: " + tabHidden);

    } catch (Exception e) {
      plugin.getLogger().warning("Failed to reload configuration: " + e.getMessage());
    }
  }
}

