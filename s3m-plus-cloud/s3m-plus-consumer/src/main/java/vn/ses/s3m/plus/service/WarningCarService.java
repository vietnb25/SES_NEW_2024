package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;



import vn.ses.s3m.plus.dto.WarningCar;


public interface WarningCarService {	
	List<WarningCar> getWarningCars(Map<String, Object> condition);
	
	List<WarningCar> getCountListWarningCar(Map<String, Object> condition);
	
	Integer getCountWarningCarByStatus(Map<String, Object> condition);
	
	void addWarningCar(String schema, WarningCar warningCars);
	
	WarningCar getWarningCarById(String schema, Integer id);
	
	void updateWarningCars(String schema, WarningCar warningCar);
	
}
