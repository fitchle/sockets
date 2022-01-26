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
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;

public final class RawPacketResolver {
    private final JsonObject content;
    private final int id;
    private HashMap<String, Object> data;

    public RawPacketResolver(String str) throws IllegalPacketFormat {
        this.content = JsonParser.parseString(decode(str)).getAsJsonObject();
        if (!content.has("packet_id")) throw new IllegalPacketFormat("The packet id is not specified!");

        this.id = content.get("packet_id").getAsInt();

        if (!content.has("data")) {
            this.data = new HashMap<>();
            return;
        }

        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
        Gson gson = new Gson();
        this.data = gson.fromJson(content.get("data"), mapType);
    }

    private String decode(String content) {
        return new String(Base64.getDecoder().decode(content), CharsetUtil.UTF_8);
    }

    public BenchionPacket resolve() throws IllegalPacket {
        if (!BenchionPackets.contains(id)) throw new IllegalPacket("That packet is not registered in server!");
        return BenchionPackets.get(id);
    }

    public PacketContext resolveData() {
        return new PacketContext(id, data);
    }
}
