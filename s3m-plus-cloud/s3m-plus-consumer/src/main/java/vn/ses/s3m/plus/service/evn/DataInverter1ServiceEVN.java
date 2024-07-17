package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;

public interface DataInverter1ServiceEVN{
	List<DataInverter1EVN> getDataInverter1ByDeviceIds(Map<String, Object> condition);
	
	List<DataInverter1EVN> getDataInverter1s(Map<String, Object> condition);

}
