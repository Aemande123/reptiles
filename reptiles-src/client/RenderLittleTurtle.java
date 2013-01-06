//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package reptiles.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

//
// Copyright 2011 Michael Sheppard (crackedEgg)
//
import org.lwjgl.opengl.GL11;

import reptiles.common.EntityLittleTurtle;

public class RenderLittleTurtle extends RenderLiving
{
  public RenderLittleTurtle(ModelBase modelbase, float shadowSize) {
    super(modelbase, shadowSize);
  }
  
  public void renderLittleTurtle(EntityLittleTurtle entitytortoise, double d, double d1, double d2, float f, float f1) {
    super.doRenderLiving(entitytortoise, d, d1, d2, f, f1);
  }

  public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
    renderLittleTurtle((EntityLittleTurtle)entity, d, d1, d2, f, f1);
  }
  
  protected void scaleEntity(EntityLittleTurtle entityturtle, float f) {
    GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
  }
  
  protected void preRenderCallback(EntityLiving entityliving, float f) {
  	scaleEntity((EntityLittleTurtle)entityliving, f);
  }
  
  private final float scaleFactor = 0.5F;
}