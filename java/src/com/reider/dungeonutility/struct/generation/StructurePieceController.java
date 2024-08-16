package com.reider.dungeonutility.struct.generation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.struct.generation.thread.Algorithms;
import com.reider.dungeonutility.struct.generation.thread.algorithms.ClearingClusters;
import com.reider.dungeonutility.struct.generation.thread.algorithms.CriticalRelease;
import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.IStructurePiece;
import com.reider.dungeonutility.struct.generation.types.IStructureStorage;
import com.reider.dungeonutility.struct.generation.types.api.Default;
import com.reider.dungeonutility.struct.generation.types.api.Nether;
import com.reider.dungeonutility.struct.generation.types.api.OverWorld;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.reider.dungeonutility.struct.generation.util.ChunkManager;
import com.reider.dungeonutility.struct.generation.util.NativeChunkManager;
import com.reider.dungeonutility.struct.generation.util.StructureStorage;
import com.zhekasmirnov.innercore.api.runtime.saver.world.WorldDataScopeRegistry;
import org.json.JSONArray;

public class StructurePieceController {
    public static HashMap<String, IStructurePiece> pieces = new HashMap<>();
    public static String type_piece = "java";
    public static void setTypePiece(String type){
        StructurePieceController.type_piece = type;
    }
    public static IStructurePiece getPiece(){
        return pieces.get(type_piece);
    }
    public static IStructurePiece getPiece(String type){
        return pieces.get(type);
    }
    public static String[] getPieces(){
        Set<String> set = pieces.keySet();
        Iterator<String> it = set.iterator();
        int size = set.size();
        String[] result = new String[size];
        for(int i = 0;i < size;i++)
            result[i] = it.next();
        return result;
    }

    public static HashMap<String, IStructureStorage> storages = new HashMap<>();
    public static String type_storage = "java";
    public static void setTypeStorage(String type){
        StructurePieceController.type_storage = type;
    }
    public static IStructureStorage getStorage(){
        return storages.get(type_storage);
    }

    public static HashMap<String, IChunkManager> chunk_managers = new HashMap<>();
    public static String type_chunk_managers = "java";
    public static void setTypeChunkManager(String type){
        StructurePieceController.type_chunk_managers = type;
    }
    public static IChunkManager getChunkManager(){
        return chunk_managers.get(type_chunk_managers);
    }


    public void generationChunck(int x, int z, Random random, int dimension){
        getPiece().generation(x, z, random, dimension);
    }

    //public static ChunkClearMembory chunckClearMembory = new ChunkClearMembory();
    public static Algorithms algorithms = new Algorithms();

    public static CriticalRelease criticalRelease = new CriticalRelease();
    public static ClearingClusters clearingClusters = new ClearingClusters();

    static {
        storages.put("java", new StructureStorage());

        chunk_managers.put("java", new ChunkManager());
        chunk_managers.put("native", new NativeChunkManager());

        StructurePiece piece = new StructurePiece();
        pieces.put("java", piece);
        piece.registerType(new OverWorld());
        piece.registerType(new Nether());
        piece.registerType(new Default());

        algorithms.addAlgoritm(criticalRelease);
        algorithms.addAlgoritm(clearingClusters);

        DungeonUtilityMain.getPackVersionApi().addCallback("LevelLeft", args -> {
            getStorage().clear();
            getChunkManager().clear();
            return null;
        });

        WorldDataScopeRegistry.getInstance().addScope("du.StructureStorage", new WorldDataScopeRegistry.SaverScope() {
            @Override
            public void readJson(Object o) throws Exception {
                if(o == null) return;
                if(o instanceof JSONArray){
                    final JSONArray array = (JSONArray) o;
                    final IStructureStorage storage = getStorage();

                    for(int i = 0;i < array.length();i++){
                        storage.add(new WorldStructure(array.getJSONObject(i)));
                    }
                    return;
                }
                throw new RuntimeException("Not support read "+o.getClass());
            }

            @Override
            public Object saveAsJson() throws Exception {
                final JSONArray array = new JSONArray();

                for(WorldStructure structure : getStorage().getStructures())
                    array.put(structure.toJSON());

                return null;
            }
        });
    }
}
