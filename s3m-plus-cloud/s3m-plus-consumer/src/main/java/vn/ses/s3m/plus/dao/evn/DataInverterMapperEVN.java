package vn.ses.s3m.plus.dao.evn;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;

@Mapper
public interface DataInverterMapperEVN {
    List<DataInverter1EVN> getDataInverter1ByDeviceIds(Map<String, Object> condition);
    
    List<DataInverter1EVN> getDataInverter1ByDeviceId(Map<String, String> condition);
    
    List<DataInverter1EVN> getDataInverter1s(Map<String, Object> condition);

}
