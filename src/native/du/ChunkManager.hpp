#pragma once

#include <vector>
#include <map>
#include <java.h>

class Chunk {
    public:
        int dimension, x, z;
        jlong time;
        Chunk(int dimension, int x, int z, jlong time): dimension(dimension), x(x), z(z), time(time){};
        void free();
};

namespace ChunkManager {
    std::vector<int> getDimensions();
    void add(Chunk* chunk);
    bool isChunckLoaded(int dimension, int x, int z);
    Chunk* remove(int dimension);
    int getCount();
    int getCount(int dimension);
    void clear();
};