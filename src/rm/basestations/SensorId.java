package rm.basestations;

public class SensorId {
	public int sensorIndex;
	public int parkometerIndex;
	public int basestationIndex;
	
	public SensorId() {
		this.sensorIndex = this.parkometerIndex = this.basestationIndex = 0;
	}
	
	public SensorId(int s, int p, int b) {
		this.sensorIndex = s;
		this.parkometerIndex = p;
		this.basestationIndex = b;
	}
	
	
	public int toInt() {
		int result = sensorIndex + parkometerIndex * 100 + basestationIndex * 100000;
		
		return result;
	}
	
	public static SensorId toSensorId(int toInt) {
		SensorId id = new SensorId();
		id.basestationIndex = toInt / 100000;
		id.parkometerIndex = (toInt - id.basestationIndex * 100000)/100;
		id.sensorIndex = toInt - id.basestationIndex * 100000 - id.parkometerIndex * 100;
		return id;
	}
}
