package fr.dreamin.dreaminTabList.cmd;

import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.player.core.PlayerTabList;
import fr.dreamin.dreaminTabList.player.tab.TabListProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestCmd implements CommandExecutor, TabExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    Player player = (Player) sender;

    PlayerTabList playerTabList = DreaminTabList.getPlayerTabListManager().getPlayer(player);

    TabListProfile profile = playerTabList.getTabList().getGlobalCache().getAll()
      .values()
      .stream()
      .filter(p -> p.getName().equals("ScravenPro")) // ou autre logique
      .findFirst()
      .orElse(null);

    switch (args[0]) {
      case "skin" -> {
        profile.addSkin("_Loomi_");
        playerTabList.getTabList().updatePlayer(profile);
      }
      case "name" -> {
        profile.setDisplayName(Component.text("_Loomi_"));
        playerTabList.getTabList().updatePlayer(profile);
      }
      case "hat" -> {
        profile.setShowHat(false);
        playerTabList.getTabList().updatePlayer(profile);
      }
    }


    return false;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    return List.of();
  }
}
