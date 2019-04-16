package org.itxtech.synapseapi.utils;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.MainLogger;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

/**
 * DataPacketEidReplacer
 * ===============
 * author: boybook
 * EaseCation Network Project
 * codefuncore
 * ===============
 */
public class DataPacketEidReplacer {

    private static final Set<Integer> replaceMetadata = Sets.newHashSet(Entity.DATA_OWNER_EID, Entity.DATA_LEAD_HOLDER_EID, Entity.DATA_TRADING_PLAYER_EID, Entity.DATA_TARGET_EID);

    public static DataPacket replace(DataPacket pk, long from, long to) {
        DataPacket packet = pk.clone();
        boolean change = true;

        switch (packet.pid()) {
            case ProtocolInfo.ADD_PLAYER_PACKET:
                AddPlayerPacket app = (AddPlayerPacket) packet;

                app.metadata = replaceMetadata(app.metadata, from, to);
                break;
            case ProtocolInfo.ADD_ENTITY_PACKET:
                AddEntityPacket aep = (AddEntityPacket) packet;

                aep.metadata = replaceMetadata(aep.metadata, from, to);
                break;
            case ProtocolInfo.ADD_ITEM_ENTITY_PACKET:
                AddItemEntityPacket aiep = (AddItemEntityPacket) packet;

                aiep.metadata = replaceMetadata(aiep.metadata, from, to);
                break;
            case ProtocolInfo.ANIMATE_PACKET:
                if (((AnimatePacket) packet).eid == from) ((AnimatePacket) packet).eid = to;
                break;
            case ProtocolInfo.TAKE_ITEM_ENTITY_PACKET:
                if (((TakeItemEntityPacket) packet).entityId == from) ((TakeItemEntityPacket) packet).entityId = to;
                break;
            case ProtocolInfo.SET_ENTITY_MOTION_PACKET:
                if (((SetEntityMotionPacket) packet).eid == from) ((SetEntityMotionPacket) packet).eid = to;
                break;
            case ProtocolInfo.SET_ENTITY_LINK_PACKET:
                SetEntityLinkPacket selp = (SetEntityLinkPacket) packet;

                if (selp.vehicleUniqueId == from) selp.vehicleUniqueId = to;
                if (selp.riderUniqueId == from) selp.riderUniqueId = to;
                break;
            case ProtocolInfo.SET_ENTITY_DATA_PACKET:
                SetEntityDataPacket sedp = (SetEntityDataPacket) packet;

                if (sedp.eid == from) sedp.eid = to;
                sedp.metadata = replaceMetadata(sedp.metadata, from, to);
                break;
            case ProtocolInfo.UPDATE_ATTRIBUTES_PACKET:
                if (((UpdateAttributesPacket) packet).entityId == from) ((UpdateAttributesPacket) packet).entityId = to;
                break;
            case ProtocolInfo.ENTITY_EVENT_PACKET:
                if (((EntityEventPacket) packet).eid == from) ((EntityEventPacket) packet).eid = to;
                break;
            case ProtocolInfo.MOVE_PLAYER_PACKET:
                if (((MovePlayerPacket) packet).eid == from) ((MovePlayerPacket) packet).eid = to;
                break;
            case ProtocolInfo.MOB_EQUIPMENT_PACKET:
                if (((MobEquipmentPacket) packet).eid == from) ((MobEquipmentPacket) packet).eid = to;
                break;
            case ProtocolInfo.MOB_EFFECT_PACKET:
                if (((MobEffectPacket) packet).eid == from) ((MobEffectPacket) packet).eid = to;
                break;
            case ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET:
                if (((MoveEntityAbsolutePacket) packet).eid == from) ((MoveEntityAbsolutePacket) packet).eid = to;
                break;
            case ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET:
                if (((MobArmorEquipmentPacket) packet).eid == from) ((MobArmorEquipmentPacket) packet).eid = to;
                break;
            case ProtocolInfo.PLAYER_LIST_PACKET:
                Arrays.stream(((PlayerListPacket) packet).entries).filter(entry -> entry.entityId == from).forEach(entry -> entry.entityId = to);
                break;
            case ProtocolInfo.BOSS_EVENT_PACKET:
                if (((BossEventPacket) packet).bossEid == from) ((BossEventPacket) packet).bossEid = to;
                break;
            case ProtocolInfo.ADVENTURE_SETTINGS_PACKET:
                if (((AdventureSettingsPacket) packet).entityUniqueId == from) ((AdventureSettingsPacket) packet).entityUniqueId = to;
                break;
            case ProtocolInfo.UPDATE_EQUIPMENT_PACKET:
                if (((UpdateEquipmentPacket) packet).eid == from) ((UpdateEquipmentPacket) packet).eid = to;
                break;
            default:
                change = false;
        }

        if (change) {
            packet.isEncoded = false;
        }

        return packet;
    }

    private static EntityMetadata replaceMetadata(EntityMetadata data, long from, long to) {
        boolean changed = false;

        for (Integer key : replaceMetadata) {
            try {
                @SuppressWarnings("rawtypes")
                EntityData ed = data.get(key);

                if (ed == null) {
                    continue;
                }

                if (ed.getType() != Entity.DATA_TYPE_LONG) {
                    MainLogger.getLogger().info("Wrong entity data type (" + key + ") expected 'Long' got '" + dataTypeToString(ed.getType()) + "'");
                    continue;
                }

                long value = ((LongEntityData) ed).getData();

                if (value == from) {
                    if (!changed) {
                        data = cloneMetadata(data);
                        changed = true;
                    }

                    data.putLong(key, to);
                }
            } catch (Exception e) {
                MainLogger.getLogger().error("Exception while replacing metadata '" + key + "'", e);
            }
        }

        return data;
    }

    @SuppressWarnings("rawtypes")
    private static EntityMetadata cloneMetadata(EntityMetadata data) {
        EntityMetadata newData = new EntityMetadata();

        for (EntityData value : data.getMap().values()) {
            newData.put(value);
        }

        return newData;
    }

    private static String dataTypeToString(int type) {
        switch (type) {
            case Entity.DATA_TYPE_BYTE:
                return "Byte";
            case Entity.DATA_TYPE_SHORT:
                return "Short";
            case Entity.DATA_TYPE_INT:
                return "Int";
            case Entity.DATA_TYPE_FLOAT:
                return "Float";
            case Entity.DATA_TYPE_STRING:
                return "String";
            case Entity.DATA_TYPE_SLOT:
                return "Slot";
            case Entity.DATA_TYPE_POS:
                return "Pos";
            case Entity.DATA_TYPE_LONG:
                return "Long";
            case Entity.DATA_TYPE_VECTOR3F:
                return "Vector3f";
        }

        return "Unknown";
    }
}
