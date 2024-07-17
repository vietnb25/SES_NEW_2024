package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataInverter;

public interface DataInverterService {

    List<DataInverter> getDataInverterByDevice(Map<String, Object> condition);

}
