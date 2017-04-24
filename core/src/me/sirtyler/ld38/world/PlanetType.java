package me.sirtyler.ld38.world;

import java.util.HashMap;

public enum PlanetType {
	
	SAFE(1, true, "safe", -1f),
	SAFE2(2, true, "safe2", -1f),
	BONUS(3, true, "bonus", 0f),
	BONUS2(4, true, "bonus2", 0f),
	BAD(5, true, "bad", 0.01f),
	GOAL(6, true, "goal", 0f);
	
	public static final int PLANET_SIZE = 32;
	
	private int id;
	private boolean collidable;
	private String name;
	private float damage;
	
	
	private PlanetType(int id, boolean collidable, String name) {
		this(id, collidable, name, 0f);
	}
	
	private PlanetType(int id, boolean collidable, String name, float damage) {
		this.id = id;
		this.collidable = collidable;
		this.name = name;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public boolean isCollidable() {
		return collidable;
	}

	public String getName() {
		return name;
	}

	public float getDamage() {
		return damage;
	}
	
	private static HashMap<Integer, PlanetType> typeMap;
	
	static {
		typeMap = new HashMap<Integer, PlanetType>();
		for(PlanetType types : PlanetType.values()) {
			typeMap.put(types.id, types);
		}
	}
	
	public static PlanetType getPlanetByID(int id) {
		return typeMap.get(id);
	}
	
}
