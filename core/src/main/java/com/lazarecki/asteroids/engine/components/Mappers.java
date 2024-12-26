package com.lazarecki.asteroids.engine.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.lazarecki.asteroids.engine.components.collision.DetectedCollisionComponent;
import com.lazarecki.asteroids.engine.components.collision.ProcessedCollisionComponent;
import com.lazarecki.asteroids.engine.components.location.*;
import com.lazarecki.asteroids.engine.components.logic.*;
import com.lazarecki.asteroids.engine.components.physics.*;

public final class Mappers {
    // collisions
    public static final ComponentMapper<DetectedCollisionComponent> detectedCollision = ComponentMapper.getFor(DetectedCollisionComponent.class);
    public static final ComponentMapper<ProcessedCollisionComponent> processedCollision = ComponentMapper.getFor(ProcessedCollisionComponent.class);

    // location
    public static final ComponentMapper<BoundingRadiusComponent> boundingRadius = ComponentMapper.getFor(BoundingRadiusComponent.class);
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<PreviousPositionComponent> prevPosition = ComponentMapper.getFor(PreviousPositionComponent.class);
    public static final ComponentMapper<RotationComponent> rotation = ComponentMapper.getFor(RotationComponent.class);
    public static final ComponentMapper<ShapeComponent> shape = ComponentMapper.getFor(ShapeComponent.class);
    public static final ComponentMapper<TeleportingComponent> teleporting = ComponentMapper.getFor(TeleportingComponent.class);

    // logic
    public static final ComponentMapper<AsteroidComponent> asteroid = ComponentMapper.getFor(AsteroidComponent.class);
    public static final ComponentMapper<BulletComponent> bullet = ComponentMapper.getFor(BulletComponent.class);
    public static final ComponentMapper<FiringComponent> firing = ComponentMapper.getFor(FiringComponent.class);
    public static final ComponentMapper<FiredComponent> fired = ComponentMapper.getFor(FiredComponent.class);
    public static final ComponentMapper<ShipComponent> ship = ComponentMapper.getFor(ShipComponent.class);

    // physics
    public static final ComponentMapper<AngularAccelerationComponent> angularAcc = ComponentMapper.getFor(AngularAccelerationComponent.class);
    public static final ComponentMapper<AngularDumpingComponent> angularDump = ComponentMapper.getFor(AngularDumpingComponent.class);
    public static final ComponentMapper<AngularVelocityComponent> angularVel = ComponentMapper.getFor(AngularVelocityComponent.class);
    public static final ComponentMapper<LinearAccelerationComponent> linearAcc = ComponentMapper.getFor(LinearAccelerationComponent.class);
    public static final ComponentMapper<LinearDumpingComponent> linearDump = ComponentMapper.getFor(LinearDumpingComponent.class);
    public static final ComponentMapper<LinearVelocityComponent> linearVel = ComponentMapper.getFor(LinearVelocityComponent.class);
}
