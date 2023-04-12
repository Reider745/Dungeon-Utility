package com.reider.dungeonutility.struct.generation.thread;

import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.Chunk;
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

    public void optimization(IChunkManager manager, Chunk chunck){
        if(System.currentTimeMillis() - chunck.time <= limit)
            manager.add(chunck);
    }

    @Override
    public void run() {
        while(true){
            try{
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
                sleep(time);
                NativeAPI.tipMessage("chuncks:"+manager.getCount());
            }catch(Exception e){
                Logger.error(e.getLocalizedMessage());
            }
        }
    }
}
