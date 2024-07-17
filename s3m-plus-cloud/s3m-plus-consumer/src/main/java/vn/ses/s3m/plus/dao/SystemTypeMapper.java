package vn.ses.s3m.plus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.SystemType;

@Mapper
public interface SystemTypeMapper {

    List<SystemType> getSystemTypes();

    SystemType getSystemTypeById(Integer systemTypeId);

}
