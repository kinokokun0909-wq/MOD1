package com.example.examplemod;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class TestEntity extends PathfinderMob {

    protected TestEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
}