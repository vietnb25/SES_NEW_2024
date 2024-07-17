package vn.ses.s3m.plus.dao;


import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.DataPDHTR02;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataPDHTRMapper {

    List<DataPDHTR02> getListHTRIndicatorByDeviceId(Map<String, Object> con);
    DataPDHTR02 getInforDeviceByWarningHTR02(Map<String, Object> con);
}
