package io.github.lazarwolfe.slots;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Lever;

public class SlotMachineLeverListener implements Listener {

	public SlotMachinePlugin plugin;
	
	public static int SPIN_COST = 100;
	
	public SlotMachineLeverListener(SlotMachinePlugin plugin){
		this.plugin = plugin;
	}
	
	/* It surprises me that this event can be made this way.  It strikes me as
	 * very inefficient.  So I am going to do my best to make it abort as quickly
	 * as possible.
	 */
	@EventHandler
	public void SlotMachineLeverEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		// Are we even the right event?
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {	// TODO: Add a path for breaking a lever and unregistering the SlotMachine.
			return;
		}
		// Are we the right type of block?
		Block block = event.getClickedBlock();
		if (block == null || block.getType() != Material.LEVER) {
			return;
		}
		// Are we a powered lever?
	    BlockState state = block.getState();
	    Lever lever = (Lever)state.getData();
	    if (lever.isPowered()) {
	    	// We only work on unpowered levers.
	    	return;
	    }
		// Is the Slot Machine setup correct?
	    SlotMachine machine = plugin.getSlotMachine(player, block);
	    if (machine == null) {
	    	return;
	    }
	    // TODO: Determine if the player has enough money
	    Boolean enoughMoney = true;
	    if (!enoughMoney) {
	    	player.sendMessage("You need $"+machine.template.cost+" to play this machine.");
	    	return;
	    }

	    // Everything is valid, we can spin.
    	lever.setPowered(false);
		machine.startSpin(player);
	}
}
