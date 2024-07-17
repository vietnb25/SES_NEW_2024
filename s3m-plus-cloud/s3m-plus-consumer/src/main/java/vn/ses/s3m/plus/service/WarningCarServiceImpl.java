package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.WarningCarMapper;
import vn.ses.s3m.plus.dto.Manufacture;
import vn.ses.s3m.plus.dto.WarningCar;
import vn.ses.s3m.plus.response.WarningCarResponse;
@Service
public class WarningCarServiceImpl implements WarningCarService{
	
	@Autowired
	private WarningCarMapper warningCarMapper;

	  /**
     * Thêm phiếu CAR
     *
     * @return phiếu CAR đã được thêm vào db.
     */
	@Override
	public void addWarningCar(String schema, WarningCar warningCars) {
		warningCarMapper.addWarningCars(schema, warningCars);
		
	}

	@Override
	public List<WarningCar> getWarningCars(Map<String, Object> condition) {
		return warningCarMapper.getWarningCars(condition);
	}

	@Override
	public List<WarningCar> getCountListWarningCar(Map<String, Object> condition) {
		return warningCarMapper.getCountListWarningCar(condition);
	}

	@Override
	public WarningCar getWarningCarById(String schema, Integer id) {
		return warningCarMapper.getWarningCarById(schema, id);
	}

	@Override
	public void updateWarningCars(String schema, WarningCar warningCar) {
		warningCarMapper.updateWarningCars(schema, warningCar);
	}

	@Override
	public Integer getCountWarningCarByStatus(Map<String, Object> condition) {
		return warningCarMapper.getCountWarningCarByStatus(condition);
	
	}
		  	    
}
