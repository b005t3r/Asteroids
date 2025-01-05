package com.lazarecki.asteroids.engine.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.AsteroidComponent;
import com.lazarecki.asteroids.engine.components.logic.BulletHitComponent;
import com.lazarecki.asteroids.engine.components.logic.ScoreCounterComponent;

public class ScoreCounterSystem extends IteratingSystem {
    private ImmutableArray<Entity> scoreCounter;

    public ScoreCounterSystem() {
        super(
            Family.all(AsteroidComponent.class, BulletHitComponent.class).get(),
            Constants.scoreCounterPriority
        );
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        scoreCounter = engine.getEntitiesFor(Family.all(ScoreCounterComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(scoreCounter.size() == 0)
            return;

        AsteroidComponent a = Mappers.asteroid.get(entity);

        if(a.asteroidType != Constants.AsteroidType.small)
            return;

        for(int i = 0; i < scoreCounter.size(); ++i) {
            ScoreCounterComponent score = Mappers.score.get(scoreCounter.get(i));

            score.score++;
        }
    }
}
