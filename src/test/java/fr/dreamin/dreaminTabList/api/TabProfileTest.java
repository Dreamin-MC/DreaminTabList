package fr.dreamin.dreaminTabList.api;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import fr.dreamin.dreaminTabList.api.profile.TabProfile;
import fr.dreamin.dreaminTabList.api.profile.TabProfileBuilder;
import fr.dreamin.dreaminTabList.impl.profile.TabProfileBuilderImpl;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TabProfile API functionality.
 *
 * <p>This test class validates the core functionality of the TabProfile
 * system, including profile creation, validation, and builder patterns.
 *
 * @author DreaminTabList Tests
 * @version 1.0.0
 */
public class TabProfileTest {

  private TabProfileBuilder builder;

  @BeforeEach
  public void setUp() {
    builder = new TabProfileBuilderImpl();
  }

  @Test
  public void testBasicProfileCreation() {
    // Test creating a basic profile
    TabProfile profile = builder
      .name("TestPlayer")
      .displayName(Component.text("Test Player"))
      .gameMode(GameMode.SURVIVAL)
      .latency(50)
      .build();

    assertNotNull(profile);
    assertEquals("TestPlayer", profile.getName());
    assertEquals(GameMode.SURVIVAL, profile.getGameMode());
    assertEquals(50, profile.getLatency());
    assertTrue(profile.isListed());
    assertNotNull(profile.getUniqueId());
  }

  @Test
  public void testProfileWithCustomUUID() {
    UUID customUUID = UUID.randomUUID();

    TabProfile profile = builder
      .name("TestPlayer")
      .uuid(customUUID)
      .build();

    assertEquals(customUUID, profile.getUniqueId());
  }

  @Test
  public void testProfileValidation() {
    // Test that empty name throws exception
    assertThrows(IllegalStateException.class, () -> {
      builder.build(); // No name set
    });

    // Test that invalid name throws exception
    assertThrows(IllegalArgumentException.class, () -> {
      builder.name(""); // Empty name
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.name("ab"); // Too short (less than 3 characters)
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.name("this_name_is_way_too_long_for_minecraft"); // Too long
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.name("invalid-name"); // Contains invalid characters
    });
  }

  @Test
  public void testProfileBuilder() {
    // Test builder pattern and method chaining
    TabProfile profile = builder
      .name("BuilderTest")
      .displayName(Component.text("§6Builder Test"))
      .gameMode(GameMode.CREATIVE)
      .latency(25)
      .listed(false)
      .sortOrder(100)
      .showHat(false)
      .group("test")
      .build();

    assertEquals("BuilderTest", profile.getName());
    assertEquals(GameMode.CREATIVE, profile.getGameMode());
    assertEquals(25, profile.getLatency());
    assertFalse(profile.isListed());
    assertEquals(100, profile.getSortOrder());
    assertFalse(profile.isShowHat());
    assertEquals("test", profile.getGroup());
  }

  @Test
  public void testProfileToBuilder() {
    // Create a profile
    TabProfile original = builder
      .name("OriginalPlayer")
      .displayName(Component.text("Original"))
      .gameMode(GameMode.ADVENTURE)
      .latency(75)
      .build();

    // Create a modified version using toBuilder()
    TabProfile modified = original.toBuilder()
      .displayName(Component.text("Modified"))
      .latency(100)
      .build();

    // Original should be unchanged
    assertEquals("OriginalPlayer", original.getName());
    assertEquals(75, original.getLatency());

    // Modified should have changes
    assertEquals("OriginalPlayer", modified.getName()); // Name should be same
    assertEquals(100, modified.getLatency()); // Latency should be changed
    assertEquals(original.getUniqueId(), modified.getUniqueId()); // UUID should be same
  }

  @Test
  public void testProfileDefaults() {
    // Test that default values are applied correctly
    TabProfile profile = builder
      .name("DefaultTest")
      .build();

    assertEquals(GameMode.SURVIVAL, profile.getGameMode());
    assertEquals(0, profile.getLatency());
    assertTrue(profile.isListed());
    assertEquals(0, profile.getSortOrder());
    assertTrue(profile.isShowHat());
    assertNull(profile.getGroup());
    assertFalse(profile.isRealPlayer());
    assertFalse(profile.isVanillaSynced());
  }

  @Test
  public void testProfileEquality() {
    UUID uuid = UUID.randomUUID();

    TabProfile profile1 = builder
      .name("TestPlayer")
      .uuid(uuid)
      .build();

    TabProfile profile2 = new TabProfileBuilderImpl()
      .name("DifferentName")
      .uuid(uuid) // Same UUID
      .build();

    // Profiles with same UUID should be equal
    assertEquals(profile1, profile2);
    assertEquals(profile1.hashCode(), profile2.hashCode());
  }

  @Test
  public void testNullValidation() {
    // Test null parameter validation
    assertThrows(IllegalArgumentException.class, () -> {
      builder.name(null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.displayName((Component) null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.gameMode(null);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      builder.uuid(null);
    });
  }

  @Test
  public void testLatencyValidation() {
    // Test that negative latency throws exception
    assertThrows(IllegalArgumentException.class, () -> {
      builder.latency(-1);
    });

    // Test that zero and positive latency work
    assertDoesNotThrow(() -> {
      builder.name("TestPlayer").latency(0).build();
    });

    assertDoesNotThrow(() -> {
      builder.name("TestPlayer").latency(999).build();
    });
  }

  @Test
  public void testBuilderReset() {
    // Configure builder
    builder.name("TestPlayer")
      .displayName(Component.text("Test"))
      .gameMode(GameMode.CREATIVE);

    // Reset builder
    builder.reset();

    // Should throw exception because name is required
    assertThrows(IllegalStateException.class, () -> {
      builder.build();
    });
  }

  @Test
  public void testStringDisplayName() {
    // Test setting display name from string
    TabProfile profile = builder
      .name("TestPlayer")
      .displayName("§6Test Player")
      .build();

    assertNotNull(profile.getDisplayName());
    // The display name should be converted to a Component
  }
}

