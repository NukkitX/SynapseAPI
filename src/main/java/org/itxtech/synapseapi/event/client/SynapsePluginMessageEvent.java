package org.itxtech.synapseapi.event.client;

import org.itxtech.synapseapi.SynapseEntry;
import org.itxtech.synapseapi.event.SynapseEvent;

/**
 * @author KCodeYT
 */
public class SynapsePluginMessageEvent extends SynapseEvent {

    private final SynapseEntry entry;
    private final String channel;
    private final byte[] data;

    public SynapsePluginMessageEvent(SynapseEntry entry, String channel, byte[] data) {
        this.entry = entry;
        this.channel = channel;
        this.data = data;
    }

    public SynapseEntry getEntry() {
        return entry;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

}
