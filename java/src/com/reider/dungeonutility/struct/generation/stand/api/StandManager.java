package com.reider.dungeonutility.struct.generation.stand.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.stand.LastingStand;
import com.reider.dungeonutility.struct.generation.stand.surface.SurfaceTowerStand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StandManager {
    private static final Map<String, Function<Structure, BaseStand>> stands = new HashMap<>();

    public static void register(@NonNull String name, Function<Structure, BaseStand> builder) {
        stands.put(name, builder);
    }

    @Nullable
    public static BaseStand build(@NonNull String name, @NonNull Structure structure) {
        final Function<Structure, BaseStand> builder = stands.get(name);
        if(builder != null) {
            final BaseStand stand = builder.apply(structure);
            if(stand != null) {
                stand.setName(name);
                return stand;
            }
        }
        return null;
    }

    static {
        register(LastingStand.ID, LastingStand::new);
        register(SurfaceTowerStand.ID, SurfaceTowerStand::new);
    }
}
