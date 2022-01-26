package com.benchion.sockets.packet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public final class PacketContext {
    private final int id;
    private final HashMap<String, Object> data;

    public PacketContext(int id) {
        this.id = id;
        this.data = new HashMap<>();
    }

    public Object get(String key) {
        return this.data.getOrDefault(key, null);
    }

    public void set(String key, Object value) {
        if (this.data.containsKey(key)) {
            this.data.replace(key, value);
            return;
        }
        this.data.put(key, value);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("packet_id", this.id);

        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(this.data).getAsJsonObject();
        object.add("data", data);

        return object;
    }


    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
