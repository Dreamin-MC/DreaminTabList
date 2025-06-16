# DreaminTabList API - Usage Guide

Welcome to the DreaminTabList API! This comprehensive guide will help you integrate and use the TabList API in your Minecraft plugins.

## Table of Contents

1. [Quick Start](#quick-start)
2. [API Overview](#api-overview)
3. [Basic Usage](#basic-usage)
4. [Advanced Features](#advanced-features)
5. [Best Practices](#best-practices)
6. [Examples](#examples)
7. [Troubleshooting](#troubleshooting)
8. [Migration Guide](#migration-guide)

## Quick Start

### 1. Add Dependency

**Gradle:**
```gradle
dependencies {
    compileOnly 'fr.dreamin:dreamintablist-api:1.0.0'
}
```

**Maven:**
```xml
<dependency>
    <groupId>fr.dreamin</groupId>
    <artifactId>dreamintablist-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### 2. Add Plugin Dependency

In your `plugin.yml`:
```yaml
name: YourPlugin
depend: [DreaminTabList]  # Required dependency
```

### 3. Basic Implementation

```java
public class YourPlugin extends JavaPlugin {
    private TabListAPI tabListAPI;
    
    @Override
    public void onEnable() {
        // Get API instance
        tabListAPI = TabListAPIFactory.getAPI();
        
        // Create a fake player
        TabProfile bot = tabListAPI.getProfileManager()
                .createProfile()
                .name("ServerBot")
                .displayName(Component.text("§c[BOT] §fAssistant"))
                .build();
        
        // Add to global tab list
        tabListAPI.getProfileManager().addGlobalProfile(bot);
        
        getLogger().info("TabList API integration successful!");
    }
}
```

## API Overview

### Core Components

The DreaminTabList API consists of several key components:

- **TabListAPI** - Main API entry point
- **TabProfileManager** - Manages global profiles (fake players)
- **PlayerTabManager** - Manages individual player's tab view
- **TabProfile** - Represents a profile in the tab list
- **TabProfileBuilder** - Builder pattern for creating profiles

### Architecture

```
TabListAPI
├── TabProfileManager (Global profiles)
│   ├── createProfile() → TabProfileBuilder
│   ├── addGlobalProfile()
│   ├── removeGlobalProfile()
│   └── findProfile()
└── PlayerTabManager (Per-player management)
    ├── addProfile() (player-specific)
    ├── hideTab() / showTab()
    ├── setHeaderAndFooter()
    └── getVisibleProfiles()
```

## Basic Usage

### Getting the API

```java
// Get API instance (after DreaminTabList is loaded)
TabListAPI api = TabListAPIFactory.getAPI();

// Check if API is available
if (TabListAPIFactory.isInitialized()) {
    TabListAPI api = TabListAPIFactory.getAPI();
    // Use API...
}
```

### Creating Profiles

#### Simple Profile
```java
TabProfile profile = api.getProfileManager()
        .createProfile()
        .name("SimpleBot")
        .build();
```

#### Detailed Profile
```java
TabProfile profile = api.getProfileManager()
        .createProfile()
        .name("DetailedBot")
        .displayName(Component.text("§a[VIP] §fBot"))
        .uuid(UUID.randomUUID())
        .gameMode(GameMode.CREATIVE)
        .latency(50)
        .group("staff")
        .sortOrder(10)
        .build();
```

#### Profile from Real Player
```java
Player player = // ... get player
TabProfile profile = api.getProfileManager()
        .createProfileFromPlayer(player);
```

### Managing Global Profiles

```java
TabProfileManager manager = api.getProfileManager();

// Add profile (visible to all players)
manager.addGlobalProfile(profile);

// Remove profile
manager.removeGlobalProfile(profile.getUniqueId());

// Update profile
profile.setDisplayName(Component.text("§b[UPDATED] §fBot"));
manager.updateGlobalProfile(profile);

// Find profiles
TabProfile found = manager.findProfile("SimpleBot");
Collection<TabProfile> staffProfiles = manager.findProfilesByGroup("staff");

// Clear all profiles
manager.clearGlobalProfiles();
```

### Managing Player-Specific Tabs

```java
Player player = // ... get player
PlayerTabManager playerManager = api.getPlayerManager(player);

// Hide/show tab for specific player
playerManager.hideTab();
playerManager.showTab();

// Add player-specific profile (only this player sees it)
TabProfile personalBot = api.getProfileManager()
        .createProfile()
        .name("PersonalAssistant")
        .displayName(Component.text("§d[PERSONAL] §fHelper"))
        .build();
playerManager.addProfile(personalBot);

// Set header and footer for specific player
playerManager.setHeaderAndFooter(
    Component.text("§6Welcome to the server!"),
    Component.text("§eEnjoy your stay!")
);

// Get what this player sees
Collection<TabProfile> visible = playerManager.getVisibleProfiles();
```

### Custom Skins

#### Using Player Skin
```java
TabProfile profile = api.getProfileManager()
        .createProfile()
        .name("BotWithSkin")
        .skinFromPlayer("Notch")  // Uses Notch's skin
        .build();
```

#### Using Custom Skin Data
```java
TabProfile profile = api.getProfileManager()
        .createProfile()
        .name("CustomSkinBot")
        .skinFromData(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMTIzIn19fQ==",
            "signature123"
        )
        .build();
```

## Advanced Features

### Event Handling

```java
@EventHandler
public void onPlayerTabJoin(PlayerTabJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabManager manager = event.getPlayerManager();
    
    // Customize tab for this player
    manager.setHeader(Component.text("§aWelcome " + player.getName() + "!"));
}

@EventHandler
public void onPlayerTabLeave(PlayerTabLeaveEvent event) {
    Player player = event.getPlayer();
    getLogger().info(player.getName() + " left the tab system");
}
```

### Conditional Profiles

```java
// Show different profiles based on player permissions
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    PlayerTabManager manager = api.getPlayerManager(player);
    
    if (player.hasPermission("vip.tablist")) {
        TabProfile vipBot = api.getProfileManager()
                .createProfile()
                .name("VIPAssistant")
                .displayName(Component.text("§6[VIP] §fAssistant"))
                .build();
        manager.addProfile(vipBot);
    }
}
```

### Dynamic Updates

```java
// Update profiles periodically
Bukkit.getScheduler().runTaskTimer(plugin, () -> {
    TabProfileManager manager = api.getProfileManager();
    
    // Update server info bot
    TabProfile serverInfo = manager.findProfile("ServerInfo");
    if (serverInfo != null) {
        int playerCount = Bukkit.getOnlinePlayers().size();
        serverInfo.setDisplayName(Component.text("§e[INFO] §fPlayers: " + playerCount));
        manager.updateGlobalProfile(serverInfo);
    }
}, 0L, 20L); // Update every second
```

### Sorting and Grouping

```java
// Create profiles with different sort orders
TabProfile admin = api.getProfileManager()
        .createProfile()
        .name("Admin")
        .group("staff")
        .sortOrder(1)  // Appears first
        .build();

TabProfile moderator = api.getProfileManager()
        .createProfile()
        .name("Moderator")
        .group("staff")
        .sortOrder(2)  // Appears second
        .build();

TabProfile player = api.getProfileManager()
        .createProfile()
        .name("Player")
        .group("players")
        .sortOrder(10) // Appears later
        .build();
```

### Bulk Operations

```java
TabProfileManager manager = api.getProfileManager();

// Clear specific group
manager.clearProfilesByGroup("temporary");

// Find and update multiple profiles
Collection<TabProfile> staffProfiles = manager.findProfiles(
    profile -> "staff".equals(profile.getGroup())
);

for (TabProfile profile : staffProfiles) {
    profile.setLatency(0); // Staff always shows 0 ping
    manager.updateGlobalProfile(profile);
}
```

## Best Practices

### 1. Resource Management

```java
// ✅ Good: Clean up on disable
@Override
public void onDisable() {
    if (TabListAPIFactory.isInitialized()) {
        TabProfileManager manager = TabListAPIFactory.getAPI().getProfileManager();
        
        // Remove your plugin's profiles
        manager.findProfilesByGroup("myplugin").forEach(profile -> 
            manager.removeGlobalProfile(profile.getUniqueId())
        );
    }
}
```

### 2. Error Handling

```java
// ✅ Good: Handle exceptions properly
try {
    PlayerTabManager manager = api.getPlayerManager(player);
    manager.hideTab();
} catch (PlayerNotFoundException e) {
    getLogger().warning("Player not found in TabList system: " + player.getName());
} catch (Exception e) {
    getLogger().severe("Failed to hide tab for player: " + e.getMessage());
}
```

### 3. Performance Considerations

```java
// ✅ Good: Batch operations
List<TabProfile> profilesToAdd = new ArrayList<>();
for (String botName : botNames) {
    TabProfile profile = api.getProfileManager()
            .createProfile()
            .name(botName)
            .build();
    profilesToAdd.add(profile);
}

// Add all at once instead of one by one
TabProfileManager manager = api.getProfileManager();
profilesToAdd.forEach(manager::addGlobalProfile);
```

### 4. Thread Safety

```java
// ✅ Good: Use main thread for API operations
if (!Bukkit.isPrimaryThread()) {
    Bukkit.getScheduler().runTask(plugin, () -> {
        // API operations here
        api.getProfileManager().addGlobalProfile(profile);
    });
} else {
    api.getProfileManager().addGlobalProfile(profile);
}
```

### 5. Naming Conventions

```java
// ✅ Good: Use consistent naming
TabProfile profile = api.getProfileManager()
        .createProfile()
        .name("MyPlugin_ServerBot")  // Prefix with plugin name
        .group("myplugin")           // Use plugin name as group
        .build();
```

## Examples

### Example 1: Server Information Display

```java
public class ServerInfoBot {
    private final TabListAPI api;
    private TabProfile infoProfile;
    
    public ServerInfoBot(TabListAPI api) {
        this.api = api;
        createInfoBot();
        startUpdating();
    }
    
    private void createInfoBot() {
        infoProfile = api.getProfileManager()
                .createProfile()
                .name("ServerInfo")
                .displayName(Component.text("§e[INFO] §fLoading..."))
                .gameMode(GameMode.SPECTATOR)
                .latency(0)
                .group("info")
                .sortOrder(1)
                .build();
        
        api.getProfileManager().addGlobalProfile(infoProfile);
    }
    
    private void startUpdating() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int players = Bukkit.getOnlinePlayers().size();
            int maxPlayers = Bukkit.getMaxPlayers();
            
            infoProfile.setDisplayName(Component.text(
                String.format("§e[INFO] §fPlayers: %d/%d", players, maxPlayers)
            ));
            
            api.getProfileManager().updateGlobalProfile(infoProfile);
        }, 0L, 20L);
    }
}
```

### Example 2: VIP Tab Features

```java
public class VIPTabFeatures implements Listener {
    private final TabListAPI api;
    
    public VIPTabFeatures(TabListAPI api) {
        this.api = api;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("vip.tablist")) {
            setupVIPTab(player);
        }
    }
    
    private void setupVIPTab(Player player) {
        PlayerTabManager manager = api.getPlayerManager(player);
        
        // Custom header/footer for VIPs
        manager.setHeaderAndFooter(
            Component.text("§6§l✦ VIP SERVER ✦"),
            Component.text("§eThank you for supporting us!")
        );
        
        // Add VIP-only bots
        TabProfile vipBot = api.getProfileManager()
                .createProfile()
                .name("VIP_Assistant")
                .displayName(Component.text("§6[VIP] §fPersonal Assistant"))
                .gameMode(GameMode.CREATIVE)
                .group("vip")
                .build();
        
        manager.addProfile(vipBot);
    }
}
```

### Example 3: Staff Management System

```java
public class StaffTabManager {
    private final TabListAPI api;
    private final Map<UUID, TabProfile> staffProfiles = new HashMap<>();
    
    public StaffTabManager(TabListAPI api) {
        this.api = api;
    }
    
    public void addStaffMember(String name, String rank, int priority) {
        TabProfile profile = api.getProfileManager()
                .createProfile()
                .name("Staff_" + name)
                .displayName(Component.text("§c[" + rank + "] §f" + name))
                .gameMode(GameMode.CREATIVE)
                .latency(0)
                .group("staff")
                .sortOrder(priority)
                .build();
        
        staffProfiles.put(profile.getUniqueId(), profile);
        api.getProfileManager().addGlobalProfile(profile);
    }
    
    public void removeStaffMember(String name) {
        staffProfiles.entrySet().removeIf(entry -> {
            TabProfile profile = entry.getValue();
            if (profile.getName().equals("Staff_" + name)) {
                api.getProfileManager().removeGlobalProfile(profile.getUniqueId());
                return true;
            }
            return false;
        });
    }
    
    public void updateStaffStatus(String name, boolean online) {
        staffProfiles.values().stream()
                .filter(profile -> profile.getName().equals("Staff_" + name))
                .findFirst()
                .ifPresent(profile -> {
                    String status = online ? "§a●" : "§c●";
                    String displayName = profile.getDisplayName().toString();
                    String baseName = displayName.replaceAll("§[ac]●\\s*", "");
                    
                    profile.setDisplayName(Component.text(status + " " + baseName));
                    api.getProfileManager().updateGlobalProfile(profile);
                });
    }
}
```

## Troubleshooting

### Common Issues

#### 1. API Not Available
```java
// Problem: TabListAPI is null
TabListAPI api = TabListAPIFactory.getAPI(); // Throws exception

// Solution: Check if initialized first
if (TabListAPIFactory.isInitialized()) {
    TabListAPI api = TabListAPIFactory.getAPI();
} else {
    getLogger().warning("DreaminTabList API not available!");
}
```

#### 2. Player Not Found
```java
// Problem: PlayerNotFoundException when getting manager
PlayerTabManager manager = api.getPlayerManager(player);

// Solution: Check if player is registered
try {
    PlayerTabManager manager = api.getPlayerManager(player);
} catch (PlayerNotFoundException e) {
    getLogger().warning("Player not in TabList system: " + player.getName());
}
```

#### 3. Profiles Not Visible
```java
// Problem: Added profiles don't appear in tab
api.getProfileManager().addGlobalProfile(profile);

// Check: Is tab hidden?
PlayerTabManager manager = api.getPlayerManager(player);
if (manager.isTabHidden()) {
    manager.showTab(); // Show tab first
}
```

#### 4. Memory Leaks
```java
// Problem: Profiles not cleaned up
// Solution: Remove profiles on plugin disable
@Override
public void onDisable() {
    if (TabListAPIFactory.isInitialized()) {
        api.getProfileManager().clearProfilesByGroup("myplugin");
    }
}
```

### Debug Information

```java
// Get debug information
TabListAPI api = TabListAPIFactory.getAPI();
TabProfileManager manager = api.getProfileManager();

getLogger().info("Global profiles: " + manager.getGlobalProfileCount());
getLogger().info("Managed players: " + api.getManagedPlayerCount());

// Per-player debug
PlayerTabManager playerManager = api.getPlayerManager(player);
getLogger().info("Visible profiles for " + player.getName() + ": " + 
                playerManager.getVisibleProfiles().size());
getLogger().info("Tab hidden: " + playerManager.isTabHidden());
```

### Performance Monitoring

```java
// Monitor API performance
long startTime = System.nanoTime();
api.getProfileManager().addGlobalProfile(profile);
long duration = System.nanoTime() - startTime;

if (duration > 1_000_000) { // More than 1ms
    getLogger().warning("Slow API operation: " + (duration / 1_000_000) + "ms");
}
```

## Migration Guide

### From Legacy System

If you were using the legacy DreaminTabList system:

#### Old Way (Legacy)
```java
// Legacy approach
PlayerTabList playerTabList = DreaminTabList.getPlayerTabListManager().getPlayer(player);
playerTabList.getTabList().hideTab();
```

#### New Way (API)
```java
// Modern API approach
PlayerTabManager manager = TabListAPIFactory.getAPI().getPlayerManager(player);
manager.hideTab();
```

### Migration Steps

1. **Update Dependencies**: Change to use the API dependency
2. **Update Plugin.yml**: Add DreaminTabList as dependency
3. **Replace Legacy Calls**: Use new API methods
4. **Add Error Handling**: Use try-catch for API exceptions
5. **Test Thoroughly**: Ensure all functionality works

### Compatibility

- **Backward Compatible**: Legacy system still works alongside API
- **Gradual Migration**: You can migrate features one by one
- **No Breaking Changes**: Existing plugins continue to work

---

## Support

- **Documentation**: Check this guide and Javadoc
- **Issues**: Report bugs on GitHub
- **Community**: Join discussions for help and tips

For more examples and advanced usage, check the `examples/` directory in the project repository.

Happy coding! 🎮

