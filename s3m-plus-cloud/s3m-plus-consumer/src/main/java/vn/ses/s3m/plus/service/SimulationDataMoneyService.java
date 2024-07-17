package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.SimulationDataMoney;
import vn.ses.s3m.plus.form.SimulationDataForm;

public interface SimulationDataMoneyService {
	List<SimulationDataMoney> getList(Map<String, String> conditon);
	void addData(SimulationDataForm data);
	void updateData(Map<String, Object> conditon);
}
