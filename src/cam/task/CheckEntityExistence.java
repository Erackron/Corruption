package cam.task;

import java.util.Iterator;
import java.util.List;

import cam.entity.Boss;
import cam.entity.LabEntityManager;

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
