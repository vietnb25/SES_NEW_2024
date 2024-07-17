package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.SelectDevice;

import java.util.List;
import java.util.Map;

@Mapper
public interface SelectDeviceMapper {
    List<SelectDevice> getLocationSelectDevice(Map<String, String> con);
    List<SelectDevice> getObjectTypeSelectDevice(Map<String, String> con);

}
