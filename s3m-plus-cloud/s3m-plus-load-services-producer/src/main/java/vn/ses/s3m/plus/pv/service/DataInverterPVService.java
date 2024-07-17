package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.DataInverter1;

public interface DataInverterPVService {

    List<DataInverter1> getDataInverterByDevice(Map<String, Object> condition);

}