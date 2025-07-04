package com.testmod;

import com.testmod.abilities.dash.DashCommand;
import com.testmod.abilities.dash.DashPlayer;
import com.testmod.abilities.dash.DashSystem;
import com.testmod.item.ModItems;
import com.testmod.item.custom.DaggerItem;
import com.testmod.item.custom.TeleportItem;
import com.testmod.mana.*;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TestMod implements ModInitializer {
	public static final String MOD_ID = "testmod";
	public static final String MOD_VERSION = "0.0.1";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ArrayList<DashPlayer> dashes  = new ArrayList<DashPlayer>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Config.load(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/config.toml"));

		ManaSystem.initialize();
		DashSystem.initialize();
		DaggerItem.initialize();
		TeleportItem.initialize();
		ModItems.registerModItems();

		registerCommands();
		UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
			if (playerEntity.getMainHandStack().getItem().equals(Items.WOODEN_PICKAXE)){
				dashes.add(new DashPlayer(playerEntity));
			}
			return ActionResult.PASS;
		});

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
			if (!dashes.isEmpty()){
				for (int x = dashes.size()-1; x > -1 ; x--) {
					DashPlayer DashPlayer = dashes.get(x);
					boolean ended = DashPlayer.decrementTime();
					if (DashPlayer.getTime()%DashPlayer.getInterval() == 0){
						PlayerEntity player = DashPlayer.getPlayer();
						Vec3d LookDirection = player.getRotationVector().normalize();
						Vec3d addedVelocity = LookDirection.multiply(DashSystem.getDefaultPower());
						Vec3d newVelocity = player.getVelocity().add(addedVelocity);
						player.setVelocity(newVelocity);
					}
					if (ended){
						dashes.remove(x);
					}
				}
			}
		});


	}


	private void registerCommands() {
		// main mod command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal(TestMod.MOD_ID)
					.requires(src -> Permissions.check(src, TestMod.MOD_ID + ".commands.about", 0))
					.executes(context -> {
						context.getSource().sendFeedback(() -> Text.literal("This server is running " + TestMod.MOD_ID + " version " + TestMod.MOD_VERSION + "."), false);
						return 1;
					})
					.then(CommandManager.literal("reload")
							.requires(src -> Permissions.check(src, TestMod.MOD_ID + ".commands.reload", 3))
							.executes(Config::reload))
					);
		});
		ManaCommand.register();
		DashCommand.register();
	}
}