package io.github.lazarwolfe.slots;

import io.github.lazarwolfe.slots.Architect.Architect;
import io.github.lazarwolfe.slots.Architect.Construction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

public class SlotMachine {
	public SlotMachinePlugin plugin;
	public Player player;
	public Block leverBlock;
	public Lever lever;
    public ItemFrame frames[];
    public Block chestBlock;
    public SlotMachineSpinner spinners[];
    public SlotMachineTemplate template;
	
	public SlotMachine(SlotMachinePlugin plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * Confirms the existence of all of the blocks and entities that make up the slot machine.
	 * @param leverBlock The Block which is the lever.
	 * @return Returns a new SlotMachine if there are valid components already in place.
	 */
	public static SlotMachine getExistingMachine(SlotMachinePlugin plugin, Block leverBlock, Player player)
	{
	    World world = leverBlock.getWorld();
	    Location loc = leverBlock.getLocation();
	    BlockState state = leverBlock.getState();
	    Lever lever = (Lever)state.getData();
	    BlockFace face = lever.getFacing();
	    SlotMachineTemplate template = plugin.GetTemplate(Material.QUARTZ_BLOCK);
	    Architect architect = new Architect(world, player);
	    Construction construction = architect.GetExistingConstruction(template.blueprint, loc, face.getOppositeFace());
	    if (construction == null) {
	    	return null;
	    }
	    return createMachineFromConstruction(plugin, leverBlock, player, construction, template, world, lever);
	}
	
	/**
	 * Creates a new slot machine.
	 * @param leverBlock The Block which is the lever.
	 * @return Returns a new SlotMachine, or null if creation failed.
	 */
	public static SlotMachine createMachine(SlotMachinePlugin plugin, Block leverBlock, Player player)
	{
	    World world = leverBlock.getWorld();
	    Location loc = leverBlock.getLocation();
	    BlockState state = leverBlock.getState();
	    Lever lever = (Lever)state.getData();
	    BlockFace face = lever.getFacing();
	    SlotMachineTemplate template = plugin.GetTemplate(Material.QUARTZ_BLOCK);
	    Architect architect = new Architect(world, player);
	    if (face == BlockFace.SOUTH || face == BlockFace.WEST) {
	    	player.sendMessage("[Slot Machine] Known bug: Cannot place item frames correctly facing this direction.");
	    	return null;
	    }
    	Construction construction = architect.BuildBlueprint(template.blueprint, loc, face.getOppositeFace());
	    if (construction == null) {
	    	return null;
	    }
	    return createMachineFromConstruction(plugin, leverBlock, player, construction, template, world, lever);
	}
	
	/**
	 * Boy this is a mess.  But it takes all the data from getExistingMachine or createMachine and builds
	 * a new SlotMachine from it.
	 */
	protected static SlotMachine createMachineFromConstruction(SlotMachinePlugin plugin, Block leverBlock, Player player, Construction construction, SlotMachineTemplate template, World world, Lever lever)
	{
		int i;
	    // Collect the pieces we need
	    ItemFrame frame[] = new ItemFrame[template.numSpinners];
	    for (i=0; i<template.numSpinners; ++i) {
	    	frame[i] = (ItemFrame)construction.GetEntity(world, EntityType.ITEM_FRAME, i, player);
	    	if (frame[i] == null) {
//		    	player.sendMessage("[SlotMachine] Failed to create SlotMachine, item frame "+i+" missing.");
		    	return null;
	    	}
	    }
	    Block chestBlock = construction.GetBlock(world, Material.CHEST, 0);
    	if (chestBlock == null) {
	    	return null;
    	}
    	
	    // Now we have all the pieces, so build the slot machine.
	    SlotMachine machine = new SlotMachine(plugin);
	    machine.leverBlock = leverBlock;
	    machine.lever = lever;
	    machine.frames = frame;
	    machine.chestBlock = chestBlock;
	    machine.template = template;
		machine.spinners = new SlotMachineSpinner[template.numSpinners];
		for (i=0; i<template.numSpinners; ++i) {
			machine.spinners[i] = new SlotMachineSpinner(machine, frame[i], template.spinnerContents[i]);
		}
		return machine;
	}
	
	/**
	 * This starts all of the spinners going, deducts money from the player, and makes the reward chest
	 * protected to the player.
	 * @param player
	 */
	public void startSpin(Player player)
	{
	    // TODO: Deduct money from player.
		this.player = player;
		// TODO: Set ownership of Chest to player.
		for (int i=0; i<template.numSpinners; ++i) {
			if (spinners[i].duration == -1) {
				spinners[i].runTaskTimer(plugin, i*2, 2);
			}
			spinners[i].duration = template.duration[i];
		}
	}
	
	/**
	 * When each spinner stops, this is called.
	 * @param doneSpinner
	 */
	public void onSlotDone(SlotMachineSpinner doneSpinner) {
		if (doneSpinner != spinners[template.numSpinners-1]) {
			// This one is intermediate.  Can we play a sound?
			return;
		}
		// We have the last one, make a reward or not.
		Material winMaterial = spinners[0].itemFrame.getItem().getType();
		for (int i=1; i<template.numSpinners; ++i) {
			Material curMaterial = spinners[i].itemFrame.getItem().getType();
			if (curMaterial != winMaterial) {
				// We have a losing spin.  Can we play a bad sound?
				return;
			}
		}
		// We have a winner!  Now give the player his reward.
		player.sendMessage("[SlotMachine] "+player.getDisplayName()+" Won!");
		for (int i=0; i<template.reward.length; ++i) {
			if (template.reward[i].frameMaterial == winMaterial) {
				// Can we play a sound based on intensity?
				Chest chest = (Chest)chestBlock.getState();
				for (int m=0; m<template.reward[i].rewardMaterialCount; ++m) {
					chest.getInventory().addItem(new ItemStack(template.reward[i].rewardMaterial));
				}
				// TODO: reward the player any money he got.
				return;
			}
		}
	}
}
