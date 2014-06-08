package io.github.lazarwolfe.slots;

import org.bukkit.Material;

public class SlotMachineReward {
	Material frameMaterial;
	Material rewardMaterial;
	int rewardMaterialCount;
	int rewardMoney;
	int rewardIntensity;
	
	public SlotMachineReward(Material frameMaterial, Material rewardMaterial, int rewardMaterialCount, int rewardMoney, int rewardIntensity) {
		this.frameMaterial = frameMaterial;
		this.rewardMaterial = rewardMaterial;
		this.rewardMaterialCount = rewardMaterialCount;
		this.rewardMoney = rewardMoney;
		this.rewardIntensity = rewardIntensity;
	}
}
