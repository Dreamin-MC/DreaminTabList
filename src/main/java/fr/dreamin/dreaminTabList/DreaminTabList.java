package fr.dreamin.dreaminTabList;

import com.github.retrooper.packetevents.PacketEvents;
import fr.dreamin.api.cmd.SimpleCommand;
import fr.dreamin.dreaminTabList.cmd.DreaminTabListCmd;
import fr.dreamin.dreaminTabList.cmd.TestCmd;
import fr.dreamin.dreaminTabList.config.Codex;
import fr.dreamin.dreaminTabList.event.player.PlayerEvent;
import fr.dreamin.dreaminTabList.player.core.PlayerTabListManager;
import fr.dreamin.mctools.McTools;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DreaminTabList extends JavaPlugin {

  @Getter private static DreaminTabList instance;
  @Getter private static Codex codex;
  @Getter private static PlayerTabListManager playerTabListManager;

  @Override
  public void onLoad() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
    PacketEvents.getAPI().load();
  }

  @Override
  public void onEnable() {
    instance = this;
    playerTabListManager = new PlayerTabListManager();

    McTools.setInstance(this);

    PacketEvents.getAPI().init();

    saveDefaultConfig();
    codex = new Codex(this);

    getServer().getPluginManager().registerEvents(new PlayerEvent(), this);

    loadCommands();
  }

  @Override
  public void onDisable() {
    PacketEvents.getAPI().terminate();
  }

  // #################################################################
  // ---------------------- PUBLIC METHOD ----------------------------
  // #################################################################

  public void callEvent(Event event) {
    Bukkit.getPluginManager().callEvent(event);
  }

  // #################################################################
  // ---------------------- PRIVATE METHOD ---------------------------
  // #################################################################

  private void loadCommands() {
    SimpleCommand.createCommand("test", new TestCmd());
    SimpleCommand.createCommand("dreamintablist", new DreaminTabListCmd());
  }

}
