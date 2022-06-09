#pragma once 

struct ItemStack {
	char filler[256];
	static ItemStack* getById(short, int, int, long long);
};
