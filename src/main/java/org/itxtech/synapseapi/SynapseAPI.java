package org.itxtech.synapseapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import org.itxtech.synapseapi.messaging.Messenger;
import org.itxtech.synapseapi.messaging.StandardMessenger;
import org.itxtech.synapseapi.network.protocol.mcpe.SetHealthPacket;
import org.itxtech.synapseapi.utils.DataPacketEidReplacer;

import java.util.*;

/**
 * @author boybook
 */
public class SynapseAPI extends PluginBase implements Listener {

    public static boolean enable = true;
    private static SynapseAPI instance;
    private boolean autoConnect = true;
    private Map<String, SynapseEntry> synapseEntries = new HashMap<>();
    private Messenger messenger;

    public static SynapseAPI getInstance() {
        return instance;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getNetwork().registerPacket(ProtocolInfo.SET_HEALTH_PACKET, SetHealthPacket.class);
        this.messenger = new StandardMessenger();
        loadEntries();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public Map<String, SynapseEntry> getSynapseEntries() {
        return synapseEntries;
    }

    public void addSynapseAPI(SynapseEntry entry) {
        this.synapseEntries.put(entry.getHash(), entry);
    }

    public SynapseEntry getSynapseEntry(String hash) {
        return this.synapseEntries.get(hash);
    }

    public void shutdownAll() {
        for (SynapseEntry entry : new ArrayList<>(this.synapseEntries.values())) {
            entry.shutdown();
        }
    }

    @Override
    public void onDisable() {
        this.shutdownAll();
    }

    public DataPacket getPacket(byte[] buffer) {
        byte pid = buffer[0] == (byte) 0xfe ? (byte) 0xff : buffer[0];

        byte start = 1;
        DataPacket data;
        data = this.getServer().getNetwork().getPacket(pid);

        if (data == null) {
            Server.getInstance().getLogger().notice("C => S Unknown packet with PID 0x" + String.format("%02x", pid));
            return null;
        }
        data.setBuffer(buffer, start);
        return data;
    }

    private void loadEntries() {
        this.saveDefaultConfig();
        enable = this.getConfig().getBoolean("enable", true);

        if (!enable) {
            this.getLogger().warning("The SynapseAPI is not be enabled!");
        } else {
            if (this.getConfig().getBoolean("disable-rak")) {
                for (SourceInterface sourceInterface : this.getServer().getNetwork().getInterfaces()) {
                    if (sourceInterface instanceof RakNetInterface) {
                        sourceInterface.shutdown();
                    }
                }
            }

            List entries = this.getConfig().getList("entries");

            for (Object entry : entries) {
                @SuppressWarnings("unchecked")
                ConfigSection section = new ConfigSection((LinkedHashMap) entry);
                String serverIp = section.getString("server-ip", "127.0.0.1");
                int port = section.getInt("server-port", 10305);
                boolean isMainServer = section.getBoolean("isMainServer");
                boolean isLobbyServer = section.getBoolean("isLobbyServer");
                boolean transfer = section.getBoolean("transferOnShutdown", true);
                String password = section.getString("password");
                String serverDescription = section.getString("description");
                this.autoConnect = section.getBoolean("autoConnect", true);
                if (this.autoConnect) {
                    this.addSynapseAPI(new SynapseEntry(this, serverIp, port, isMainServer, isLobbyServer, transfer, password, serverDescription));
                }
            }
        }
    }

    public Messenger getMessenger() {
        return messenger;
    }

    @EventHandler
    public void onBatchPackets(BatchPacketsEvent e) {
        DataPacket[] packets = e.getPackets();

        for (Player player : e.getPlayers()) {
            if (player instanceof SynapsePlayer) {
                for (DataPacket pk : packets) {
                    player.dataPacket(DataPacketEidReplacer.replace(pk, player.getId(), SynapsePlayer.REPLACE_ID));
                }
            } else {
                for (DataPacket pk : packets) {
                    player.dataPacket(pk);
                }
            }
        }
//        Player[] players = e.getPlayers();
//        DataPacket[] packets = e.getPackets();
//
//        List<SynapsePlayer> sp = new LinkedList<>();
//        List<Player> np = new LinkedList<>();
//
//        for (Player p : players) {
//            if (p instanceof SynapsePlayer) {
//                sp.add((SynapsePlayer) p);
//            } else {
//                np.add(p);
//            }
//        }
//
//        if (sp.isEmpty()) {
//            return;
//        }
//
//        if (!np.isEmpty()) {
//            getServer().batchPackets(np.toArray(new Player[0]), packets);
//        }
//
//        e.setCancelled();
//
////        Map<DataPacket, List<Player>> unchanged = new HashMap<>();
////        Map<SynapsePlayer, DataPacket[]> map = new HashMap<>();
////
////        for (SynapsePlayer p : sp) {
////            Map<SynapsePlayer, DataPacket[]> playerPackets = map.computeIfAbsent(p.getSynapseEntry(), k -> new HashMap<>());
////
////            DataPacket[] replaced = Arrays.stream(packets)
////                    .map(packet -> DataPacketEidReplacer.replace(packet, p.getId(), SynapsePlayer.REPLACE_ID))
////                    .toArray(DataPacket[]::new);
////
////            playerPackets.put(p, replaced);
////        }
//
//        for (SynapsePlayer p : sp) {
////                SynapsePlayer p = playerEntry.getKey();
//
//            for (DataPacket pk : packets) {
//                pk = DataPacketEidReplacer.replace(pk, p.getId(), SynapsePlayer.REPLACE_ID);
//
//                    if (!pk.isEncoded) {
//                        pk.encode();
//                        pk.isEncoded = true;
//                    }
//
//                    if (pk instanceof MoveEntityAbsolutePacket) {
//                        MainLogger.getLogger().info("Sending movement of entity(" + ((MoveEntityAbsolutePacket) pk).eid + ") to player '" + p.getName() + "' (" + p.getId() + ")");
//                    }
//
//                    p.getInterface().putPacket(p, pk);
//                }
//            }
    }
}
