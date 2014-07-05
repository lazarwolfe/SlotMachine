package io.github.lazarwolfe.slots.Architect;

import org.bukkit.Material;

/**
 * A single block to be placed in a blueprint.
 * @author Lazar
 */
public class BlueprintBlock {
	public Material material;
	public int x;
	public int y;
	public int z;
	// TODO: Do we want to add BlockFace?  How do we position stairs, for example?
	
	public BlueprintBlock(Material material, int x, int y, int z) {
		this.material = material;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlueprintBlock(BlueprintBlock block) {
		material = block.material;
		x = block.x;
		y = block.y;
		z = block.z;
	}
}
