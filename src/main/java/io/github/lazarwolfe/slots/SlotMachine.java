package io.github.lazarwolfe.slots;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

public class SlotMachine {
	public SlotMachinePlugin plugin;
	public Player player;
	public Block leverBlock;
	public Lever lever;
    public ItemFrame frame[];
    public Block chestBlock;
    public SlotMachineSpinner spinner[];
    public int cost;
    
    // TODO: Read this block of data from a config file.
    // TODO: Add support for multiple types of slot machines.
	public static int NUM_SLOTS = 3;
    private static Material[][] spinnerContents = {
    	// Materials that will spin in slot 0
        { Material.APPLE, Material.BONE, Material.DIAMOND },
        // Materials that will spin in slot 1
        { Material.APPLE, Material.BONE, Material.DIAMOND },
        // Materials that will spin in slot 2
        { Material.APPLE, Material.BONE, Material.APPLE, Material.BONE, Material.DIAMOND }
    };
    private static SlotMachineReward[] reward = {
    	new SlotMachineReward(Material.APPLE, Material.APPLE, 3, 0, 1),
    	new SlotMachineReward(Material.BONE, Material.AIR, 0, 10, 2),
    	new SlotMachineReward(Material.DIAMOND, Material.NETHER_STAR, 1, 0, 5)
    };
    private static int[] duration = { 20, 40, 70 };
    
	
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
	    int i;
	    World world = leverBlock.getWorld();
	    Location loc = leverBlock.getLocation();
	    BlockState state = leverBlock.getState();
	    Lever lever = (Lever)state.getData();
	    BlockFace face = lever.getFacing();
	    Location slotLoc[];
	    slotLoc = new Location[NUM_SLOTS+1];
	    ItemFrame frame[];
	    frame = new ItemFrame[NUM_SLOTS];
	    // TODO: Make sure the lever has a valid slot machine enchantment.
	    switch (face) {
	    case NORTH:	// TODO: BUG: Find out why North and East don't work right.
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()+i+1, loc.getY(), loc.getZ());
	    	}
	    	break;
	    case SOUTH:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()-i-1, loc.getY(), loc.getZ());
	    	}
	    	break;
	    case EAST:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		player.sendMessage("===== Item Frame At: "+(loc.getZ()+i+1));
	    		slotLoc[i] = new Location(world, loc.getX(), loc.getY(), loc.getZ()+i+1);
	    	}
	    	break;
	    case WEST:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX(), loc.getY(), loc.getZ()-i-1);
	    	}
	    	break;
    	default:
    		return null;
	    }
	    // Make sure we have a reward chest.
	    loc = slotLoc[NUM_SLOTS/2];
	    slotLoc[NUM_SLOTS-1] = new Location(world, loc.getX(), loc.getY()-1, loc.getZ());
	    Block chestBlock;
	    chestBlock = world.getBlockAt(slotLoc[NUM_SLOTS-1]);
		if (chestBlock == null || chestBlock.getType() != Material.CHEST) {
			return null;
		}
	    // Make sure we have the item frames.
	    Chunk chunk = world.getChunkAt(loc);
	    int itemFramesFound = 0;
	    for (Entity entity : chunk.getEntities()) {
	    	if (entity.getType() == EntityType.ITEM_FRAME) {
	    		Location entityLoc = entity.getLocation();
	    		for (i=itemFramesFound; i<NUM_SLOTS; ++i) {
	    			if (slotLoc[i].getBlockX() == entityLoc.getBlockX() &&
	    				slotLoc[i].getBlockY() == entityLoc.getBlockY() &&
	    				slotLoc[i].getBlockZ() == entityLoc.getBlockZ())
	    			{
	    	    		player.sendMessage("===== Found Item Frame At: "+entityLoc.getBlockZ());
	    				frame[itemFramesFound] = (ItemFrame)entity;
	    				++itemFramesFound;
	    			}
	    		}
	    		if (itemFramesFound == NUM_SLOTS) {
	    			break;
	    		}
	    	}
	    }
	    if (itemFramesFound != NUM_SLOTS) {
	    	chunk = world.getChunkAt(slotLoc[NUM_SLOTS-1]);
		    for (Entity entity : chunk.getEntities()) {
		    	if (entity.getType() == EntityType.ITEM_FRAME) {
		    		Location entityLoc = entity.getLocation();
		    		for (i=itemFramesFound; i<NUM_SLOTS; ++i) {
		    			if (slotLoc[i].getBlockX() == entityLoc.getBlockX() &&
		    				slotLoc[i].getBlockY() == entityLoc.getBlockY() &&
		    				slotLoc[i].getBlockZ() == entityLoc.getBlockZ())
		    			{
		    	    		player.sendMessage("===== Found Item Frame At: "+entityLoc.getBlockZ());
		    				frame[itemFramesFound] = (ItemFrame)entity;
		    				++itemFramesFound;
		    			}
		    		}
		    		if (itemFramesFound == NUM_SLOTS) {
		    			break;
		    		}
		    	}
		    }
	    }
	    if (itemFramesFound != NUM_SLOTS) {
			return null;
	    }
	    
	    // Now we have all the pieces, so build the slot machine.
	    SlotMachine machine = new SlotMachine(plugin);
	    machine.leverBlock = leverBlock;
	    machine.lever = lever;
	    machine.frame = frame;
	    machine.chestBlock = chestBlock;
	    machine.cost = 100;	// TODO: Make the cost depend on the level of enchantment of the lever.
		machine.spinner = new SlotMachineSpinner[NUM_SLOTS];
		for (i=0; i<NUM_SLOTS; ++i) {
			machine.spinner[i] = new SlotMachineSpinner(machine, frame[i], spinnerContents[i]);
		}
		return machine;
	}
	
	/**
	 * Creates a slot machine, either for testing purposes or because the lever
	 * has an enchantment that allows the slot machine to create itself.
	 * Warning: This is potentially destructive.  There are no checks to see what,
	 * if anything, is being written over.
	 * @param player The player that clicked on the lever.
	 * @param leverBlock The Block which is the lever.
	 */
	public static SlotMachine createMachine(SlotMachinePlugin plugin, Player player, Block leverBlock)
	{
		World world = leverBlock.getWorld();
	    Location loc = leverBlock.getLocation();
	    BlockState state = leverBlock.getState();
	    Lever lever = (Lever)state.getData();
		BlockFace face = lever.getFacing();
		BlockFace frameFace = face.getOppositeFace();
		HangingPlaceEvent hEvent;
	    Location slotLoc[];
	    int i;
	    slotLoc = new Location[NUM_SLOTS+1];
	    switch (face) {
	    case NORTH:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()+i+1, loc.getY(), loc.getZ()+1);
	    	}
		    slotLoc[NUM_SLOTS] = new Location(world, loc.getX()+2, loc.getY()-1, loc.getZ());
		    frameFace = BlockFace.SOUTH;	// TODO: Bug. Figure out why item frames are on the wrong side.
	    	break;
	    case SOUTH:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()-i-1, loc.getY(), loc.getZ()-1);
	    	}
		    slotLoc[NUM_SLOTS] = new Location(world, loc.getX()-2, loc.getY()-1, loc.getZ());
	    	break;
	    case EAST:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()-1, loc.getY(), loc.getZ()+i+1);
	    	}
		    slotLoc[NUM_SLOTS] = new Location(world, loc.getX(), loc.getY()-1, loc.getZ()+2);
		    frameFace = BlockFace.WEST;	// TODO: Bug. Figure out why item frames are on the wrong side.
	    	break;
	    case WEST:
	    	for (i=0; i<NUM_SLOTS; ++i) {
	    		slotLoc[i] = new Location(world, loc.getX()+1, loc.getY(), loc.getZ()-i-1);
	    	}
		    slotLoc[NUM_SLOTS] = new Location(world, loc.getX(), loc.getY()-1, loc.getZ()-2);
	    	break;
    	default:
    		return null;
	    }
	    Block block;
	    player.sendMessage("Creating signs facing: "+frameFace.toString());
    	for (i=0; i<NUM_SLOTS; ++i) {
    		block = world.getBlockAt(slotLoc[i]);
    		player.sendMessage("===== Creating Frame At: "+slotLoc[i].getBlockZ());
    		block.setType(Material.QUARTZ_BLOCK);
	    	hEvent = new HangingPlaceEvent(world.spawn(slotLoc[i], ItemFrame.class), player, block, frameFace);
        	Bukkit.getServer().getPluginManager().callEvent(hEvent);
    	}
    	block = slotLoc[NUM_SLOTS].getBlock();
    	block.setType(Material.CHEST);
    	
    	// Now that we have tried to create the machine, make sure we got it right
	    SlotMachine machine = getExistingMachine(plugin, leverBlock, player);
	    if (machine != null) {
			// Success!
	    	player.sendMessage("Slot Machine created.");
	    } else {
			// Something went wrong :(
	    	player.sendMessage("Slot Machine creation failed.  Please try a different direction.");
	    }
		lever.setPowered(false);
		return machine;
 	}
	
	/**
	 * This starts all of the spinners going, deducts money from the player, and makes the reward chest
	 * protected to the player.
	 * @param player
	 */
	public void startSpin(Player player)
	{
		player.sendMessage("===== Strating a spin.");
	    // TODO: Deduct money from player.
		this.player = player;
		// TODO: Set ownership of Chest to player.
		for (int i=0; i<NUM_SLOTS; ++i) {
			spinner[i].duration = duration[i];
			spinner[i].runTaskTimerAsynchronously(plugin, i*2, 1);
		}
	}
	
	public void onSlotDone(SlotMachineSpinner doneSpinner) {
		if (doneSpinner != spinner[NUM_SLOTS-1]) {
			// This one is intermediate.  Can we play a sound?
			player.sendMessage("===== Spinner stopped.");
			return;
		}
		player.sendMessage("===== Spinner stopped.");
		// We have the last one, make a reward or not.
		Material winMaterial = spinner[0].itemFrame.getItem().getType();
		for (int i=1; i<NUM_SLOTS; ++i) {
			Material curMaterial = spinner[i].itemFrame.getItem().getType();
			if (curMaterial != winMaterial) {
				// We have a losing spin.  Can we play a bad sound?
				player.sendMessage("===== Player Lost.");
				return;
			}
		}
		// We have a winner!  Now give the player his reward.
		player.sendMessage("===== Player Won.");
		for (int i=0; i<reward.length; ++i) {
			if (reward[i].frameMaterial == winMaterial) {
				// Can we play a sound based on intensity?
				Chest chest = (Chest)chestBlock.getState();
				for (int m=0; m<reward[i].rewardMaterialCount; ++m) {
					player.sendMessage("===== Adding item to chest.");
					chest.getInventory().addItem(new ItemStack(reward[i].rewardMaterial));
				}
				player.sendMessage("===== Adding "+reward[i].rewardMoney+" to player.");
				// TODO: reward the player any money he got.
				return;
			}
		}
	}
}
