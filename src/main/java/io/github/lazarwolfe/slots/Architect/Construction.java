package io.github.lazarwolfe.slots.Architect;

import java.util.Iterator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Construction extends Blueprint {

	public BlockFace blockFace = BlockFace.NORTH;
	
	public Construction(Blueprint baseBlueprint, Location loc, BlockFace blockFace)
	{
    	Iterator<BlueprintBlock> blockIterator = baseBlueprint.blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		blocks.add(new BlueprintBlock(block));
    	}
    	Iterator<BlueprintEntity> entityIterator = baseBlueprint.entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		entities.add(new BlueprintEntity(entity));
    	}
    	SetFace(blockFace);
    	Translate(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	/**
	 * Checks that all positions in the blueprint are empty.
	 * @param world The world to check.
	 * @param player Optional.  If non-null, this player will receive error messages.
	 * @return true if the space is empty.
	 */
	public Boolean CheckAvailableSpace(World world, Player player)
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		if (!CheckLocationEmpty(block.x, block.y, block.z, world, player)) {
    			return false;
    		}
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		if (!CheckLocationEmpty(entity.x, entity.y, entity.z, world, player)) {
    			return false;
    		}
    	}
		return true;
	}
	
	/**
	 * Check that one location is empty.  That is, the block is AIR, and there are no PAINTINGS or ITEM_FRAMES there.
	 * @param x Loc to check.
	 * @param y Loc to check.
	 * @param z Loc to check.
	 * @param world The world to check.
	 * @param player Optional.  If non-null, this player will receive error messages.
	 * @return true if the space is empty.
	 */
	protected Boolean CheckLocationEmpty(int x, int y, int z, World world, Player player)
	{
		Block checkBlock = world.getBlockAt(x, y, z);
		if (checkBlock.getType() != Material.AIR) {
			return false;
		}
	    Chunk chunk = world.getChunkAt(checkBlock);
	    for (Entity entity : chunk.getEntities()) {
	    	if (entity.getType() == EntityType.ITEM_FRAME || entity.getType() == EntityType.PAINTING) {
	    		Location entityLoc = entity.getLocation();
	    		if (entityLoc.getBlockX() == x &&
	    			entityLoc.getBlockY() == y &&
	    			entityLoc.getBlockZ() == z)
	    		{
	    			return false;
	    		}
	    	}
	    }
		return true;
	}
	
	/**
	 * Checks that all locations and entities are present where they are supposed to be.
	 * @param world The world to check.
	 * @param player Optional.  If non-null, this player will receive error messages.
	 * @return true if everything is in place.
	 */
	public Boolean CheckBuildSuccessful(World world, Player player)
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		Block checkBlock = world.getBlockAt(block.x, block.y, block.z);
    		if (checkBlock.getType() != block.material) {
//				player.sendMessage("Block at ("+block.x+","+block.y+","+block.z+") should be "+block.material.name()+" but is "+checkBlock.getType().name());
    			return false;
    		}
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		if (GetEntityAt(world, entity.entityType, entity.x, entity.y, entity.z, player) == null) {
//    			player.sendMessage("Desired entity "+entity.entityType.name()+" not found at ("+entity.x+","+entity.y+","+entity.z);
    	    	return false;
    		}
    	}
		return true;
	}
	
	/**
	 * Sets the facing of the entire blueprint.  Blueprints start facing north.
	 * This rotates all blocks and entities around 0,0,0.
	 * @param newBlockFace
	 */
	public void SetFace(BlockFace newBlockFace)
	{
		if (newBlockFace == blockFace) {
			return;
		} else if (newBlockFace == blockFace.getOppositeFace()) {
			Rotate180();
		} else if (newBlockFace == GetLeft90(blockFace)) {
			RotateLeft90();
		} else if (newBlockFace == GetRight90(blockFace)) {
			RotateRight90();
		} else {
			return;
		}
		blockFace = newBlockFace;
	}
	
	/**
	 * Translates all blocks and entities by the offset provided.
	 */
	public void Translate(int dx, int dy, int dz)
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		block.x += dx;
    		block.y += dy;
    		block.z += dz;
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		entity.x += dx;
    		entity.y += dy;
    		entity.z += dz;
    	}
	}
	
	/**
	 * Rotates all blocks and entities left 90 degrees around 0,0,0.
	 */
	protected void RotateLeft90()
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		int newX = block.z;
    		int newZ = -block.x;
    		block.x = newX;
    		block.z = newZ;
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		int newX = entity.z;
    		int newZ = -entity.x;
    		entity.x = newX;
    		entity.z = newZ;
    		entity.blockFace = GetLeft90(entity.blockFace);
    	}
	}
	
	/**
	 * Rotates all blocks and entities right 90 degrees around 0,0,0.
	 */
	protected void RotateRight90()
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		int newX = -block.z;
    		int newZ = block.x;
    		block.x = newX;
    		block.z = newZ;
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		int newX = -entity.z;
    		int newZ = entity.x;
    		entity.x = newX;
    		entity.z = newZ;
    		entity.blockFace = GetRight90(entity.blockFace);
    	}
	}
	
	/**
	 * Rotates all blocks and entities 180 degrees around 0,0,0.
	 */
	protected void Rotate180()
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		block.x = -block.x;
    		block.z = -block.z;
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		entity.x = -entity.x;
    		entity.z = -entity.z;
    		entity.blockFace = entity.blockFace.getOppositeFace();
    	}
	}
	
	// These two helper functions should be functions of BlockFace, IMHO.
	protected BlockFace GetLeft90(BlockFace blockFace)
	{
		switch (blockFace) {
		case NORTH:
			return BlockFace.WEST;
		case SOUTH:
			return BlockFace.EAST;
		case EAST:
			return BlockFace.NORTH;
		case WEST:
			return BlockFace.SOUTH;
		default:
			// We don't deal with diagonals.
			return blockFace;
		}
	}
	
	// These two helper functions should be functions of BlockFace, IMHO.
	protected BlockFace GetRight90(BlockFace blockFace)
	{
		switch (blockFace) {
		case NORTH:
			return BlockFace.EAST;
		case SOUTH:
			return BlockFace.WEST;
		case EAST:
			return BlockFace.SOUTH;
		case WEST:
			return BlockFace.NORTH;
		default:
			// We don't deal with diagonals.
			return BlockFace.SELF;
		}
	}
	
	/**
	 * Returns the Nth block of the specified material in this construction.
	 * @param world The world to check.
	 * @param material The material to check.
	 * @param index Index of the block to return.
	 * @return The block, or null if not found.
	 */
	public Block GetBlock(World world, Material material, int index)
	{
		int iFound = 0;
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		if (block.material == material) {
    			if (iFound == index) {
    	    		Block checkBlock = world.getBlockAt(block.x, block.y, block.z);
    	    		if (checkBlock.getType() == block.material) {
    					return checkBlock;
    	    		} else {
    	    			return null;
    	    		}
    			}
    			++iFound;
    		}
    	}
		return null;
	}
	
	/**
	 * Returns the Nth block of the specified material in this construction.
	 * @param world The world to check.
	 * @param entityType The type of entity to check.
	 * @param index Index of the entity to return.
	 * @return The entity, or null if not found.
	 */
	public Entity GetEntity(World world, EntityType entityType, int index, Player player)
	{
		int iFound = 0;
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		if (entity.entityType == entityType) {
    			if (iFound == index) {
    				return GetEntityAt(world, entityType, entity.x, entity.y, entity.z, player);
    			}
    			++iFound;
    		}
    	}
		return null;
	}
	
	// This also should be a common, optimized helper function
	protected Entity GetEntityAt(World world, EntityType type, int x, int y, int z, Player player)
	{
//		player.sendMessage("GetEntityAt looking for a "+type.name()+" at ("+x+","+y+","+z+")");
		Location loc = new Location(world, x, y, z);
	    Chunk chunk = world.getChunkAt(loc);
	    for (Entity checkEntity : chunk.getEntities()) {
	    	if (checkEntity.getType() == type) {
	    		Location entityLoc = checkEntity.getLocation();
//    			player.sendMessage("GetEntityAt found a "+type.name()+" at ("+entityLoc.getBlockX()+","+entityLoc.getBlockY()+","+entityLoc.getBlockZ()+")");
		    	if (entityLoc.getBlockX() == x && entityLoc.getBlockY() == y && entityLoc.getBlockZ() == z) {
		    		return checkEntity;
		    	}
	    	}
	    }
		return null;
	}
}
