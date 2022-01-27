package com.benchion.sockets.resolver;

import com.benchion.sockets.packet.BenchionPacket;
import com.benchion.sockets.packet.BenchionPackets;
import com.benchion.sockets.packet.PacketContext;
import com.benchion.sockets.packet.exceptions.IllegalPacket;
import com.benchion.sockets.resolver.exceptions.IllegalPacketFormat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;

/**
 * Resolver class that resolve incoming packets
 */
public final class RawPacketResolver {
    private final int id;
    private final HashMap<String, Object> data;

    /**
     * @param str Raw Packet Data
     * @throws IllegalPacketFormat
     */
    public RawPacketResolver(String str) throws IllegalPacketFormat {
        JsonObject content = JsonParser.parseString(decode(str)).getAsJsonObject();
        if (!content.has("packet_id")) throw new IllegalPacketFormat("The packet id is not specified!");

        this.id = content.get("packet_id").getAsInt();

        if (!content.has("data")) {
            this.data = new HashMap<>();
            return;
        }

        Type mapType = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        Gson gson = new Gson();
        this.data = gson.fromJson(content.get("data"), mapType);
    }

    /**
     * Decodes specified raw packet data with Base64 Decoder
     *
     * @param content encoded data
     * @return
     */
    private String decode(String content) {
        return new String(Base64.getDecoder().decode(content), CharsetUtil.UTF_8);
    }

    /**
     * Resolves specified raw packet data to BenchionPacket
     *
     * @return Benchion Packet Class
     * @throws IllegalPacket
     */
    public BenchionPacket resolve() throws IllegalPacket {
        if (!BenchionPackets.contains(id)) throw new IllegalPacket("That packet is not registered in server!");
        return BenchionPackets.get(id);
    }

    /**
     * Resolves data in specified raw packet data
     *
     * @return
     */
    public PacketContext resolveData() {
        return new PacketContext(id, data);
    }
}
