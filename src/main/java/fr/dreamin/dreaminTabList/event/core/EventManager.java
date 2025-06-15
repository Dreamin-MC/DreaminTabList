package fr.dreamin.dreaminTabList.event.core;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EventManager {

  <T extends DreaminTabListEvent> void registerListener(@NotNull Plugin plugin, @NotNull Class<T> eventClass, @NotNull Consumer<T> listener);

  void unregisterListeners(@NotNull Plugin plugin);

  @NotNull
  <T extends DreaminTabListEvent> T callEvent(@NotNull T event);

  @NotNull
  <T extends Event> T callBukkitEvent(@NotNull T event);

}
