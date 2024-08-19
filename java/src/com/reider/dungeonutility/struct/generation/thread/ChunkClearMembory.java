package com.reider.dungeonutility.struct.generation.thread;

import com.reider.dungeonutility.logger.Debug;
import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.reider.dungeonutility.struct.generation.types.IChunkManager;
import com.reider.dungeonutility.struct.generation.types.api.IChunk;

public class ChunkClearMembory extends Thread {
    public static boolean enable = true;
    public static long time = 3000l;
    public static long limit = 60 * 1000l;
    public static int pace = 64;

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
                if(!enable){
                    sleep(time);
                    continue;
                }

                final long start = System.currentTimeMillis();
                final IChunkManager manager = StructurePieceController.getChunkManager();
                final int[] dimensions = manager.getDimensions();

                for(int dimension : dimensions){
                    int count = manager.getCount(dimension);
                    if(count == 0)
                        continue;

                    boolean critical = true;
                    for(int i = 0;i < 4;i++)
                        if(count < pace*i){
                            for(int a = 0;a < Math.max(1, pace*(i-1));a++)
                                optimization(manager, manager.remove(dimension));
                            critical = false;
                            break;
                        }

                    if(critical){
                        for(int i = 0;i < count;i++)
                            optimization(manager, manager.remove(dimension));
                    }
                }

                Debug.get().updateСhart("chunk_clear_manager", "Chunk сlear time", (int) (System.currentTimeMillis()-start));
                Debug.get().updateСhart("chunk_clear_manager_chunks", "Chunks", manager.getCount());

                sleep(time);
            }catch(Exception e){
                Debug.get().error(e);
            }
        }
    }
}
