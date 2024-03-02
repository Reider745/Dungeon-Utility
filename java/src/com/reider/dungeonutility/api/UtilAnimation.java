package com.reider.dungeonutility.api;

import com.zhekasmirnov.innercore.api.NativeRenderMesh;
import com.zhekasmirnov.innercore.api.NativeStaticRenderer;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI.StaticRenderer;

public class UtilAnimation {
    private NativeRenderMesh mesh;
    private float x, y, z, scale;
    private NativeStaticRenderer render = null;

    public UtilAnimation(NativeRenderMesh mesh, float x, float y, float z, float scale){
        description(mesh, x, y, z, scale);
    }

    public void description(NativeRenderMesh mesh, float x, float y, float z,  float scale){
        this.mesh = mesh;
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }

    public boolean canLoad(){
        return render != null;
    }

    public void load(){
        if(this.canLoad()) return;

        render = StaticRenderer.createStaticRenderer(-1, x, y, z);
        render.setSkin("terrain-atlas");
        render.setMesh(mesh);
        render.setPos(x, y, z);
        render.setScale(scale);
    }

    public void unload(){
        if(!this.canLoad()) return;

        this.render.remove();
    }

    public void reload(){
        unload();
        load();
    }
}
