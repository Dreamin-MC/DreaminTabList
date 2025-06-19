package fr.dreamin.dreaminTabList.event.packet;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import fr.dreamin.dreaminTabList.DreaminTabList;
import fr.dreamin.dreaminTabList.event.custom.playerUpdate.PacketPlayerUpdateEvent;

public class PacketEvent implements PacketListener {

  @Override
  public void onPacketSend(PacketSendEvent event) {
    if (event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_UPDATE)) {
      WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
      PacketPlayerUpdateEvent packetEvent = new PacketPlayerUpdateEvent(event.getUser().getUUID(), packet);
      DreaminTabList.getInstance().callEvent(packetEvent);
      if (packetEvent.isCancelled()) event.setCancelled(true);
      packet.setActions(packetEvent.getActions());
      packet.setEntries(packetEvent.getPlayerInfos());
    }
  }
}
