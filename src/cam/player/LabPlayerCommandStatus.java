package cam.player;

public class LabPlayerCommandStatus {
	
	private boolean viewer = false;
	private boolean ignore = false;
		
	public boolean getViewer() {
		return viewer;
	}
		
	public boolean getIgnore() {
		return ignore;
	}
		
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}
		
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
}
