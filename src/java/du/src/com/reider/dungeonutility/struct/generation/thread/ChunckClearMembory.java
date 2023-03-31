package com.reider.dungeonutility.struct.generation.thread;

import java.util.ArrayList;

import com.reider.dungeonutility.struct.generation.StructurePiece;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

public class ChunckClearMembory extends Thread {
    public static long time = 2000l;

    public static long limit = 30l*1000l;

    public static int pre_length = -1;

    public ChunckClearMembory(){
        this.start();
    }

    @Override
    public void run() {
        while(true){
            try{
                synchronized(StructurePiece.loaded){
                    long now = System.currentTimeMillis();
                    ArrayList<StructurePiece.ChunckPosTime> list = new ArrayList<>();
                    for(StructurePiece.ChunckPosTime pos : StructurePiece.loaded)
                        if(now - pos.time <= limit)
                            list.add(pos);
                    StructurePiece.loaded = list;
                }
                sleep(time);
            }catch(Exception e){
                Logger.error(e.getLocalizedMessage());
            }
        }
    }
}
