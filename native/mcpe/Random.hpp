#pragma once

struct Random {
	int nextInt(int);
	float nextFloat(int a){
int b = this->nextInt(a);
Logger::debug("TEST", "%i", b);
Logger::flush();
		return b / a;
	}
	float nextFloat(){
		return this->nextFloat(10000);
	}
};




