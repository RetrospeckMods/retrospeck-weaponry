package com.retrospeck.testmod;

import com.retrospeck.testmod.abilities.dash.DashKeybind;
import com.retrospeck.testmod.item.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.DamageParticle;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.BACKSTAB_PARTICLE, DamageParticle.Factory::new);
        DashKeybind.init();
    }
}
