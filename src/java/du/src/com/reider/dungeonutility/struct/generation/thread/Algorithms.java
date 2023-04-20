package com.reider.dungeonutility.struct.generation.thread;

import java.util.ArrayList;

import com.reider.Debug;
import com.reider.dungeonutility.struct.generation.StructurePieceController;
import com.reider.dungeonutility.struct.generation.thread.algorithms.Base;
import com.reider.dungeonutility.struct.generation.types.api.WorldStructure;
import com.zhekasmirnov.apparatus.adapter.innercore.game.common.Vector3;
import com.zhekasmirnov.innercore.api.log.DialogHelper;
import com.zhekasmirnov.horizon.runtime.logger.Logger;

public class Algorithms extends Thread {
    public ArrayList<Base> algorithms = new ArrayList<>();

    public void addAlgoritm(Base base){
        algorithms.add(base);
    }

    public ArrayList<Vector3> list_pos = new ArrayList<>();
    public int length_pre = -1;
    public long time = 2000l;

    public Algorithms(){
        this.start();
    }

    public void setTime(long time){
        this.time = time;
    }
    

    public void algorithmsOptimization(Vector3 pos){
        WorldStructure[] structures = StructurePieceController.getStorage().getStructures();

        for(Base base : algorithms)
            base.run(pos, structures);

        ArrayList<WorldStructure> result = new ArrayList<>();
        for(WorldStructure stru : structures)
            if(stru != null)
                result.add(stru);
        StructurePieceController.getStorage().setStructures(structures);
    }
    
    @Override
    public void run() {
        while(true){
            try {
                long start = System.currentTimeMillis();
                ArrayList<Vector3> list;
                synchronized(list_pos){
                    list = (ArrayList<Vector3>) list_pos.clone();
                }
                if(list.size() == 0){
                    sleep(time);
                    continue;
                }
                if(length_pre == -1){
                    algorithmsOptimization(list.remove(0));
                    length_pre = list.size();
                }else{
                    int count = list.size() - length_pre;
                    if(count <= 8)
                        algorithmsOptimization(list.remove(0));
                    else if(count <= 64)
                        for(int i = 0;i < 4;i++)
                            algorithmsOptimization(list.remove(0));
                    else if(count <= 128)
                        for(int i = 0;i < 16;i++)
                            algorithmsOptimization(list.remove(0));
                    else{
                        int size = list.size();
                        for(int i = 0;i < size;i++)
                            algorithmsOptimization(list.remove(0));
                    }
                    length_pre = list.size();
                }

                Debug.get().updateDebug("algorithms", "Algorithms time:"+(System.currentTimeMillis()-start));
                sleep(time);
            } catch (Exception e){
                Logger.error(e.getLocalizedMessage());
                DialogHelper.reportNonFatalError("Generation", e);
            }
        }
    }

    public void addPos(Vector3 pos){
        synchronized(list_pos){
            list_pos.add(pos);
        }
    }
}