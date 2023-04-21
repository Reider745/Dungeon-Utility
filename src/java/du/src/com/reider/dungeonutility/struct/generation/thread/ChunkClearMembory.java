package com.reider.dungeonutility.struct.generation.thread;

import com.reider.Debug;
import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.IChunk;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;

public class ChunkClearMembory extends Thread {
    public long time = 3000l;
    public long limit = 60 * 1000l;

    public void setTime(long time){
        this.time = time;
    }

    public void setLimit(long limit){
        this.limit = limit;
    }

    public ChunkClearMembory(){
        this.start();
    }

    private long getTime(IChunk chunk){
        if(chunk.canClear())
            return (long) (Math.sqrt(chunk.getTime()) * 10);
        return chunk.getTime();
    }

    public void optimization(IChunkManager manager, IChunk chunk){
        if(System.currentTimeMillis() - getTime(chunk) <= limit)
            manager.add(chunk);
        else
            chunk.free();
    }

    @Override
    public void run() {
        while(true){
            try{
                long start = System.currentTimeMillis();
                IChunkManager manager = StructurePieceController.getChunkManager();
                int[] dimensions = manager.getDimensions();
                for(int dimension : dimensions){
                    int count = manager.getCount(dimension);
                    if(count == 0)
                        continue;
                    if(count <= 64)
                        optimization(manager, manager.remove(dimension));
                    else if(count <= 128)
                        for(int i = 0;i < 64;i++)
                            optimization(manager, manager.remove(dimension));
                    else if(count <= 512)
                        for(int i = 0;i < 256;i++)
                            optimization(manager, manager.remove(dimension));
                    else if(count <= 1024)
                        for(int i = 0;i < 512;i++)
                            optimization(manager, manager.remove(dimension));
                    else
                        for(int i = 0;i < count;i++)
                            optimization(manager, manager.remove(dimension));
                    
                }
                Debug.get().updateСhart("chunk_clear_manager", "ChunkClear time", (int) (System.currentTimeMillis()-start));
                Debug.get().updateСhart("chunk_clear_manager_chunks", "Chunks", manager.getCount());
                sleep(time);
            }catch(Exception e){
                Debug.get().error(e);
            }
        }
    }
}
