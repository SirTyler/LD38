package me.sirtyler.ld38.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import me.sirtyler.ld38.LD38Game;
import me.sirtyler.ld38.ents.EntityComet;
import me.sirtyler.ld38.ents.EntityPlanet;
import me.sirtyler.ld38.ents.EntityPlayer;
import me.sirtyler.ld38.ents.EntitySpaceShip;

public class PlayerListener implements ContactListener {
	
	private Sound hit;
	
	public PlayerListener() {
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture user = contact.getFixtureA();
		Fixture target = contact.getFixtureB();
		
		if(user.getBody().getUserData() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user.getBody().getUserData();
			
			if(target.getBody().getUserData() instanceof EntityComet) {
				player.health -= ((EntityComet) target.getBody().getUserData()).getDamage();
				hit.play(0.5f);
				LD38Game.world.dead.add((EntityComet) target.getBody().getUserData());
			}
			
			if(target.getBody().getUserData() instanceof EntitySpaceShip) {
				if(!LD38Game.world.levelComplete) {
					LD38Game.world.levelComplete = true;
				}
			}
			
			if(target.getBody().getUserData() instanceof EntityPlanet) {
				//System.out.println("Touchdown");
				PlanetType type = ((EntityPlanet) target.getBody().getUserData()).type;
				switch(type) {
				case BAD:
					((EntityPlanet) target.getBody().getUserData()).doDamage = true;
					player.doRefuel = true;
					break;
				case SAFE:
				case SAFE2:
					((EntityPlanet) target.getBody().getUserData()).doHeal = true;
				default:
					player.doRefuel = true;
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture user = contact.getFixtureA();
		Fixture target = contact.getFixtureB();
		
		if(user.getBody().getUserData() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user.getBody().getUserData();
			
			if(target.getBody().getUserData() instanceof EntityPlanet) {
				//System.out.println("Touchdown");
				PlanetType type = ((EntityPlanet) target.getBody().getUserData()).type;
				switch(type) {
				case BAD:
					((EntityPlanet) target.getBody().getUserData()).doDamage = false;
					player.doRefuel = false;
					break;
				case SAFE:
				case SAFE2:
					((EntityPlanet) target.getBody().getUserData()).doHeal = false;
				default:
					player.doRefuel = false;
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
