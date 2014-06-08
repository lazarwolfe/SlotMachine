package io.github.lazarwolfe.slots;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Lever;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SlotMachinePlugin extends JavaPlugin {
	
	private String pluginName = "Slots";
	private String version = "0.1";
	
	private LinkedList <SlotMachine> machines = new LinkedList<SlotMachine>();
	
	private static SlotMachinePlugin instance = null;
	public static SlotMachinePlugin getInstance() {
		return instance;
	}
	
	@Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SlotMachineLeverListener(this),  this);
		instance = this;
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
    	if (sender instanceof Player) {
    		if (command.getName().equalsIgnoreCase("SlotsVersion")) {
    			((Player)sender).sendMessage(pluginName+" "+version);
    		}
    	}
    	return false;
    }
    
    /**
     * Checks to see if there is a lot machine currently registered, or if one is built but not registered.
     * If it cannot find either, it checks to see if it should build one.
     * @param Player The player who flipped the lever.
     * @param leverBlock The lever which identifies the slot machine.
     * @return A working machine, or null if none are found.
     */
    public SlotMachine getSlotMachine(Player player, Block leverBlock)
    {
    	SlotMachine machine;
		// TODO: Check to see if the lever has a slot machine enchantment.
	    BlockState state = leverBlock.getState();
	    Lever lever = (Lever)state.getData();
    	// Do we already know about this slot machine?
    	Iterator<SlotMachine> iterator = machines.iterator();
    	while (iterator.hasNext()) {
    		machine = iterator.next();
	    	player.sendMessage("===== Checking a machine.");
    		if (machine.leverBlock == leverBlock) {
    	    	player.sendMessage("===== Found a machine.");
    			return machine;
    		}
    	}
    	// Is there a machine properly set up?
    	machine = SlotMachine.getExistingMachine(this, leverBlock, player);
    	if (machine != null) {
	    	player.sendMessage("===== Added a new machine.");
    		machines.add(machine);
    		return machine;
    	}
    	// TODO: Determine if the lever has the correct enchantment to allow creation of a SlotMachine.
    	if (true) {
	    	player.sendMessage("===== Trying to create a machine.");
    		machine = SlotMachine.createMachine(this, player, leverBlock);
    		if (machine != null) {
        		machines.add(machine);
            	player.sendMessage("Slot Machine created.");
        		lever.setPowered(false);
    		}
    		else
    		{
    	    	player.sendMessage("===== Failed to create a machine.");
    		}
    	}
    	return machine;
    }
    
}