package fr.dreamin.dreaminTabList.config;

import fr.dreamin.dreaminTabList.DreaminTabList;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Configuration manager for the DreaminTabList plugin.
 *
 * <p>The Codex class handles all configuration-related operations for the plugin,
 * including loading, parsing, and applying configuration values. It provides
 * a centralized way to access configuration settings and automatically applies
 * changes to all online players when the configuration is reloaded.
 *
 * <p>Supported configuration options:
 * <ul>
 *   <li><strong>hide-tab:</strong> Whether to hide the tab list for all players by default</li>
 *   <li><strong>hide-player-join:</strong> Whether to hide new players when they join</li>
 *   <li><strong>header-footer.enabled:</strong> Whether to enable custom header and footer</li>
 *   <li><strong>header-footer.header:</strong> List of header lines</li>
 *   <li><strong>header-footer.footer:</strong> List of footer lines</li>
 * </ul>
 *
 * <p>Example configuration:
 * <pre>{@code
 * hide-tab: false
 * hide-player-join: false
 * header-footer:
 *   enabled: true
 *   header:
 *     - "§6§lMy Server"
 *     - "§7Welcome to our community!"
 *   footer:
 *     - "§7Players online: %online%"
 *     - "§7Visit our website: example.com"
 * }</pre>
 *
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
@Getter @Setter
public class Codex {

  /**
   * The plugin instance this configuration manager belongs to.
   */
  private final DreaminTabList instance;

  /**
   * The Bukkit configuration file instance.
   */
  private FileConfiguration config;

  /**
   * Whether the tab list should be hidden for all players by default.
   * -- GETTER --
   *  Checks if the tab list is configured to be hidden by default.
   *
   * @return true if the tab should be hidden, false otherwise

   */
  @Getter
  private boolean hideTab;

  /**
   * Whether new players should be hidden when they join the server.
   *
   * <p>When enabled, new players will not appear in other players' tab lists
   * when they join the server. This can be useful for creating a more
   * controlled tab list experience.
   * -- GETTER --
   *  Checks if new players should be hidden when they join.
   *
   * @return true if new players should be hidden, false otherwise

   */
  @Getter
  private boolean hidePlayerJoin;

  /**
   * Whether custom header and footer are enabled.
   * -- GETTER --
   *  Checks if custom header and footer are enabled.
   *
   * @return true if header/footer are enabled, false otherwise

   */
  @Getter
  private boolean headerFooterEnabled;

  /**
   * The header component to display above the tab list.
   *
   * <p>This component is built from the configuration and supports
   * multiple lines and legacy color codes.
   */
  private Component headers = Component.empty();

  /**
   * The footer component to display below the tab list.
   *
   * <p>This component is built from the configuration and supports
   * multiple lines and legacy color codes.
   */
  private Component footers = Component.empty();

  /**
   * Creates a new configuration manager.
   *
   * <p>This constructor automatically loads the configuration from the
   * plugin's config.yml file and applies the settings.
   *
   * @param instance the plugin instance, must not be null
   * @throws IllegalArgumentException if instance is null
   */
  public Codex(@NotNull DreaminTabList instance) {
    if (instance == null) throw new IllegalArgumentException("Plugin instance cannot be null");

    this.instance = instance;

    // Load configuration on creation
    refresh();
  }

  /**
   * Refreshes the configuration by reloading the file and applying changes.
   *
   * <p>This method performs a complete configuration reload:
   * <ol>
   *   <li>Reloads the configuration file from disk</li>
   *   <li>Parses all configuration values</li>
   *   <li>Applies changes to all online players</li>
   * </ol>
   *
   * <p>This method is safe to call at any time and will not cause
   * disruption to online players beyond the necessary updates.
   */
  public void refresh() {
    reloadConfigFile();
    initGlobal();

    instance.getLogger().info("Configuration refreshed successfully");
  }

  /**
   * Reloads the configuration file from disk.
   *
   * <p>This method uses Bukkit's built-in configuration reloading
   * mechanism to ensure the latest file contents are loaded.
   */
  public void reloadConfigFile() {
    this.instance.reloadConfig();
    this.config = this.instance.getConfig();

    instance.getLogger().fine("Configuration file reloaded");
  }

  /**
   * Initializes global configuration settings.
   *
   * <p>This method parses all configuration values from the loaded
   * configuration file and converts them to the appropriate internal
   * representations. It also applies the settings to all online players.
   */
  private void initGlobal() {
    // Load basic settings with defaults
    this.hideTab = this.config.getBoolean("hide-tab", false);
    this.hidePlayerJoin = this.config.getBoolean("hide-player-join", false);

    // Load header/footer settings
    this.headerFooterEnabled = this.config.getBoolean("header-footer.enabled", false);

    // Build header component from configuration
    buildHeaderComponent();

    // Build footer component from configuration
    buildFooterComponent();

    // Apply settings to all online players
    updateAllPlayer();

    instance.getLogger().fine("Global configuration initialized - hideTab: " + hideTab +
      ", hidePlayerJoin: " + hidePlayerJoin +
      ", headerFooterEnabled: " + headerFooterEnabled);
  }

  /**
   * Builds the header component from the configuration.
   *
   * <p>This method reads the header lines from the configuration and
   * combines them into a single Component with proper line breaks.
   * Legacy color codes are automatically converted to modern components.
   */
  private void buildHeaderComponent() {
    List<String> headerList = this.config.getStringList("header-footer.header");

    this.headers = Component.empty();
    for (int i = 0; i < headerList.size(); i++) {
      String value = headerList.get(i);

      // Convert legacy color codes to components
      Component line = LegacyComponentSerializer.legacySection().deserialize(value);
      this.headers = this.headers.append(line);

      // Add newline between lines (except for the last line)
      if (i < headerList.size() - 1) {
        this.headers = this.headers.append(Component.newline());
      }
    }

    instance.getLogger().fine("Header component built with " + headerList.size() + " lines");
  }

  /**
   * Builds the footer component from the configuration.
   *
   * <p>This method reads the footer lines from the configuration and
   * combines them into a single Component with proper line breaks.
   * Legacy color codes are automatically converted to modern components.
   */
  private void buildFooterComponent() {
    List<String> footerList = this.config.getStringList("header-footer.footer");

    this.footers = Component.empty();
    for (int i = 0; i < footerList.size(); i++) {
      String value = footerList.get(i);

      // Convert legacy color codes to components
      Component line = LegacyComponentSerializer.legacySection().deserialize(value);
      this.footers = this.footers.append(line);

      // Add newline between lines (except for the last line)
      if (i < footerList.size() - 1) {
        this.footers = this.footers.append(Component.newline());
      }
    }

    instance.getLogger().fine("Footer component built with " + footerList.size() + " lines");
  }

  /**
   * Applies configuration changes to all online players.
   *
   * <p>This method updates the tab list state for all currently online
   * players based on the current configuration settings. It handles:
   * <ul>
   *   <li>Tab visibility (hide/show)</li>
   *   <li>Header and footer display</li>
   * </ul>
   *
   * <p>If no players are online, this method does nothing.
   */
  private void updateAllPlayer() {
    // Check if there are any players to update
    if (DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().isEmpty()) {
      instance.getLogger().fine("No players online, skipping configuration update");
      return;
    }

    // Apply tab visibility setting
    if (this.hideTab) {
      DreaminTabList.getPlayerTabListManager().hideTabForAll();
      instance.getLogger().fine("Applied hide tab setting to all players");
    } else {
      DreaminTabList.getPlayerTabListManager().showTabForAll();
      instance.getLogger().fine("Applied show tab setting to all players");
    }

    // Apply header/footer setting
    if (this.headerFooterEnabled) {
      DreaminTabList.getPlayerTabListManager().setHeaderAndFooterForAll();
      instance.getLogger().fine("Applied header/footer to all players");
    } else {
      DreaminTabList.getPlayerTabListManager().removeHeaderAndFooterForAll();
      instance.getLogger().fine("Removed header/footer from all players");
    }
  }

  /**
   * Gets the configured header component.
   *
   * @return the header component, never null but may be empty
   */
  @NotNull
  public Component getHeaders() {
    return headers;
  }

  /**
   * Gets the configured footer component.
   *
   * @return the footer component, never null but may be empty
   */
  @NotNull
  public Component getFooters() {
    return footers;
  }
}

