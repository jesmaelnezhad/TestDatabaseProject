package rm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import rm.basestations.Sensor;
import rm.basestations.SensorId;
import rm.basestations.Sensor.SensorSnapshot;
import rm.parking_structure.ParkingSpotContainer;
import utility.DBManager;

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
            Map<SensorId, Sensor> citySensors = container.citySensors;
            Connection connection = DBManager.getDBManager().createNewConnection();
            String sql = "UPDATE sensors SET full_flag=?, last_changed=?, last_updated=? WHERE id=?;";
            
            // iterate on all sensors and if their values are changed, update the DB record.
            for(SensorId sensorId : citySensors.keySet()) {
            	if(citySensors.get(sensorId).isChanged()) {
            		SensorSnapshot sensorSnapshot = citySensors.get(sensorId).read();
            		PreparedStatement stmt;
            		try {
            			stmt = connection.prepareStatement(sql);
            			stmt.setInt(1, sensorSnapshot.fullFlag?1:0);
            			stmt.setTime(2, sensorSnapshot.lastTimeChanged);
            			stmt.setTime(3, sensorSnapshot.lastTimeUpdated);
            			stmt.setInt(4, sensorId.toInt());
            			stmt.executeUpdate();
            			stmt.close();
            			citySensors.get(sensorId).flush();
            		} catch (SQLException e) {
            			e.printStackTrace();
            		}
            	}
            }
        }
	}
}
