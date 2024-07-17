package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.DataPDAMS01;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataPDAMSSMapper {
    List<DataPDAMS01> getListAMSIndicatorByDeviceId(Map<String, Object> con);
    List<DataPDAMS01> getInforDeviceByWarningAMS01(Map<String, Object> con);
}
