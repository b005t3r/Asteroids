package com.lazarecki.asteroids.engine.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.lazarecki.asteroids.Constants;
import com.lazarecki.asteroids.engine.components.Mappers;
import com.lazarecki.asteroids.engine.components.logic.ScoreCounterComponent;

public class ScoreRendererSystem extends IteratingSystem {
    private Label scoreLabel;
    private int lastScore = -1;

    public ScoreRendererSystem(Label scoreLabel) {
        super(Family.all(ScoreCounterComponent.class).get(), Constants.scoreRenderingPriority);

        this.scoreLabel = scoreLabel;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ScoreCounterComponent score = Mappers.score.get(entity);

        if(lastScore == score.score)
            return;

        lastScore = score.score;

        scoreLabel.getText().clear();

        if(lastScore < 10)
            scoreLabel.getText().append("0000");
        else if(lastScore < 100)
            scoreLabel.getText().append("000");
        else if(lastScore < 1000)
            scoreLabel.getText().append("00");
        else if(lastScore < 10000)
            scoreLabel.getText().append("0");

        scoreLabel.getText().append(lastScore);
        scoreLabel.invalidateHierarchy();
    }
}
