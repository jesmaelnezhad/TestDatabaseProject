package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import rm.basestations.Sensor;
import rm.basestations.SensorId;
import rm.basestations.Sensor.SensorSnapshot;
import rm.parking_structure.ParkingSpotContainer;
import utility.DBManager;
import utility.Permit;

public class SensorInfoMaterializer extends Thread{
	
	private ParkingSpotContainer container = null;
	public SensorInfoMaterializer(ParkingSpotContainer container) {
		this.container = container;
	}
	
	@Override
	public void run() {
        // Loop forever.
        
        while(true) {
            // Sleep for a while
            try {
                Thread.sleep(1800000);
            } catch (InterruptedException e) {
                // Interrupted exception will occur if
                // the Worker object's interrupt() method
                // is called. interrupt() is inherited
                // from the Thread class.
                break;
            }
            // 1. Get a read permit on citySensors and get the sensors to be updated.
            Permit citySensorsReadPermit = null;
            Map<SensorId, SensorSnapshot> toBeUpdated = new HashMap<>();
            try{
            	citySensorsReadPermit = container.getReadPermit();
            	Map<SensorId, Sensor> citySensors = container.citySensors;
                for(SensorId sensorId : citySensors.keySet()) {
                	if(citySensors.get(sensorId).isChanged()) {
                		toBeUpdated.put(sensorId, citySensors.get(sensorId).read());
                	}
                }
            }finally {
            	citySensorsReadPermit.unlock();
            }
            
            Connection connection = DBManager.getDBManager().createNewConnection();
            
            // iterate on all sensors and if their values are changed, update the DB record.
            Map<SensorId, SensorSnapshot> updated = new HashMap<>();
            for(SensorId sensorId : toBeUpdated.keySet()) {
            	SensorSnapshot sensorSnapshot = toBeUpdated.get(sensorId);
        		PreparedStatement stmt;
        		String sql = "UPDATE sensors SET full_flag=?, last_changed=?, last_updated=? WHERE id=?;";
        		try {
        			stmt = connection.prepareStatement(sql);
        			stmt.setInt(1, sensorSnapshot.fullFlag?1:0);
        			stmt.setTime(2, sensorSnapshot.lastTimeChanged);
        			stmt.setTime(3, sensorSnapshot.lastTimeUpdated);
        			stmt.setInt(4, sensorId.toInt());
        			stmt.executeUpdate();
        			stmt.close();
        			updated.put(sensorId, sensorSnapshot);
        		} catch (SQLException e) {
        			e.printStackTrace();
        		}
            }
            
            // flush the updated sensors
            try{
            	citySensorsReadPermit = container.getReadPermit();
                for(SensorId sensorId : updated.keySet()) {
                	container.citySensors.get(sensorId).flush();
                }
            }finally {
            	citySensorsReadPermit.unlock();
            }
            
        }
	}
}
