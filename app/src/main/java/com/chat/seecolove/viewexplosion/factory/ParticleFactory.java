package com.chat.seecolove.viewexplosion.factory;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.chat.seecolove.viewexplosion.particle.Particle;



public abstract class ParticleFactory {
    public abstract Particle[][] generateParticles(Bitmap bitmap, Rect bound);
}
