package org.itxtech.synapseapi.event.player;

import cn.nukkit.event.HandlerList;
import cn.nukkit.network.SourceInterface;
import org.itxtech.synapseapi.SynapsePlayer;
import org.itxtech.synapseapi.event.SynapseEvent;

import java.net.InetSocketAddress;

/**
 */
public class SynapsePlayerCreationEvent extends SynapseEvent {

    private static final HandlerList handlers = new HandlerList();
    private SourceInterface interfaz;
    private Long clientId;
    private InetSocketAddress address;
    private Class<? extends SynapsePlayer> baseClass;
    private Class<? extends SynapsePlayer> playerClass;

    public SynapsePlayerCreationEvent(SourceInterface interfaz, Class<? extends SynapsePlayer> baseClass, Class<? extends SynapsePlayer> playerClass, Long clientId, InetSocketAddress address) {
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.address = address;

        this.baseClass = baseClass;
        this.playerClass = playerClass;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public SourceInterface getInterface() {
        return interfaz;
    }

    public String getAddress() {
        return this.address.getAddress().getHostAddress();
    }

    public int getPort() {
        return this.address.getPort();
    }

    public Long getClientId() {
        return clientId;
    }

    public Class<? extends SynapsePlayer> getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(Class<? extends SynapsePlayer> baseClass) {
        this.baseClass = baseClass;
    }

    public Class<? extends SynapsePlayer> getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(Class<? extends SynapsePlayer> playerClass) {
        this.playerClass = playerClass;
    }
}
