package ch.swisssmp.adventuredungeons.pathfinder;

import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.MathHelper;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import net.minecraft.server.v1_12_R1.World;

public class PathfinderGoalAnimalMeleeAttack extends PathfinderGoal {
    World a;
    EntityCreature entity;
    int c;
    double speed;
    boolean e;
    PathEntity target;
    @SuppressWarnings("rawtypes")
	Class g;
    private int h;
    private double i;
    private double j;
    private double k;
    private double damage;

    @SuppressWarnings("rawtypes")
	public PathfinderGoalAnimalMeleeAttack(EntityCreature entitycreature, Class oclass, double speed, double damage, boolean flag) {
        this(entitycreature, speed, damage, flag);
        this.g = oclass;
    }

    public PathfinderGoalAnimalMeleeAttack(EntityCreature entitycreature, double speed, double damage, boolean flag) {
        this.entity = entitycreature;
        this.a = entitycreature.world;
        this.speed = speed;
        this.damage = damage;
        this.e = flag;
        this.a(3);
    }

    @SuppressWarnings("unchecked")
	public boolean a() {
        EntityLiving entityliving = this.entity.getGoalTarget();

        if (entityliving == null) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else if (this.g != null && !this.g.isAssignableFrom(entityliving.getClass())) {
            return false;
        } else {
            this.target = this.entity.getNavigation().a(entityliving);
            return this.target != null;
        }
    }

    /*public boolean b() {
        EntityLiving entityliving = this.entity.getGoalTarget();
        if(entityliving == null){
        	return false;
        }
        if(!entityliving.isAlive()){
        	return false;
        }
        if(this.e){
        	return !this.entity.getNavigation().n();
        }
        //entity.c(double x, double y, double z) means entity.getInWater(double x, double y, double z);
        return this.entity.c(MathHelper.floor(entityliving.locX), MathHelper.floor(entityliving.locY), MathHelper.floor(entityliving.locZ));
    }*/

    public void c() {
        this.entity.getNavigation().a(this.target, this.speed);
        this.h = 0;
    }

    /*public void d() {
        this.entity.getNavigation().h();
    }*/

    public void e() {
        EntityLiving entityliving = this.entity.getGoalTarget();

        this.entity.getControllerLook().a(entityliving, 30.0F, 30.0F);
        double d0 = this.entity.e(entityliving.locX, entityliving.getBoundingBox().b, entityliving.locZ);
        double d1 = (double) (this.entity.width * 2.0F * this.entity.width * 2.0F + entityliving.width);

        --this.h;
        if ((this.e || this.entity.getEntitySenses().a(entityliving)) && this.h <= 0 && (this.i == 0.0D && this.j == 0.0D && this.k == 0.0D || entityliving.e(this.i, this.j, this.k) >= 1.0D || this.entity.getRandom().nextFloat() < 0.05F)) {
            this.i = entityliving.locX;
            this.j = entityliving.getBoundingBox().b;
            this.k = entityliving.locZ;
            this.h = 4 + this.entity.getRandom().nextInt(7);
            if (d0 > 1024.0D) {
                this.h += 10;
            } else if (d0 > 256.0D) {
                this.h += 5;
            }
            if (!this.entity.getNavigation().a((Entity) entityliving, this.speed)) {
                this.h += 15;
            }
        }

        this.c = Math.max(this.c - 1, 0);
        if (d0 <= d1 && this.c <= 20) {
            this.c = 20;
            //if (this.entity.be() != null) {
                //this.entity.ba();
            //}
            entityliving.damageEntity(DamageSource.GENERIC, (float) this.damage);
            //this.entity.a(entityliving);
        }
    }
}
