package cam.player;

public class LabPlayerData {
	private boolean viewer;
	private boolean ignore;
	private boolean motdReceived;
	
	public boolean getViewer() {
		return viewer;
	}
		
	public boolean getIgnore() {
		return ignore;
	}
	
	public boolean getMotdReceived() {
		return motdReceived;
	}
		
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}
		
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
	public void setMotdReceived(boolean motdReceived) {
		this.motdReceived = motdReceived;
	}
}
