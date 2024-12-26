package com.lazarecki.asteroids.engine.systems;

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
        super(Constants.spawnerInterval, Constants.spawnerSystemPriority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        asteroids = engine.getEntitiesFor(Family.all(AsteroidComponent.class).get());
    }

    @Override
    protected void updateInterval() {
        if(asteroids.size() >= Constants.spawnThreshold)
            return;

        final Engine engine = getEngine();

        Constants.AsteroidType at = MathUtils.random(1.0f) < 0.35f ? Constants.AsteroidType.small : Constants.AsteroidType.epic;

        Entity entity = EngineUtils.createAsteroidEntity(at, engine);
        EngineUtils.spawnAsteroid(entity, engine);

        engine.addEntity(entity);
/*
        if(asteroids.size() >= 2)
            return;

        final Engine engine = getEngine();

        Entity epic = EngineUtils.createAsteroidEntity(Constants.AsteroidType.epic, engine);
        Entity small = EngineUtils.createAsteroidEntity(Constants.AsteroidType.small, engine);

        EngineUtils.spawnAsteroid(epic, engine);
        EngineUtils.spawnAsteroid(small, engine);

        Mappers.position.get(epic).position.set(Constants.gameWidth / 2, Constants.gameHeight / 2);
        Mappers.position.get(small).position.set(Constants.gameWidth / 2, Constants.gameHeight / 2);

        engine.addEntity(epic);
        engine.addEntity(small);
*/
    }
}
