#pragma once

#include "Random.hpp"

struct Level {
	Random* getRandom() const;
};
class ServerLevel : public Level {};