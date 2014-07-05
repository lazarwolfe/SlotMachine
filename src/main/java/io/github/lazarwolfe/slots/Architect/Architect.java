package io.github.lazarwolfe.slots.Architect;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class Architect {
	World world;
	Player player;
	
	public Architect(World world, Player player)
	{
		this.world = world;
		this.player = player;
	}
	
	/**
	 * Get a structure that already exists in the world.
	 * @param blueprint Blueprint to fill.
	 * @param loc Location to start the construction.
	 * @param face Direction to rotate the blueprint to.
	 * @return The Construction object if it was found, null otherwise.
	 */
	public Construction GetExistingConstruction(Blueprint blueprint, Location loc, BlockFace face)
	{
		if (world == null) {
			return null;
		}
		Construction construction = new Construction(blueprint, loc, face);
    	if (!construction.CheckBuildSuccessful(world, player)) {
			return null;
    	}
		return construction;
	}
	
	/**
	 * Create a structure in the world.
	 * @param blueprint Blueprint to build.
	 * @param loc Location to start the build.
	 * @param face Direction to rotate the blueprint to.
	 * @return The Construction object if the build was successful, null otherwise.
	 */
	public Construction BuildBlueprint(Blueprint blueprint, Location loc, BlockFace face)
	{
		if (world == null) {
			return null;
		}
		Construction construction = new Construction(blueprint, loc, face);
		if (!construction.CheckAvailableSpace(world, player)) {
			return null;
		}
		// The space should be available, so none of this next should fail.
		Block block;
    	Iterator<BlueprintBlock> blockIterator = construction.blocks.iterator();
    	BlueprintBlock blueprintBlock;
    	while (blockIterator.hasNext()) {
    		blueprintBlock = blockIterator.next();
    		block = world.getBlockAt(blueprintBlock.x, blueprintBlock.y, blueprintBlock.z);
    		block.setType(blueprintBlock.material);
    		// TODO: Do we want to set orientation somehow?
    	}
    	Iterator<BlueprintEntity> entityIterator = construction.entities.iterator();
    	BlueprintEntity blueprintEntity;
    	while (entityIterator.hasNext()) {
    		blueprintEntity = entityIterator.next();
    		switch (blueprintEntity.entityType) {
    		case ITEM_FRAME:
    			switch (blueprintEntity.blockFace) {
    			case NORTH:
    				block = world.getBlockAt(blueprintEntity.x, blueprintEntity.y, blueprintEntity.z+1);
    				break;
    			case SOUTH:
    				block = world.getBlockAt(blueprintEntity.x, blueprintEntity.y, blueprintEntity.z-1);
    				break;
    			case EAST:
    				block = world.getBlockAt(blueprintEntity.x-1, blueprintEntity.y, blueprintEntity.z);
    				break;
    			case WEST:
    				block = world.getBlockAt(blueprintEntity.x+1, blueprintEntity.y, blueprintEntity.z);
    				break;
    			default:
        			continue;
    			}
    			HangingPlaceEvent hEvent = new HangingPlaceEvent(world.spawn(block.getLocation(), ItemFrame.class), player, block, blueprintEntity.blockFace);
            	Bukkit.getServer().getPluginManager().callEvent(hEvent);
    			break;
    		default:
    			break;
    		}
    	}
    	// Now check to see if we got everything placed correctly.
    	if (!construction.CheckBuildSuccessful(world, player)) {
//			player.sendMessage("[Architect] Build unsuccessful, demolishing.");
			DemolishBlueprint(blueprint, loc, face);
			return null;
    	}
		return construction;
	}
	
	/**
	 * Removes all blocks and entities that match this blueprint.
	 * @param blueprint Blueprint to demolish.
	 * @param loc Location to start the demolish.
	 * @param face Direction to rotate the blueprint to.
	 */
	public void DemolishBlueprint(Blueprint blueprint, Location loc, BlockFace face)
	{
		Construction construction = new Construction(blueprint, loc, face);
		Block block;
		// Remove all blocks that are ours.
    	Iterator<BlueprintBlock> blockIterator = construction.blocks.iterator();
    	BlueprintBlock blueprintBlock;
    	while (blockIterator.hasNext()) {
    		blueprintBlock = blockIterator.next();
    		block = world.getBlockAt(blueprintBlock.x, blueprintBlock.y, blueprintBlock.z);
    		if (block.getType() == blueprintBlock.material) {
    			block.setType(Material.AIR);
    		}
    	}
    	// Remove all entities that are ours.
    	Iterator<BlueprintEntity> entityIterator = construction.entities.iterator();
    	BlueprintEntity blueprintEntity;
    	while (entityIterator.hasNext()) {
    		blueprintEntity = entityIterator.next();
    		Block checkBlock = world.getBlockAt(blueprintEntity.x, blueprintEntity.y, blueprintEntity.z);
    	    Chunk chunk = world.getChunkAt(checkBlock);
    	    for (Entity entity : chunk.getEntities()) {
    	    	if (entity.getType() == blueprintEntity.entityType) {
    	    		Location entityLoc = entity.getLocation();
    	    		if (entityLoc.getBlockX() == blueprintEntity.x &&
    	    			entityLoc.getBlockY() == blueprintEntity.y &&
    	    			entityLoc.getBlockZ() == blueprintEntity.z)
    	    		{
    	    			if (blueprintEntity.entityType == EntityType.ITEM_FRAME) {
    	    				ItemFrame itemFrame = (ItemFrame)entity;
    	    				itemFrame.setItem(null);
    	    			}
    	    			entity.remove();
    	    		}
    	    	}
    	    }
    	}
	}
}
