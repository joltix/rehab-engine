package com.rehab.world;

public class Physics {
	// Gravitational constant
	public static final double GRAVITY = 0.00000000006674;
	// Default gravity settings
	public static final Double EARTH_MASS = new Double("5972000000000000000000000");
	public static final double EARTH_RADIUS = 6370000;
	
	public static double speed(double acceleration, double seconds) {
		return acceleration * seconds;
	}
	
	public static double acceleration(double force, double mass) {
		return force / mass;
	}
	
	public static double atDistanceWithAngleX(double dir, double dist) {
		return (Math.cos((dir * Math.PI) / 180) * dist);
	}
	
	public static double atDistanceWithAngleY(double dir, double dist) {
		return Math.abs(Math.sin((dir * Math.PI) / 180) * dist);
	}
	
	/**
	 * Calculates the magnitude of gravity for a world with the given mass
	 * and planetary radius.
	 * @param worldMass
	 * 		the mass of the game's planet in kilograms.
	 * @param planetRadius
	 * 		the radius of the game's planet in meters.
	 * @return
	 * 		the acceleration (m/s/s) of the planet's gravity.
	 */
	public static double getPlanetGravity(double worldMass, double planetRadius) {
		return Physics.GRAVITY * (worldMass / Math.pow(planetRadius, 2));
	}
	
}
