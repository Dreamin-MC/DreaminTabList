package fr.dreamin.dreaminTabList.cmd;

import fr.dreamin.dreaminTabList.DreaminTabList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DreaminTabListCmd implements CommandExecutor, TabExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    Player player = (Player) sender;

    switch (args[0]) {
      case "reload" -> {
        player.sendMessage(Component.text("Reload en cours"));
        DreaminTabList.getCodex().refresh();
        player.sendMessage(Component.text("Reload effectué"));
      }
      default -> throw new IllegalStateException("Unexpected value: " + args[0]);
    }

    return false;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    List<String> result = new ArrayList<>();

    Player player = (Player) sender;

    if (player.isOp()) result.add("reload");

    return result;
  }
}
