package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.SimulationDataEP;
import vn.ses.s3m.plus.dto.SimulationDataMoney;
import vn.ses.s3m.plus.form.SimulationDataForm;

@Mapper
public interface SimulationDataMoneyMapper {
	List<SimulationDataMoney> getListByProjectAndSystemType(Map<String, String> condition);
	void addDataMoney(SimulationDataForm data);
	void updateDataMoney(Map<String, Object> conditon);
}
