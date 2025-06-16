package fr.dreamin.dreaminTabList.config;

import fr.dreamin.dreaminTabList.DreaminTabList;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class Codex {

  private final DreaminTabList instance;
  private FileConfiguration config;

  private boolean hideTab, hidePlayerJoin;

  private boolean headerFooterEnabled;
  private Component headers = Component.empty(), footers = Component.empty();

  public Codex(DreaminTabList instance) {
    this.instance = instance;

    refresh();
  }

  // #################################################################
  // ---------------------- PUBLIC METHOD ----------------------------
  // #################################################################

  public void refresh() {
    reloadConfigFile();

    initGlobal();
  }

  public void reloadConfigFile() {
    this.instance.reloadConfig();
    this.config = this.instance.getConfig();
  }

  // #################################################################
  // ---------------------- PRIVATE METHOD ---------------------------
  // #################################################################

  private void initGlobal() {
    this.hideTab = this.config.getBoolean("hide-tab", false);
    this.hidePlayerJoin = this.config.getBoolean("hide-player-join", false);

    this.headerFooterEnabled = this.config.getBoolean("header-footer.enabled", false);

    List<String> headerList = this.config.getStringList("header-footer.header");

    this.headers = Component.empty();
    for (int i = 0; i < headerList.size(); i ++) {
      String value = headerList.get(i);
      this.headers = this.headers.append(Component.text(value));
      if (i < headerList.size() - 1) this.headers = this.headers.append(Component.newline());
    }

    List<String> footerList = this.config.getStringList("header-footer.footer");

    this.footers = Component.empty();
    for (int i = 0; i < footerList.size(); i ++) {
      String value = footerList.get(i);
      this.footers = this.footers.append(Component.text(value));
      if (i < footerList.size() - 1) this.footers = this.footers.append(Component.newline());
    }

    updateAllPlayer();
  }

  private void updateAllPlayer() {
    if (DreaminTabList.getPlayerTabListManager().getPlayerTabListSet().isEmpty()) return;

    if (this.hideTab)  DreaminTabList.getPlayerTabListManager().hideTabForAll();
    else DreaminTabList.getPlayerTabListManager().showTabForAll();

    if (this.headerFooterEnabled) DreaminTabList.getPlayerTabListManager().setHeaderAndFooterForAll();
    else DreaminTabList.getPlayerTabListManager().removeHeaderAndFooterForAll();
  }

}
