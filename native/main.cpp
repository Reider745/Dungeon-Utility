#include <hook.h>
#include <mod.h>
#include <logger.h>
#include <symbol.h>
#include <nativejs.h>
#include <innercore_callbacks.h>



#include "includes/VtableHelper.h"
#include "Structure.hpp"
#include "ItemGeneration.hpp"
#include "DungeonUtilityDimension.hpp"
#include "GenerationUtils.hpp"
#include "Global.hpp"

jclass DungeonUtility::NativeAPI;

class MainModule : public Module {
public:
	MainModule(const char* id): Module(id) {};
	 
	virtual void initialize() {
		JNIEnv* env;
		ATTACH_JAVA(env, JNI_VERSION_1_2) {
			jclass localClass = env->FindClass("com/reider/dungeonutility/NativeAPI");
			DungeonUtility::NativeAPI = reinterpret_cast<jclass>(env->NewGlobalRef(localClass)); 
		}
		DLHandleManager::initializeHandle("libminecraftpe.so", "mcpe");
		ItemGeneration::init();
		Structure::init();
		DungeonUtilityDimension::init();
		
  }
};

MAIN {
	Module* main_module = new MainModule("DungeonUtility");
	new GenerationUtilsModule(main_module);
}

std::string jstring2string(JNIEnv* env, jstring jStr) {
	if (!jStr) 
		return ""; 
	const jclass stringClass = env->GetObjectClass(jStr);
	const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
	const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));
	size_t length = (size_t) env->GetArrayLength(stringJbytes); 
	jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);
	std::string ret = std::string((char *)pBytes, length);
	env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);
	env->DeleteLocalRef(stringJbytes); env->DeleteLocalRef(stringClass); 
	return ret; 
}

extern "C" JNIEXPORT void JNICALL Java_com_reider_dungeonutility_NativeAPI_setCustomStructure
  (JNIEnv* env, jobject clz, jstring name, jint x, jint y, jint z, jlong pointer) {
    Structure::setStructure(jstring2string(env, name), (int) x, (int) y, (int) z, (BlockSource*) pointer);
}

extern "C" JNIEXPORT void JNICALL Java_com_reider_dungeonutility_NativeAPI_fill
  (JNIEnv* env, jobject clz, jstring name, jint x, jint y, jint z, jlong pointer) {
    ItemGeneration::fill(jstring2string(env, name), (int) x, (int) y, (int) z, (BlockSource*) pointer);
}

extern "C" JNIEXPORT void JNICALL Java_com_reider_dungeonutility_NativeAPI_addItem
  (JNIEnv* env, jobject clz, jstring name, jint id, jint data, jfloat chance, jint min, jint max) {
    ItemGeneration::addItem(jstring2string(env, name), (int) id, (int) data, (float) chance, (int) min, (int) max);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_reider_dungeonutility_NativeAPI_isCustomLoad
  (JNIEnv* env, jobject clz, jstring name) {
    return (jboolean) Structure::isLoad(jstring2string(env, name));
}