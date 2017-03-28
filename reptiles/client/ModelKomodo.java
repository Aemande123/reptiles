/*
 * ModelKomodo.java
 *
 *  Copyright (c) 2017 Michael Sheppard
 *
 * =====GPL=============================================================
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

package com.reptiles.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import com.reptiles.common.EntityKomodo;
import net.minecraft.client.renderer.GlStateManager;

public class ModelKomodo extends ModelBase {

	public ModelKomodo()
	{
		float yPos = 16F;

		komodoBody = new ModelRenderer(this, 12, 8);
		komodoBody.addBox(-3F, -3F, -8F, 6, 8, 16);
		komodoBody.setRotationPoint(0.0F, yPos, 0.0F);

		komodoHead = new ModelRenderer(this, 0, 0);
		komodoHead.addBox(-3F, -3F, -8F, 6, 6, 8);
		komodoHead.setRotationPoint(0.0F, yPos, -8F);

		komodoLeg1 = new ModelRenderer(this, 48, 0);
		komodoLeg1.addBox(-2F, 0.0F, -2F, 4, 8, 4);
		komodoLeg1.setRotationPoint(5F, yPos, -5F);

		komodoLeg2 = new ModelRenderer(this, 48, 0);
		komodoLeg2.addBox(-2F, 0.0F, -2F, 4, 8, 4);
		komodoLeg2.setRotationPoint(5F, yPos, 5F);

		komodoLeg3 = new ModelRenderer(this, 48, 0);
		komodoLeg3.mirror = true;
		komodoLeg3.addBox(-2F, 0.0F, -2F, 4, 8, 4);
		komodoLeg3.setRotationPoint(-5F, yPos, -5F);

		komodoLeg4 = new ModelRenderer(this, 48, 0);
		komodoLeg4.mirror = true;
		komodoLeg4.addBox(-2F, 0.0F, -2F, 4, 8, 4);
		komodoLeg4.setRotationPoint(-5F, yPos, 5F);

		komodoTail = new ModelRenderer(this, 16, 8);
		komodoTail.addBox(-2F, -2F, 0.0F, 4, 4, 20);
		komodoTail.setRotationPoint(0.0F, yPos, 6F);
		komodoTail.rotateAngleX = 5.934119F;

		// komodoTongue = new ModelRenderer(this, 0, 14);
		// komodoTongue.addBox(-0.5F, -0.5F, -16F, 1, 1, 16);
		// komodoTongue.setRotationPoint(0.0F, yPos+2, -16F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);

		setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		// EntityKomodo entitykomodo = (EntityKomodo)entity;
		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24F * f5, 0.0F);
			komodoHead.render(f5);
			komodoBody.render(f5);
			komodoLeg1.render(f5);
			komodoLeg2.render(f5);
			komodoLeg3.render(f5);
			komodoLeg4.render(f5);
			komodoTail.render(f5);
			// if (entitykomodo.isSmelling())
			// komodoTongue.render(f5);

			GlStateManager.popMatrix();
		} else {
			komodoBody.render(f5);
			komodoHead.render(f5);
			komodoLeg1.render(f5);
			komodoLeg2.render(f5);
			komodoLeg3.render(f5);
			komodoLeg4.render(f5);
			komodoTail.render(f5);
			// if (entitykomodo.isSmelling())
			// komodoTongue.render(f5);
		}
	}
//float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

		komodoHead.rotateAngleX = headPitch * 0.0174532924F;
		komodoHead.rotateAngleY = netHeadYaw * 0.0174532924F;

		// komodoTongue.rotateAngleX = f4 / 57.29578F;
		// komodoTongue.rotateAngleY = f3 / 57.29578F;
		// wag the tail
		komodoTail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 0.4F * limbSwingAmount;
		// tongue moves up and down when out
		// komodoTongue.rotateAngleX = MathHelper.cos(f * 0.6662F) * 0.4F * f1;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float partialTicks)
	{
		EntityKomodo entitykomodo = (EntityKomodo) entityliving;

		if (entitykomodo.isSitting()) {
			float yPos = 19F;
			komodoBody.setRotationPoint(0.0F, yPos, 0.0F);
			komodoTail.setRotationPoint(0F, yPos + 2, 6F);
			komodoTail.rotateAngleX = 0.0F;
			komodoHead.setRotationPoint(0F, yPos, -8F);
			// komodoTongue.setRotationPoint(0F, yPos+2, -16F);

			komodoLeg1.setRotationPoint(5F, yPos + 3, -5F);
			komodoLeg2.setRotationPoint(5F, yPos + 3, 5F);
			komodoLeg3.setRotationPoint(-5F, yPos + 3, -5F);
			komodoLeg4.setRotationPoint(-5F, yPos + 3, 5F);

			komodoLeg1.rotateAngleX = 4.712389F;
			komodoLeg2.rotateAngleX = 1.570799F;
			komodoLeg3.rotateAngleX = 4.712389F;
			komodoLeg4.rotateAngleX = 1.570799F;
		} else {
			float yPos = 16F;
			komodoBody.setRotationPoint(0.0F, yPos, 0.0F);
			komodoHead.setRotationPoint(0F, yPos, -8F);
			komodoTail.setRotationPoint(0F, yPos, 6F);
			komodoTail.rotateAngleX = 5.934119F;
			// komodoTongue.setRotationPoint(0F, yPos+2, -16F);

			komodoLeg1.setRotationPoint(5F, yPos, -5F);
			komodoLeg2.setRotationPoint(5F, yPos, 5F);
			komodoLeg3.setRotationPoint(-5F, yPos, -5F);
			komodoLeg4.setRotationPoint(-5F, yPos, 5F);

			komodoLeg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
			komodoLeg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
			komodoLeg3.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f1;
			komodoLeg4.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		}
		// TODO: stick out tongue occasionally, render tongue inside head and
		// animate to front of head
		// moving tongue up and down at the same time.
		// if (entitykomodo.isSmelling()) {
		// komodoTongue.setRotationPoint(0F, yPos, zPos);
		// zPos++;
		// } else {
		// zPos--;
		// }
	}

	public ModelRenderer komodoBody;
	public ModelRenderer komodoHead;
	public ModelRenderer komodoLeg1;
	public ModelRenderer komodoLeg2;
	public ModelRenderer komodoLeg3;
	public ModelRenderer komodoLeg4;
	ModelRenderer komodoTail;
	// ModelRenderer komodoTongue;
}
