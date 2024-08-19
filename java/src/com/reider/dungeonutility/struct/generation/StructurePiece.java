package com.reider.dungeonutility.struct.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;

import com.reider.dungeonutility.logger.Debug;
import com.reider.dungeonutility.DungeonUtilityMain;
import com.reider.dungeonutility.multiversions.js_types.IJsObject;
import com.reider.dungeonutility.struct.Structure;
import com.reider.dungeonutility.struct.generation.types.*;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.apparatus.mcpe.NativeBlockSource;
import com.zhekasmirnov.innercore.api.runtime.saver.world.WorldDataScopeRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

public class StructurePiece implements IStructurePiece {
    private final HashMap<String, IGenerationType> types = new HashMap<>();
    private final HashMap<String, IGenerationDescription> unique_description = new HashMap<>();
    private final ArrayList<IGenerationDescription> structures = new ArrayList<>();
    private final HashMap<Integer, HashMap<Long, ArrayList<StructureChunk>>> dimensionsChunksStructures = new HashMap<>();

    private static final Function<Integer, HashMap<Long, ArrayList<StructureChunk>>> NEW_HASH_MAP = k -> new HashMap<>();
    private static final Function<Long, ArrayList<StructureChunk>> NEW_ARRAY_LIST = k -> new ArrayList<>();

    public StructurePiece(String name){
        WorldDataScopeRegistry.getInstance().addScope("du.StructurePiece."+name, new WorldDataScopeRegistry.SaverScope() {

            @Override
            public void readJson(Object o) throws Exception {
                if(o == null) return;
                if(o instanceof JSONObject){
                    final JSONObject json_dimensions = (JSONObject) o;
                    final Iterator<String> dimensions_it = json_dimensions.keys();

                    while (dimensions_it.hasNext()){
                        final String dimension = dimensions_it.next();
                        final HashMap<Long, ArrayList<StructureChunk>> chunks = new HashMap<>();
                        final JSONObject json_chunks = json_dimensions.getJSONObject(dimension);
                        final Iterator<String> chunks_it = json_chunks.keys();

                        while (chunks_it.hasNext()){
                            final String chunk = chunks_it.next();
                            final ArrayList<StructureChunk> structures = new ArrayList<>();
                            final JSONArray list = json_chunks.getJSONArray(chunk);

                            for(int i = 0;i < list.length();i++)
                                structures.add(StructureChunk.fromJSON(list.getJSONObject(i)));

                            chunks.put(Long.parseLong(chunk), structures);
                        }

                        dimensionsChunksStructures.put(Integer.parseInt(dimension), chunks);
                    }
                }
                throw new RuntimeException("Not support read "+o.getClass());
            }

            @Override
            public Object saveAsJson() throws Exception {
                final JSONObject json_dimensions = new JSONObject();

                for(final Integer dimension : dimensionsChunksStructures.keySet()){
                    final JSONObject json_chunks = new JSONObject();
                    final HashMap<Long, ArrayList<StructureChunk>> chunks = dimensionsChunksStructures.get(dimension);

                    for(Long chunk : chunks.keySet()){
                        final JSONArray json_list = new JSONArray();
                        final ArrayList<StructureChunk> list = chunks.get(chunk);

                        for(StructureChunk structure : list)
                            json_list.put(structure.toJSON());

                        json_chunks.put(chunk.toString(), json_list);
                    }

                    json_dimensions.put(dimension.toString(), json_chunks);
                }

                return json_dimensions;
            }
        });

        DungeonUtilityMain.getPackVersionApi().addCallback("LevelLeft", args -> {
            dimensionsChunksStructures.clear();
            return null;
        });
    }

    private static Long hashChunkPos(int x, int z){
        return (((long)x) << 32) | (z & 0xffffffffL);
    }

    @Override
    public IGenerationDescription getFromUniqueIdentifier(String id) {
        return unique_description.get(id);
    }

    @Override
    public void addGeneration(IGenerationDescription stru) {
        structures.add(stru);

        final String id = stru.getUniqueIdentifier();
        if(!id.isEmpty()) {
            if(unique_description.containsKey(id))
                throw new RuntimeException("The structure with "+id+" generation has already been registered!");
            stru.setStructure(new StructureDescriptionChunkSlip(stru.getStructure().getStructure()));
            unique_description.put(id, stru);
        }
    }

    @Override
    public void registerType(IGenerationType type) {
        types.put(type.getType(), type);
    }

    @Override
    public void generation(int x, int z, Random random, int dimension) {
        final long start = System.currentTimeMillis();
        final NativeBlockSource region = NativeBlockSource.getCurrentWorldGenRegion();

        synchronized (dimensionsChunksStructures){
            final HashMap<Long, ArrayList<StructureChunk>> chunksStructures = dimensionsChunksStructures.computeIfAbsent(dimension, NEW_HASH_MAP);
            final Long hashCode = hashChunkPos(x, z);
            final ArrayList<StructureChunk> chunks = chunksStructures.get(hashCode);

            if(chunks != null) {
                for (StructureChunk chunk : chunks)
                    chunk.set(region);
                chunksStructures.remove(hashCode);
            }
        }

        final IJsObject object = DungeonUtilityMain.getPackVersionApi().createObjectEmpty();
        object.setJavaObj("random", random);
        Object jsPacket = object.passToScript();

        for(IGenerationDescription stru : structures)
            generationStructure(stru, x, z, random, region, jsPacket);

        Debug.get().updateСhart("generation", "Generation time ", (int) (System.currentTimeMillis() - start));
    }

    public void generationStructure(IGenerationDescription description, int x, int z,Random random, NativeBlockSource region, Object packet){
        int dimension = region.getDimension();
        IGenerationType type = types.get(description.getType());
        if(type == null) return;
        int[] counts = description.getCount();
        int count = counts[random.nextInt(counts.length)];
        for(int i = 0;i < count;i++)
            if(random.nextInt(description.getChance()) <= 1) {
                Vector3 pos = type.getPosition(x, z, random, dimension, region);
                Vector3 offset = description.getOffset();
                int[] y = description.getMinAndMaxY();
                if(y[0] >= pos.y && pos.y <= y[1]){
                    boolean res = true;
                    for(int yy = y[0];yy < y[1];yy++)
                        if(region.getBlockId((int) pos.x, yy, (int) pos.z) == 0 && region.getBlockId((int) pos.x, yy-1, (int) pos.z) != 0){
                            pos = new Vector3(pos.x, yy, pos.z);
                            res = false;
                            break;
                        }
                    if(res)
                        return;
                }
                
                if(description.canLegacyOffset())
                    pos = new Vector3(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

                double distant = description.getDistance();
                if(distant != 0) {
                    WorldStructure nearest = StructurePieceController.getStorage().getNearestStructure(pos,dimension, description.getName(), description.checkName());
                    if (nearest != null && nearest.pos.distance(pos) < distant)
                        return;
                }
                if(!(type.isGeneration(pos, random, dimension, region) && description.isGeneration(pos, random, dimension, region)) || (description.isSet() && !description.getStructure().isSetStructure((int)pos.x, (int)pos.y, (int)pos.z, region)))
                    return;
                
                if(!description.canLegacyOffset())
                    pos = new Vector3(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

                IStructureStorage storage = StructurePieceController.getStorage();
                if(description.isPoolStructure(pos, random, dimension, region))
                    storage.add(new WorldStructure(pos, description.getName(), dimension));

                if(!description.getUniqueIdentifier().isEmpty()){
                    final StructureDescriptionChunkSlip chunkSlip = (StructureDescriptionChunkSlip) description.getStructure().getStructure();

                    final int _x = (int) pos.x;
                    final int _z = (int) pos.z;

                    final int chunkStartX = _x - ((_x - chunkSlip.x_offset) % 16) - chunkSlip.x_offset;
                    final int chunkStartZ = _z - ((_z - chunkSlip.z_offset) % 16) - chunkSlip.z_offset;
                    final int chunkEndX = _x - ((_x + chunkSlip.max_x) % 16) + chunkSlip.max_x;
                    final int chunkEndZ = _z - ((_z + chunkSlip.max_z) % 16) + chunkSlip.max_z;

                    final Structure structure = description.getStructure();

                    structure.getPrototype().before(_x, (int) pos.y, _z, region, packet);
                    if(structure.isUseGlobalPrototype())
                        structure.getStructure().prot.before(_x, (int) pos.y, _z, region, packet);

                    StructureChunk lastChunk = null;
                    for(int X = chunkStartX, CX = chunkStartX / 16;X <= chunkEndX;X+=16,CX++)
                        for(int Z = chunkStartZ, CZ = chunkStartZ / 16;Z <= chunkEndZ;Z+=16,CZ++){
                            lastChunk = chunkSlip.getChunk(X, Z, _x, _z);

                            if(lastChunk.isEmpty()) continue;
                            lastChunk.init(description, pos, packet);

                            if(region.getChunkState(CX, CZ) >= 3) {
                                lastChunk.set(region);
                            }else{
                                synchronized (dimensionsChunksStructures){
                                    dimensionsChunksStructures.computeIfAbsent(dimension, NEW_HASH_MAP)
                                            .computeIfAbsent(hashChunkPos(CX, CZ), NEW_ARRAY_LIST)
                                                .add(lastChunk);
                                }
                            }
                        }
                    if(lastChunk != null)
                        lastChunk.setLast();
                    if(description.canOptimization())
                        StructurePieceController.algorithms.addPos(pos);
                }else
                    spawnStructure(description, pos, region, packet, random, dimension);
            }
    }

    @Override
    public void spawnStructure(IGenerationDescription description, Vector3 pos, NativeBlockSource region, Object object, Random random, int dimension) {
        description.getStructure().setStructure((int)pos.x, (int)pos.y, (int)pos.z, region, object);
        if(description.canOptimization())
            StructurePieceController.algorithms.addPos(pos);
    }
}