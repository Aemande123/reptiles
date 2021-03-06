/*
 * EntityLizard.java
 *
 *  Copyright (c) 2017 Michael Sheppard
 *
 * =====GPLv3===========================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */

package com.reptiles.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityLizardBase extends EntityTameable {
    private final int maxHealth = 10;
    private static final DataParameter<Float> health = EntityDataManager.createKey(EntityLizardBase.class, DataSerializers.FLOAT);

    public EntityLizardBase(World world) {
        super(world);
//        setSize(0.2F, 0.25F);
        setPathPriority(PathNodeType.WATER, 0.0f);
        setTamed(false);
    }

    @Override
    protected void initEntityAI() {
        double moveSpeed = 1.0;

        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, aiSit = new EntityAISit(this));
        tasks.addTask(2, new EntityAIPanic(this, 0.38F));
        tasks.addTask(3, new EntityAIMate(this, moveSpeed));
        tasks.addTask(4, new EntityAITempt(this, 1.2, Items.CARROT, false));
        tasks.addTask(4, new EntityAITempt(this, 1.2, Items.GOLDEN_CARROT, false));
        if (ConfigHandler.getFollowOwner()) {
            tasks.addTask(5, new EntityAIFollowOwner(this, moveSpeed, 10.0F, 2.0F));
        }
        tasks.addTask(6, new EntityAIWander(this, moveSpeed));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

//    @Override
//    protected boolean canDespawn() {
//        return false;
//    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (isTamed()) {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth); // health
        } else {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0); // health
        }
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2); // move speed
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(health, getHealth());
    }

    // This MUST be overridden in the derived class
    public EntityAnimal spawnBabyAnimal(EntityAgeable entityageable) {
        Reptiles.instance.error("[ERROR] Do NOT call this base class method directly!");
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ReptileSounds.varanus_hurt;
    }

    @Override
    protected Item getDropItem() {
        return Reptiles.REPTILE_LEATHER;
    }

    @Override
    protected void dropFewItems(boolean flag, int add) {
        int count = rand.nextInt(3) + rand.nextInt(1 + add);
        dropItem(Reptiles.REPTILE_LEATHER, count);

        count = rand.nextInt(3) + 1 + rand.nextInt(1 + add);
        if (isBurning()) {
            dropItem(Reptiles.REPTILE_MEAT_COOKED, count);
        } else {
            dropItem(Reptiles.REPTILE_MEAT_RAW, count);
        }
    }

    private boolean isTamingFood(ItemStack itemstack) {
        return (itemstack != null && (itemstack.getItem() == Items.CARROT || itemstack.getItem() == Items.GOLDEN_CARROT));
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemFood && isTamingFood(itemStack));
    }

    @Override
    protected void updateAITasks() {
        dataManager.set(health, getHealth());
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entity) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
    }

    // taming stuff //////////////////
    @Override
    public boolean processInteract(EntityPlayer entityplayer, @Nonnull EnumHand enumHand) {
        ItemStack itemstack = entityplayer.getHeldItem(enumHand);

        if (isTamed()) {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood) itemstack.getItem();
                    if (isTamingFood(itemstack) && dataManager.get(health) < maxHealth) {
                        if (!entityplayer.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }

                        heal((float) itemfood.getHealAmount(itemstack));

                        if (itemstack.getCount() <= 0) {
                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
                        }

                        return true;
                    }
                }
            }

            if (isOwner(entityplayer) && !world.isRemote && !isBreedingItem(itemstack)) {
                aiSit.setSitting(!isSitting());
                isJumping = false;
                navigator.clearPath();
                setAttackTarget(null);
            }
        } else if (!itemstack.isEmpty() && itemstack.getItem() == Items.APPLE && entityplayer.getDistanceSq(this) < 9.0D) {
            if (!entityplayer.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            if (itemstack.getCount() <= 0) {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
            }

            if (!this.world.isRemote) {
                if (rand.nextInt(3) == 0) {
                    setTamed(true);
                    navigator.clearPath();
                    setAttackTarget(null);
                    aiSit.setSitting(true);
                    setHealth(maxHealth);
                    setOwnerId(entityplayer.getUniqueID());
                    playTameEffect(true);
                    world.setEntityState(this, (byte) 7);
                } else {
                    playTameEffect(false);
                    world.setEntityState(this, (byte) 6);
                }
            }

            return true;
        }

        return super.processInteract(entityplayer, enumHand);
    }

    @Override
    public boolean canMateWith(@Nonnull EntityAnimal entityAnimal) {
        if (entityAnimal == this) {
            return false;
        } else if (!isTamed()) {
            return false;
        } else if (!(entityAnimal instanceof EntityLizardBase)) {
            return false;
        } else {
            EntityLizardBase l = (EntityLizardBase) entityAnimal;
            return l.isTamed() && (!l.isSitting() && (isInLove() && l.isInLove()));
        }
    }

    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable var1) {
        return this.spawnBabyAnimal(var1);
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);

        if (tamed) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }

}
