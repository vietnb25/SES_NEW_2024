package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SimulationDataEpMapper;
import vn.ses.s3m.plus.dto.SimulationDataEP;
import vn.ses.s3m.plus.form.SimulationDataForm;

@Service
public class SimulationDataEpImpl implements SimulationDataEpService{

	@Autowired
	private SimulationDataEpMapper mapper;
	
	@Override
	public List<SimulationDataEP> getList(Map<String, String> conditon) {
		return this.mapper.getListByProjectAndSystemType(conditon);
	}

	@Override
	public void addData(SimulationDataForm data) {
		this.mapper.addDataEp(data);
	}

	@Override
	public void updateData(Map<String, Object> conditon) {
		this.mapper.updateDataEp(conditon);
	}
}

