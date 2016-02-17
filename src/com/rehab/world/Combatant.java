package com.rehab.world;

public interface Combatant {
	
	// Monitors damage dealing and taking
	void setOnDamageTakenListener(OnDamageTakenListener listener);
	void setOnDamageDealtListener(OnDamageDealtListener listener);
		
}