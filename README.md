# DreaminTabList API

[![Version](https://img.shields.io/badge/version-0.0.2-blue.svg)](https://github.com/dreamin/dreamintablist)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.4-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/license-GPL-lightgrey.svg)](LICENSE)

A comprehensive and developer-friendly TabList management API for Minecraft servers. DreaminTabList provides advanced customization capabilities for player tab lists with a modern, well-documented API.

## 🌟 Features

### Core Functionality
- **Global Profile Management**: Create and manage fake players visible to all players
- **Player-Specific Customization**: Individual tab list customization per player
- **Tab Visibility Control**: Hide/show tab lists globally or per player
- **Custom Headers & Footers**: Rich text headers and footers with full formatting support
- **Skin Customization**: Custom skins from players or raw texture data
- **Profile Grouping**: Organize profiles with groups and custom sorting

### Advanced Features
- **Event-Driven Architecture**: Listen to tab list events for seamless integration
- **Dynamic Updates**: Real-time profile updates with efficient packet handling
- **Permission Integration**: Permission-based tab customization
- **Performance Optimized**: Efficient packet handling with minimal server impact
- **Thread-Safe**: Concurrent access support for multi-threaded environments
- **Backward Compatible**: Seamless integration with existing DreaminTabList setups

### Developer Experience
- **Comprehensive API**: Clean, intuitive interfaces following Java best practices
- **Complete Documentation**: Extensive Javadoc and usage guides
- **Rich Examples**: Basic and advanced usage examples included
- **Builder Pattern**: Fluent interface for easy profile creation
- **Error Handling**: Robust error handling with helpful error messages

## 🚀 Quick Start

### Installation

1. Download the latest DreaminTabList plugin
2. Download the latest PacketEvents plugin
3. Place it in your server's `plugins` folder
4. Restart your server
5. Add the API dependency to your project

#### Maven
```xml
<dependency>
    <groupId>fr.dreamin</groupId>
    <artifactId>dreamintablist-api</artifactId>
    <version>0.0.1</version>
    <scope>provided</scope>
</dependency>
```

#### Gradle
```gradle
dependencies {
    compileOnly 'fr.dreamin:dreamintablist-api:0.0.2'
}
```

### Basic Usage

```java
import fr.dreamin.dreaminTabList.api.TabListAPI;
import fr.dreamin.dreaminTabList.api.TabListAPIFactory;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import net.kyori.adventure.text.Component;

public class MyPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    // Get the API instance
    TabListAPI api = TabListAPIFactory.getAPI();

    // Create a fake player
    TabProfile serverBot = api.getProfileManager()
      .createProfile()
      .name("ServerBot")
      .displayName(Component.text("§c[BOT] §fServer Assistant"))
      .build();

    // Add to global profiles (visible to all players)
    api.getProfileManager().addGlobalProfile(serverBot);

    // Set global header and footer
    api.setHeaderAndFooterForAll(
      Component.text("My Server"),
      Component.text("Welcome to our community!")
    );
  }
}
```

## 📖 Documentation

### API Reference
- **[API Usage Guide](API_USAGE_GUIDE.md)** - Comprehensive guide with examples
- **[Javadoc Documentation](docs/javadoc/)** - Complete API reference
- **[Basic Examples](examples/basic/)** - Simple usage examples
- **[Advanced Examples](examples/advanced/)** - Complex integration examples

### Key Interfaces

#### TabListAPI
Main API interface providing access to all functionality:
```java
TabListAPI api = TabListAPIFactory.getAPI();
api.hideTabForAll();                    // Hide tab for all players
api.showTabForAll();                    // Show tab for all players
api.setHeaderAndFooterForAll(h, f);     // Set global header/footer
```

#### TabProfileManager
Manages global profiles visible to all players:
```java
TabProfileManager manager = api.getProfileManager();
TabProfile profile = manager.createProfile()
  .name("FakePlayer")
  .displayName(Component.text("§6Fake Player"))
  .build();
manager.addGlobalProfile(profile);
```

#### PlayerTabManager
Manages tab list for individual players:
```java
PlayerTabManager playerManager = api.getPlayerManager(player);
playerManager.hideTab();                // Hide tab for this player
playerManager.setHeaderAndFooter(h, f); // Set player-specific header/footer
```

#### TabProfile
Represents a tab list entry:
```java
TabProfile profile = manager.createProfile()
  .name("PlayerName")
  .displayName(Component.text("§aDisplay Name"))
  .gameMode(GameMode.CREATIVE)
  .latency(50)
  .skinFromPlayer("Notch")
  .group("staff")
  .sortOrder(-100)
  .build();
```

## 🎯 Examples

### Creating Fake Players

```java
// Simple fake player
TabProfile simple = profileManager.createProfile()
    .name("SimpleBot")
    .displayName(Component.text("§7[BOT] Simple"))
    .build();

// Advanced fake player with custom skin
TabProfile advanced = profileManager.createProfile()
  .name("AdvancedBot")
  .displayName(Component.text("§6[ADMIN] §fAdvanced Bot"))
  .gameMode(GameMode.CREATIVE)
  .latency(0)
  .skinFromPlayer("Notch")
  .group("staff")
  .sortOrder(-1000)
  .build();
```

### Player-Specific Customization

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabManager manager = api.getPlayerManager(player);
    
    if (player.hasPermission("server.vip")) {
        manager.setHeaderAndFooter(
            Component.text("§6§lVIP SERVER"),
            Component.text("§7Thank you for your support!")
        );
    }
}
```

### Dynamic Updates

```java
// Update server info every 30 seconds
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    TabProfile serverInfo = profileManager.findProfile("ServerInfo");
    if (serverInfo != null) {
        TabProfile updated = serverInfo.toBuilder()
                .displayName(Component.text("§6[INFO] §fPlayers: " + 
                           Bukkit.getOnlinePlayers().size()))
                .build();
        profileManager.updateGlobalProfile(updated);
    }
}, 0L, 600L);
```

## 🔧 Configuration

The plugin uses a `config.yml` file for basic settings:

```yaml
# Hide tab list for all players by default
hide-tab: false

# Hide new players when they join
hide-player-join: false

# Header and footer configuration
header-footer:
  enabled: true
  header:
    - "§6§lMy Server"
    - "§7Welcome to our community!"
  footer:
    - "§7Players online: %online%"
    - "§7Visit: §bwww.example.com"
```

## 🎮 Events

Listen to tab list events for advanced integration:

```java
@EventHandler
public void onPlayerTabJoin(PlayerTabJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabManager manager = event.getPlayerManager();
    
    // Customize based on player data
    customizePlayerTab(player, manager);
}

@EventHandler
public void onPlayerTabLeave(PlayerTabLeaveEvent event) {
    // Clean up player-specific data
    cleanupPlayerData(event.getPlayerUuid());
}
```

## 🏗️ Architecture

### Package Structure
```
fr.dreamin.dreaminTabList.api/
├── TabListAPI.java              # Main API interface
├── TabListAPIFactory.java       # API factory
├── profile/
│   ├── TabProfile.java          # Profile interface
│   ├── TabProfileBuilder.java   # Profile builder
│   └── TabProfileManager.java   # Profile manager
├── player/
│   └── PlayerTabManager.java    # Player-specific manager
├── events/
│   ├── TabListEvent.java        # Base event class
│   ├── PlayerTabJoinEvent.java  # Player join event
│   └── PlayerTabLeaveEvent.java # Player leave event
└── exceptions/
    ├── TabListException.java    # Base exception
    ├── InvalidProfileException.java
    └── PlayerNotFoundException.java
```

### Design Patterns
- **Factory Pattern**: `TabListAPIFactory` for API access
- **Builder Pattern**: `TabProfileBuilder` for profile creation
- **Observer Pattern**: Event system for notifications
- **Facade Pattern**: `TabListAPI` as main interface
- **Strategy Pattern**: Different profile types and behaviors

## 🔒 Thread Safety

The API is designed to be thread-safe:
- All public methods are safe for concurrent access
- Internal state is protected with appropriate synchronization
- Immutable objects where possible (TabProfile instances)
- Concurrent collections for internal storage

## ⚡ Performance

### Optimizations
- **Efficient Packet Handling**: Minimal packet creation and sending
- **Change Tracking**: Only send updates when necessary
- **Batch Operations**: Support for bulk profile operations
- **Caching**: Intelligent caching of frequently accessed data
- **Lazy Loading**: Load resources only when needed

### Best Practices
- Limit profile updates to once per second or less
- Use batch operations for multiple changes
- Cache profile instances instead of recreating
- Use async tasks for expensive operations

## 🐛 Troubleshooting

### Common Issues

**PlayerNotFoundException**
```java
try {
    PlayerTabManager manager = api.getPlayerManager(player);
} catch (PlayerNotFoundException e) {
    // Player not registered yet, wait for PlayerTabJoinEvent
}
```

**InvalidProfileException**
```java
try {
    TabProfile profile = builder.build();
} catch (InvalidProfileException e) {
    // Check required fields: name must be set and valid
}
```

**Skin Loading Fails**
```java
try {
  builder.skinFromPlayer("PlayerName");
} catch (Exception e) {
  // Network issue or invalid player name
  builder.defaultSkin(); // Use default skin instead
}
```

### Debug Mode
Enable debug logging for troubleshooting:
```java
plugin.getLogger().info("API enabled: " + api.isEnabled());
plugin.getLogger().info("Managed players: " + api.getManagedPlayerCount());
plugin.getLogger().info("Global profiles: " + profileManager.getGlobalProfileCount());
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup
1. Clone the repository
2. Import into your IDE
3. Run `./gradlew shadowJar` to build the project
4. Run tests with `./gradlew test`

### Code Style
- Follow Java naming conventions
- Add Javadoc for all public methods
- Include unit tests for new features
- Maintain backward compatibility

## 📄 License

This project is licensed under the GPL License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **PacketEvents** - For efficient packet handling
- **Adventure API** - For modern text components
- **Bukkit/Spigot** - For the plugin platform
- **Community** - For feedback and suggestions

## 📞 Support

- **Documentation**: [API Usage Guide](API_USAGE_GUIDE.md)
- **Examples**: [examples/](examples/)
- **Issues**: [GitHub Issues](https://github.com/dreamin/dreamintablist/issues)
- **Discord**: [Join our Discord](https://discord.gg/dreamin)

---

**Made with ❤️ by the Dreamin Studio**

