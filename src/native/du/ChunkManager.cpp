#include "ChunkManager.hpp"
#include <logger.h>

void Chunk::free(){

}


std::map<int, std::vector<Chunk*>> dimensions;


std::vector<int> ChunkManager::getDimensions(){
    std::vector<int> result;
	for(auto it = dimensions.begin(); it != dimensions.end(); ++it)
        result.push_back(it->first);
    return result;
}

std::vector<Chunk*>& getChunks(int dimension){
    if(dimensions.find(dimension) == dimensions.end()){
        std::vector<Chunk*> array;
        dimensions.insert(std::make_pair(dimension, array));
        return array;
    }
    Logger::debug("DU", "Normal return");
    return dimensions[dimension];
}

void ChunkManager::add(Chunk* chunk){
    getChunks(chunk->dimension).push_back(chunk);
}

void ChunkManager::clear(){
    dimensions.clear();
}

int ChunkManager::getCount(){
    int count = 0;
    for(auto it = dimensions.begin(); it != dimensions.end(); ++it)
        count += getChunks(it->first).size();;
    return count;
}

int ChunkManager::getCount(int dimension){
    return getChunks(dimension).size();
}


bool ChunkManager::isChunckLoaded(int dimension, int x, int z){
    return false;
}

Chunk* ChunkManager::remove(int dimension){
    std::vector<Chunk*> chunks = getChunks(dimension);
    return *(chunks.erase(chunks.begin()));
}

extern "C" {
    JNIEXPORT jlong JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_createChunk
    (JNIEnv* env, jobject clz, jint dimension, jint x, jint z, jlong time) {
        return (jlong) new Chunk(dimension, x, z, time);
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeGetDimension
    (JNIEnv* env, jobject clz, jlong chunk) {
        return ((Chunk*) chunk)->dimension;
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeGetX
    (JNIEnv* env, jobject clz, jlong chunk) {
        return ((Chunk*) chunk)->x;
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeGetZ
    (JNIEnv* env, jobject clz, jlong chunk) {
        return ((Chunk*) chunk)->z;
    }

    JNIEXPORT jlong JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeGetTime
    (JNIEnv* env, jobject clz, jlong chunk) {
        return ((Chunk*) chunk)->time;
    }

    JNIEXPORT jboolean JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeCanClear
    (JNIEnv* env, jobject clz, jlong chunk) {
        return JNI_TRUE;
    }

    JNIEXPORT void JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeSetCanClear
    (JNIEnv* env, jobject clz, jlong chunk, jboolean value) {

    }

    JNIEXPORT void JNICALL Java_com_reider_dungeonutility_struct_generation_types_api_NativeChunk_nativeFree
    (JNIEnv* env, jobject clz, jlong ptr) {
        Chunk* chunk = (Chunk*) ptr;
        delete chunk;
    }










    JNIEXPORT jintArray JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeGetDimensions
    (JNIEnv* env, jobject clz) {
        std::vector<int> dimensions = ChunkManager::getDimensions();
        int size = dimensions.size();
		jintArray array = env->NewIntArray(size);
		env->SetIntArrayRegion(array, 0, size, (jint*) &dimensions[0]);
		return array;
    }

    JNIEXPORT jboolean JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeIsChunckLoaded
    (JNIEnv* env, jobject clz, jint dimension, jint x, jint z) {
        return ChunkManager::isChunckLoaded(dimension, x, z) == JNI_TRUE;
    }

    JNIEXPORT jlong JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeRemove
    (JNIEnv* env, jobject clz, jint dimension) {
        ChunkManager::remove(dimension);
    }

    JNIEXPORT void JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeAdd
    (JNIEnv* env, jobject clz, jlong chunk) {
        ChunkManager::add((Chunk*) chunk);
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeGetCount
    (JNIEnv* env, jobject clz) {
        return ChunkManager::getCount();
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeGetCountByDimension
    (JNIEnv* env, jobject clz, jint dimension) {
        return ChunkManager::getCount(dimension);
    }

    JNIEXPORT jint JNICALL Java_com_reider_dungeonutility_struct_generation_util_NativeChunkManager_nativeClear
    (JNIEnv* env, jobject clz) {
        ChunkManager::clear();
    }
}