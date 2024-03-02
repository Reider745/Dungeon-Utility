#pragma once

#include <vector>
#include <map>
#include <java.h>

class Chunk {
    public:
        int dimension, x, z;
        jlong time;
        bool clear;
        Chunk(int dimension, int x, int z, jlong time);
        ~Chunk(){}
};

namespace ChunkManager {
    std::vector<int> getDimensions();
    void add(Chunk* chunk);
    bool isChunckLoaded(int dimension, int x, int z);
    bool canSpawn(int dimension, int sX, int sZ, int eX, int eZ);
    Chunk* remove(int dimension);
    int getCount();
    int getCount(int dimension);
    void clear();
    Chunk* at(int dimension, int x, int z);
    void setNotClear(int dimension, int sX, int sZ, int eX, int eZ);
};