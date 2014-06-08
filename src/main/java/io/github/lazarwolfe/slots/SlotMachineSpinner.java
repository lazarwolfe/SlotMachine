package io.github.lazarwolfe.slots;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SlotMachineSpinner extends BukkitRunnable {

	public SlotMachine machine;
	public ItemFrame itemFrame;
	public Material[] contents;
	public int index = 0;
	public int duration;
	
	public SlotMachineSpinner(SlotMachine machine, ItemFrame itemFrame, Material[] contents) {
		this.machine = machine;
		this.itemFrame = itemFrame;
		this.contents = contents;
		itemFrame.setItem(new ItemStack(contents[0]));
	}
	
	@Override
	public void run() {
		--duration;
		if (duration <= 0) {
			cancel();
			machine.onSlotDone(this);
		} else {
			++index;
			if (index == contents.length)
				index = 0;
			ItemStack item = itemFrame.getItem();
			item.setType(contents[index]);
			itemFrame.setItem(item);
		}
	}

}
