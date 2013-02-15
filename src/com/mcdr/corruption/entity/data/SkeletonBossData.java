package com.mcdr.corruption.entity.data;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton.SkeletonType;

public class SkeletonBossData extends BossData {
	private SkeletonType skeletonType;
	
	public SkeletonBossData(String name, EntityType entityType, boolean isWitherSkeleton){
		super(name, entityType);
		skeletonType = isWitherSkeleton?SkeletonType.WITHER:SkeletonType.NORMAL;
	}
	
	public SkeletonType getSkeletonType() {
		return getEntityType()==EntityType.SKELETON?skeletonType:null;
	}

	public void setSkeletonType(SkeletonType skeletonType) {
		this.skeletonType = skeletonType;
	}
}
