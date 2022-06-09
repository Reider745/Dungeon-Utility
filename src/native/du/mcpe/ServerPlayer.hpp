#pragma once

class ActorUniqueID;

struct Actor {
	ActorUniqueID getUniqueID() const;
};

struct Mob : public Actor {
	
};

struct Player : public Mob {
	
};

struct ServerPlayer : public Player {
	
};