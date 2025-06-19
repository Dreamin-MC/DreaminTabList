package fr.dreamin.dreaminTabList.event.core;

import lombok.Getter;
import org.bukkit.event.Cancellable;

@Getter
public abstract class DreaminTabListCancelEvent extends DreaminTabListEvent implements Cancellable {

  private boolean cancelled = false;

  public DreaminTabListCancelEvent() {
    super();
  }

  public DreaminTabListCancelEvent(boolean isAsync) {
    super(isAsync);
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

}
