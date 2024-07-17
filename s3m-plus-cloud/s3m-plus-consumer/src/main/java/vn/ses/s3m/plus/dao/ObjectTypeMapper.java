package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;

@Mapper
public interface ObjectTypeMapper {
    List<ObjectType> getAllObjectType(Map<String, Object> conditon);

    List<ObjectType> getObjectTypeByType(Map<String, Object> conditon);

    void addObjectType(String schema, ObjectType objectType);

    void updateObjectType(String schema, ObjectType objectType);

    void deleteObjectTypeById(String schema, ObjectType objectType);

    ObjectType getObjectTypeById(String schema, Integer id);

    List<ObjectTypeMst> getListObjectTypeMst();

    List<ObjectTypeMst> searchObjectType(Map<String, String> condtion);

    void deleteObjectTypeMstById(Integer id);

    void add(ObjectTypeMst objectTypeMst);

    void update(ObjectTypeMst objectTypeMst);

    ObjectTypeMst getObjectTypeById(Map<String, String> condtion);

    ObjectTypeMst getObjectTypeByName(Map<String, String> condtion);

    List<ObjectTypeMst> getObjectTypeByIds(Map<String, String> condtion);
}
