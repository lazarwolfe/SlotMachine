package io.github.lazarwolfe.slots.Architect;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

/**
 * A blueprint is a template for something to build, centered at 0,0,0, facing NORTH.
 * @author Lazar
 */
public class Blueprint {
	public LinkedList <BlueprintBlock> blocks = new LinkedList<BlueprintBlock>();
	public LinkedList <BlueprintEntity> entities = new LinkedList<BlueprintEntity>();

	public Blueprint()
	{
		
	}
	
	public void AddBlock(Material material, int x, int y, int z)
	{
		blocks.add(new BlueprintBlock(material, x, y, z));
	}
	
	public void AddEntity(EntityType entityType, int x, int y, int z, BlockFace blockFace)
	{
		entities.add(new BlueprintEntity(entityType, x, y, z, blockFace));
	}
}
