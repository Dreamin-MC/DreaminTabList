package fr.dreamin.dreaminTabList.player.tab;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TabListCache {

  private final Map<UUID, TabListProfile> globalEntries = new HashMap<>();

  public void add(TabListProfile profile) {
    this.globalEntries.put(profile.getUuid(), profile);
  }

  public void remove(UUID uuid) {
    this.globalEntries.remove(uuid);
  }

  public @Nullable TabListProfile get(UUID uuid) {
    return this.globalEntries.get(uuid);
  }

  public @Nullable TabListProfile get(String name) {
    return this.globalEntries.values()
      .stream()
      .filter(p -> p.getName().equals(name)) // ou autre logique
      .findFirst()
      .orElse(null);
  }

  public Map<UUID, TabListProfile> getAll() {
    return new HashMap<>(this.globalEntries);
  }

}
