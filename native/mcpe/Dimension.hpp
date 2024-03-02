#pragma once

enum Brightness {};
class WorldGenerator;

struct Dimension {
	//void getLevelConst() const;
	int getDimensionId() const;
	WorldGenerator getWorldGenerator() const;
	int getMoonPhase() const;
	
	void setSkylight(bool);
	bool hasSkylight() const;
	
	float getSunAngle(float) const;
	Brightness getMoonBrightness() const;
	int getHeightRange() const;
	void setSkyDarken(Brightness);
	
	bool hasCeiling() const;
	void setCeiling(bool);
};