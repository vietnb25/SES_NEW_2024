package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.evn.DataInverterMapperEVN;
import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;

@Service
public class DataInverter1ServiceImpEVN implements DataInverter1ServiceEVN{
	@Autowired
	private DataInverterMapperEVN dataInverter1Mapper;
	
	@Override
	public List<DataInverter1EVN> getDataInverter1ByDeviceIds(Map<String, Object> condition) {
		return dataInverter1Mapper.getDataInverter1ByDeviceIds(condition);
	}

	@Override
	public List<DataInverter1EVN> getDataInverter1s(Map<String, Object> condition) {
		return dataInverter1Mapper.getDataInverter1s(condition);
	}

	
	
}
