package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.EngineUtils;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;

public class AsteroidSpawnerSystem extends IntervalSystem {
    private ImmutableArray<Entity> asteroids;

    public AsteroidSpawnerSystem() {
        super(Constants.asteroidSpawnerInterval, Constants.asteroidSpawnerPriority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        asteroids = engine.getEntitiesFor(Family.all(AsteroidComponent.class).get());
    }

    @Override
    protected void updateInterval() {
        int asteroidsSize = 0;

        for(int i = 0; i < asteroids.size(); ++i) {
            Entity asteroid = asteroids.get(i);

            AsteroidComponent as = Mappers.asteroid.get(asteroid);

            if(as.asteroidType != Constants.AsteroidType.epic
            && as.asteroidType != Constants.AsteroidType.large)
                continue;

            asteroidsSize++;
        }

        if(asteroidsSize >= Constants.asteroidSpawnThreshold)
            return;

        final Engine engine = getEngine();

        Constants.AsteroidType at = MathUtils.random(1.0f) < 0.35f ? Constants.AsteroidType.large : Constants.AsteroidType.epic;

        Entity entity = EngineUtils.createAsteroidEntity(at, engine);
        EngineUtils.spawnAsteroid(entity, engine);

        engine.addEntity(entity);
    }
}
