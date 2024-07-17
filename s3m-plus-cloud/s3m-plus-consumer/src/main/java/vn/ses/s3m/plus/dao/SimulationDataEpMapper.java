package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.SimulationDataEP;
import vn.ses.s3m.plus.form.SimulationDataForm;

@Mapper
public interface SimulationDataEpMapper {
	List<SimulationDataEP> getListByProjectAndSystemType(Map<String, String> condition);
	void addDataEp(SimulationDataForm data);
	void updateDataEp(Map<String, Object> conditon);
	
}
