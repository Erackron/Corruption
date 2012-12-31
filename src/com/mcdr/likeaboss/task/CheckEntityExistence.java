<<<<<<< HEAD:src/com/mcdr/likeaboss/task/CheckEntityExistence.java
package com.mcdr.likeaboss.task;

import java.util.Iterator;
import java.util.List;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;


public class CheckEntityExistence extends BaseTask {
	@Override
	public void run() {
		List<Boss> bosses = LabEntityManager.getBosses();
		
		for (Iterator<Boss> it = bosses.iterator() ; it.hasNext() ; ) {
			if (!it.next().IsEntityAlive())
				it.remove();
		}
	}
}
=======
package com.mcdr.likeaboss.task;

import java.util.Iterator;
import java.util.List;

import com.mcdr.likeaboss.entity.Boss;
import com.mcdr.likeaboss.entity.LabEntityManager;


public class CheckEntityExistence extends BaseTask {
	@Override
	public void run() {
		List<Boss> bosses = LabEntityManager.getBosses();
		
		for (Iterator<Boss> it = bosses.iterator() ; it.hasNext() ; ) {
			if (!it.next().IsEntityAlive())
				it.remove();
		}
	}
}
>>>>>>> origin/EquipmentExpansion:src/com/mcdr/likeaboss/task/CheckEntityExistence.java
