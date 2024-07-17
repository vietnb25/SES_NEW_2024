package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.LoadType;
import vn.ses.s3m.plus.dto.ObjectType;

@Mapper
public interface LoadTypeMapper {
    List<LoadType> getListLoadType();

    List<LoadType> getListLoadBySystemTypeIdAndProjectId(Map<String, Object> condition);

    List<LoadType> getListLoadTypeByListId(Map<String, Object> condition);
    
    List<LoadType> getAllLoadType(Map<String, Object> conditon);
    
    void addLoadType(String schema, LoadType loadType);

    void updateLoadType(String schema, LoadType loadType);

    void deleteLoadTypeById(Integer id);

    LoadType getLoadTypeById(String schema, Integer id);

    List<LoadType> getListLoadTypeMst();
    List<LoadType> getLoadTypeByProjectAndSystemType(Map<String, Object> conditon);

    List<Device> checkLoadTypeDevice(Integer id);
}
