package io.github.lazarwolfe.slots.Architect;

import java.util.LinkedList;

/**
 * A blueprint is a template for something to build, centered at 0,0, facing NORTH.
 * @author Lazar
 */
public class Blueprint {
	public LinkedList <BlueprintBlock> blocks = new LinkedList<BlueprintBlock>();
	public LinkedList <BlueprintEntity> entities = new LinkedList<BlueprintEntity>();

	public Blueprint()
	{
		
	}
}
