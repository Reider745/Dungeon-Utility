package com.reider.dungeonutility.struct.generation.thread;

import java.util.ArrayList;
import java.util.Collection;

import com.reider.dungeonutility.struct.generation.StructurePiece;
import com.reider.dungeonutility.struct.generation.StructurePiece.ChunckPosTime;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;

public class ChunckClearMembory extends Thread {
    public static long time = 5000l;

    public static long limit = 60l*1000l;

    public ChunckClearMembory(){
        this.start();
    }

    public int getCount(){
        int result = 0;
        Collection<ArrayList<ChunckPosTime>> array = StructurePiece.loaded.values();
        for(ArrayList<ChunckPosTime> list : array)
            result += list.size();
        return result;
    }

    public void optimization(ArrayList<ChunckPosTime> list, ChunckPosTime chunck){
        if(System.currentTimeMillis() - chunck.time <= limit)
            list.add(chunck);
    }

    @Override
    public void run() {
        while(true){
            try{
                synchronized(StructurePiece.loaded){
                    Collection<ArrayList<ChunckPosTime>> array = StructurePiece.loaded.values();
                    for(ArrayList<ChunckPosTime> list : array){
                        if(list.size() == 0)
                            continue;
                        int count = list.size();
                        if(count <= 64)
                            optimization(list, list.remove(0));
                        else if(count <= 128)
                            for(int i = 0;i < 64;i++)
                                optimization(list, list.remove(0));
                        else if(count <= 512)
                            for(int i = 0;i < 256;i++)
                                optimization(list, list.remove(0));
                        else if(count <= 1024)
                            for(int i = 0;i < 512;i++)
                                optimization(list, list.remove(0));
                        else{
                            int size = list.size();
                            for(int i = 0;i < size;i++)
                                optimization(list, list.remove(0));
                        }
                    }
                    NativeAPI.tipMessage("chuncks:"+getCount());
                }
                sleep(time);
            }catch(Exception e){
                Logger.error(e.getLocalizedMessage());
            }
        }
    }
}
