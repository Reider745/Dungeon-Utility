#pragma once

class VtableHelper {
 public:
 void** vtable;
 void* original;

 VtableHelper(void* a);
 void resize();
 void patch(const char* table, const char* symbol, void* func);

    public:
 void* getAdreess(const char* table, const char* symbol);
    void* getTop();
    void** get();
};

#include "vtable.h"

VtableHelper::VtableHelper(void* a){
  original = a;
  vtable = *(void***) a;
};

void** VtableHelper::get(){
  return vtable;
};

void VtableHelper::resize(){
  const int new_size = 512;
  const int offset = 4;
  void** vtable_old = this->vtable;
  vtable = (void**) malloc((new_size + offset) * sizeof(void*));
  //memcpy(this->vtable, vtable_old - offset, (new_size + offset) * sizeof(void*));
  this->vtable += offset;
  *(void***) this->original = vtable;

}

void VtableHelper::patch(const char* table, const char* symbol, void* func){
  int index = getVtableOffset(table, symbol);
  this->get()[index] = func;
};

void* VtableHelper::getAdreess(const char* table, const char* symbol){
  return SYMBOL(table, symbol);
};
