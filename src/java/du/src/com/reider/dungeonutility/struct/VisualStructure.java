package com.reider.dungeonutility.struct;

import android.opengl.GLSurfaceView;
import com.reider.dungeonutility.StructureLoader;
import com.reider.dungeonutility.api.data.BlockData;
import com.zhekasmirnov.innercore.api.NativeBlockRenderer;
import com.zhekasmirnov.innercore.api.NativeRenderMesh;
import com.zhekasmirnov.innercore.api.mod.adaptedscript.AdaptedScriptAPI;
import com.zhekasmirnov.innercore.api.mod.ui.GuiRenderMesh;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VisualStructure {
    private static int convertId(int id){
        if(id > 255 && id < 2048)
            return 255 - id;
        return id;
    }

    public static NativeRenderMesh getStructureMesh(String name){
        BlockData[] blocks = StructureLoader.getStructure(name).blocks;
        NativeRenderMesh mesh = AdaptedScriptAPI.ItemModel.getEmptyMeshFromPool();
        for(BlockData block : blocks)
            AdaptedScriptAPI.ItemModel.getForWithFallback(convertId(block.state.id), block.state.data).addToMesh(mesh, block.x, block.y, block.z);
        return mesh;
    }

    public static class Animation {
        private GLSurfaceView view;
        private String name;
        private NativeRenderMesh mesh;
        public NativeBlockRenderer.RenderAPI renderAPI;

        public Animation(String name){
            this.name = name;
            mesh = getStructureMesh(name);
        }

        public void open(){
            view = new GLSurfaceView(AdaptedScriptAPI.UI.getContext());
            GuiRenderMesh render = mesh.newGuiRenderMesh();
            view.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                }

                @Override
                public void onSurfaceChanged(GL10 gl10, int i, int i1) {

                }

                @Override
                public void onDrawFrame(GL10 gl10) {
                    render.draw(gl10);
                }
            });
        }

        public void load(){

        }
    }

};