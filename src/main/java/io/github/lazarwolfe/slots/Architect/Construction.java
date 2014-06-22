package io.github.lazarwolfe.slots.Architect;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class Construction extends Blueprint {

	public BlockFace blockFace = BlockFace.NORTH;
	
	public Construction (Blueprint baseBlueprint, Location loc, BlockFace blockFace)
	{
    	Iterator<BlueprintBlock> blockIterator = blocks.iterator();
    	BlueprintBlock block;
    	while (blockIterator.hasNext()) {
    		block = blockIterator.next();
    		blocks.add(new BlueprintBlock(block));
    	}
    	Iterator<BlueprintEntity> entityIterator = entities.iterator();
    	BlueprintEntity entity;
    	while (entityIterator.hasNext()) {
    		entity = entityIterator.next();
    		entities.add(new BlueprintEntity(entity));
    	}
    	SetFace(blockFace);
    	Translate(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	/**
	 * Sets the facing of the entire blueprint.  Blueprints start facing north.
	 * This rotates all blocks and entities around 0,0.
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
	 * Rotates all blocks and entities left 90 degrees around 0,0.
	 */
	protected void Translate(int dx, int dy, int dz)
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
	 * Rotates all blocks and entities left 90 degrees around 0,0.
	 */
	protected void RotateLeft90()
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
    		entity.blockFace = GetLeft90(entity.blockFace);
    	}
	}
	
	/**
	 * Rotates all blocks and entities right 90 degrees around 0,0.
	 */
	protected void RotateRight90()
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
    		entity.blockFace = GetRight90(entity.blockFace);
    	}
	}
	
	/**
	 * Rotates all blocks and entities 180 degrees around 0,0.
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
	
	// These three helper functions should be functions of BlockFace, IMHO.
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
	
	// These three helper functions should be functions of BlockFace, IMHO.
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
}
