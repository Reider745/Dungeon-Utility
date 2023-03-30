package com.reider.dungeonutility.struct.generation.thread;

import java.util.ArrayList;

import com.reider.dungeonutility.struct.generation.StructurePiece;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.innercore.api.log.DialogHelper;
import com.zhekasmirnov.horizon.runtime.logger.Logger;
import com.zhekasmirnov.innercore.api.NativeAPI;

public class Algorithms extends Thread {
    private static ArrayList<Vector3> list = new ArrayList<>();
    private static int length_pre = -1;
    private static long time = 500l;


    public Algorithms(){
        this.start();
    }
    
    @Override
    public void run() {
        while(true){
            try {
                if(list.size() == 0){
                    this.sleep(time);
                    continue;
                }
                if(length_pre == -1){
                    StructurePiece.algorithmsOptimization(list.remove(0));
                    length_pre = list.size();
                }else{
                    int count = list.size() - length_pre;
                    if(count <= 8)
                        StructurePiece.algorithmsOptimization(list.remove(0));
                    else if(count <= 64)
                        for(int i = 0;i < 4;i++)
                            StructurePiece.algorithmsOptimization(list.remove(0));
                    else if(count <= 128)
                        for(int i = 0;i < 16;i++)
                            StructurePiece.algorithmsOptimization(list.remove(0));
                    else{
                        int size = list.size();
                        for(int i = 0;i < size;i++)
                            StructurePiece.algorithmsOptimization(list.remove(0));
                    }
                    length_pre = list.size();
                }
                this.sleep(time);
            } catch (Exception e){
                Logger.error(e.getLocalizedMessage());
                DialogHelper.reportNonFatalError("Generation", e);
            }
        }
    }

    public static void addPos(Vector3 pos){
        list.add(pos);
        NativeAPI.clientMessage("addPos");
    }
}
