#ifndef GENERATIONS_UTILS_DUNGEON_UTILITY
#define GENERATIONS_UTILS_DUNGEON_UTILITY

#include <mod.h>

class GenerationUtilsModule : public Module {
public:
    GenerationUtilsModule(Module* parent);

    void initialize() override;
};

#endif