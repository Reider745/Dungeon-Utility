#pragma once

struct ActorUniqueID {
	long long value = 0;
	static ActorUniqueID const INVALID_ID;
	inline ActorUniqueID() {}
	inline ActorUniqueID(long long value) : value(value) {} 
};