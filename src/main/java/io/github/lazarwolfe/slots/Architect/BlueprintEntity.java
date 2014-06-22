package io.github.lazarwolfe.slots.Architect;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

/**
 * A single entity to be placed in a blueprint.
 * @author Lazar
 */
public class BlueprintEntity {
	public EntityType entityType;
	public int x;
	public int y;
	public int z;
	public BlockFace blockFace;
	
	public BlueprintEntity(EntityType entityType, int x, int y, int z, BlockFace blockFace) {
		this.entityType = entityType;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockFace = blockFace;
	}
	
	public BlueprintEntity(BlueprintEntity entity) {
		entityType = entity.entityType;
		x = entity.x;
		y = entity.y;
		z = entity.z;
		blockFace = entity.blockFace;
	}

}
