package rm;

import java.sql.Time;

public class SpotPhoto{
	public int localSpotId;
	public Time photoTime;
	public String plateNumber;
	public SpotPhoto(int localSpotId, Time photoTime, String plateNumber){
		this.localSpotId = localSpotId ;
		this.photoTime = photoTime;
		this.plateNumber = plateNumber;
	}
	public boolean isRelatedTo(SpotPhoto s) {
		if(! this.plateNumber.equals(s.plateNumber)) {
			return false;
		}
		if(this.localSpotId != s.localSpotId) {
			return false;
		}
		//TODO: check if the given SpotPhoto is related to this
		return true;
	}
}