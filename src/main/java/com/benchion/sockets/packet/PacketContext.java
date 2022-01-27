package com.benchion.sockets.packet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

/**
 * Packet Data is holding in that class
 */
@AllArgsConstructor
@Getter
public final class PacketContext {
    private final int id;
    private final HashMap<String, Object> data;

    /**
     * @param id Packet ID
     */
    public PacketContext(int id) {
        this.id = id;
        this.data = new HashMap<>();
    }

    /**
     * @param key The data key
     * @return data value
     */
    public Object get(String key) {
        return this.data.getOrDefault(key, null);
    }

    /**
     * Puts a new data or replace existing data
     *
     * @param key   Data key
     * @param value Data value
     */
    public void set(String key, Object value) {
        if (this.data.containsKey(key)) {
            this.data.replace(key, value);
            return;
        }
        this.data.put(key, value);
    }

    /**
     * Converts context to JSON Object
     *
     * @return JSON Object
     */
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("packet_id", this.id);

        Gson gson = new Gson();
        JsonObject data = gson.toJsonTree(this.data).getAsJsonObject();
        object.add("data", data);

        return object;
    }


    /**
     * Converts context to JSON String
     *
     * @return JSON String
     */
    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
