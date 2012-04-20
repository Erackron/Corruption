package cam.task;

import java.util.Iterator;
import java.util.List;

import cam.entity.Boss;
import cam.entity.LabEntityManager;
import cam.entity.Minion;

public class CheckEntityExistence extends BaseTask {
	@Override
	public void run() {
		List<Boss> bosses = LabEntityManager.getBosses();
		List<Minion> minions = LabEntityManager.getMinions();
		
		for (Iterator<Boss> it = bosses.iterator() ; it.hasNext() ; ) {
			if (!it.next().IsEntityAlive())
				it.remove();
		}
		
		for (Iterator<Minion> it = minions.iterator() ; it.hasNext() ; ) {
			if (!it.next().IsEntityAlive())
				it.remove();
		}
	}
}
