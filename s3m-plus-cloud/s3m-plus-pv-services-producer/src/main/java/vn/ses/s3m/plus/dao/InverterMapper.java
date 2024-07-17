package vn.ses.s3m.plus.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Inverter;

@Mapper
public interface InverterMapper {

    Inverter getDataInverterByDeviceId(Map<String, Object> map);

}
