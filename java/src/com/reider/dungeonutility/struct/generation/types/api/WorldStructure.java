package com.reider.dungeonutility.struct.generation.types.api;

import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorldStructure {
    public final int dimension;
    public final Vector3 pos;
    public final String name;
    private final JSONObject json;

    public WorldStructure(Vector3 pos, String name, int dimension) {
        this.pos = pos;
        this.name = name;
        this.dimension = dimension;

        this.json = new JSONObject();

        try {
            json.put("d", dimension);
            json.put("n", name);

            final JSONArray position = new JSONArray();
            position.put(pos.x);
            position.put(pos.y);
            position.put(pos.z);

            json.put("p", position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public WorldStructure(JSONObject json){
        try {
            dimension = json.getInt("d");
            name = json.getString("n");

            final JSONArray position = json.getJSONArray("p");
            pos = new Vector3(position.getInt(0), position.getInt(1), position.getInt(2));

            this.json = json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject toJSON(){
        return json;
    }
}