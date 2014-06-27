package io.github.lazarwolfe.slots;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

import io.github.lazarwolfe.slots.Architect.Blueprint;

public class SlotMachineTemplate {
	
	/**
	 * The number of spinners for the machine, usually 3.
	 */
	public int numSpinners;
	
	/**
	 * The cycle of materials for each spinners.
	 */
	public Material[][] spinnerContents;
	/**
	 * The time at which each spinners stops in turn.
	 */
    public int[] duration;
    /**
     * One Reward for ever material used in spinnerContents.
     */
    public static SlotMachineReward[] reward;
    
    /**
     * The shape of this machine, which will not be read in by a config file.
     */
    public Blueprint blueprint;
    
    /**
     * The type of machine this is, which will decide what config file to load
     * and what block to place behind each item frame.
     */
    public Material type;
    
    public SlotMachineTemplate(Material type)
    {
    	this.type = type;
    	InitData();
    	InitBlueprint();
    }
    
    /**
     * Initializes spinner and reward data.
     */
    protected void InitData()
    {
        // TODO: Read this block of data from a config file, based on type.
    	numSpinners = 3;
    	
    	spinnerContents = new Material[numSpinners][];
    	spinnerContents[0] = new Material[3];
    	spinnerContents[0][0] = Material.APPLE;
    	spinnerContents[0][1] = Material.BONE;
    	spinnerContents[0][2] = Material.DIAMOND;
    	spinnerContents[1] = new Material[3];
    	spinnerContents[1][0] = Material.APPLE;
    	spinnerContents[1][1] = Material.BONE;
    	spinnerContents[1][2] = Material.DIAMOND;
    	spinnerContents[2] = new Material[5];
    	spinnerContents[2][0] = Material.APPLE;
    	spinnerContents[2][1] = Material.BONE;
    	spinnerContents[2][2] = Material.APPLE;
    	spinnerContents[2][3] = Material.BONE;
    	spinnerContents[2][4] = Material.DIAMOND;
    	
    	duration = new int[numSpinners];
    	duration[0] = 20;
    	duration[1] = 40;
    	duration[2] = 70;
    	
    	reward = new SlotMachineReward[3];
        reward[0] = new SlotMachineReward(Material.APPLE, Material.APPLE, 3, 0, 1);
        reward[1] = new SlotMachineReward(Material.BONE, Material.AIR, 0, 10, 2);
        reward[2] = new SlotMachineReward(Material.DIAMOND, Material.NETHER_STAR, 1, 0, 5);
    }

	protected void InitBlueprint()
	{
		int x;
		blueprint = new Blueprint();
		
		// The lever is at 0,0,0, and is not part of the blueprint.
		
		// Staring from the far left, specify each spinner.
		for (x=-numSpinners; x<0; x++) {
			blueprint.AddBlock(type, x, 0, 1);
			blueprint.AddEntity(EntityType.ITEM_FRAME, x, 0, 0, BlockFace.SOUTH);
		}
		
		// TODO: Set up signs.
		int midway = -((numSpinners+1)/2);
		blueprint.AddBlock(Material.SIGN, 0, 1, 0);
		blueprint.AddBlock(Material.SIGN, midway, 1, 0);
		
		// Set up the reward chest.
		blueprint.AddBlock(Material.CHEST, midway, -1, 0);
	}
}
