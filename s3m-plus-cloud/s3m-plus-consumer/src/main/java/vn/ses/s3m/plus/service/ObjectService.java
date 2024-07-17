package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.ObjectName;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.form.ObjectForm;

public interface ObjectService {
    void deleteObjectById(String schema, Integer id);

    void addObjectName(ObjectName condition);

    void addObjectType(ObjectForm objectName);

    List<Device> checkObjectIdLinkToDevice(Integer id);

    void updateObjectType(ObjectName objectName);

    ObjectName getObjectById(Integer id);

    void updateObjectTypeId(Map<String, Integer> condition);

    List<ObjectType> getListObject(Map<String, String> condition);

    List<ObjectName> getAllObjectType();

    ObjectName getObjectByName(Map<String, String> condition);

    List<ObjectType> getListObjectOneLevelByCustomerId(Map<String, String> condition);

    List<ObjectType> getListObjectTwoLevelByCustomerId(Map<String, String> condition);

    List<ObjectType> getListObjectOneLevelByProjectId(Map<String, String> condition);

    List<ObjectType> getListObjectTwoLevelByProjectId(Map<String, String> condition);

    ObjectType getObjectTypeById(Map<String, String> condition);

    List<ObjectName> getObjectsByObjectTypeId(Map<String, String> condition);

    List<ObjectType> getListObjectTwoLevelByCusSys(Map<String, String> condition);

    List<ObjectType> getListObjectTwoLevelByProSys(Map<String, String> condition);

    List<ObjectType> getListObjectTypeBySystemTypeIdAndProjectId(Map<String, Object> condition);

    List<ObjectType> getListAreaBySystemTypeIdAndProjectId(Map<String, Object> condition);

    List<ObjectType> getListObjectByListId(Map<String, Object> condition);

    ObjectType getCountDeviceByObjectId(Map<String, String> condition);

    List<ObjectType> getListObjectByDeviceType(Map<String, String> condition);

    String getObjectTypeIds(Map<String, String> condition);

    List<ObjectType> getListObjectMst();

    ObjectTypeMst getObjectTypeIdByObjectId(Integer id);

    ObjectName getObjectLastest();

}