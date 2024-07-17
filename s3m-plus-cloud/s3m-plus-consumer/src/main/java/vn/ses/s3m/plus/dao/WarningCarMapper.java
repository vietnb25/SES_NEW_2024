package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;



import vn.ses.s3m.plus.dto.WarningCar;

@Mapper
public interface WarningCarMapper {
	List<WarningCar> getWarningCars(Map<String, Object> condition);
	
    List<WarningCar> getCountListWarningCar(Map<String, Object> condition);
    
    Integer getCountWarningCarByStatus(Map<String, Object> condition);
	
	void addWarningCars(String schema, WarningCar warningCar);
	
	WarningCar getWarningCarById(String schema, Integer id);
	
	void updateWarningCars(String schema, WarningCar warningCar);
}


