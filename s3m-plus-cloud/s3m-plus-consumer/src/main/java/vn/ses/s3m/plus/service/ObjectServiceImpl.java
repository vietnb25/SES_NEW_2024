package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ObjectMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.ObjectName;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.form.ObjectForm;

@Service
public class ObjectServiceImpl implements ObjectService {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<ObjectType> getListObject(Map<String, String> condition) {
        return mapper.getListObject(condition);
    }

    @Override
    public List<ObjectName> getAllObjectType() {
        return mapper.getAllObjectType();
    }

    @Override
    public void deleteObjectById(String schema, Integer id) {
        mapper.deleteObjectById(schema, id);
    }

    @Override
    public void addObjectName(ObjectName condition) {
        mapper.addObjectName(condition);
    }

    @Override
    public void addObjectType(ObjectForm objectName) {
        mapper.addObjectType(objectName);
    }

    @Override
    public List<Device> checkObjectIdLinkToDevice(Integer id) {
        return mapper.checkObjectIdLinkToDevice(id);
    }

    @Override
    public ObjectName getObjectByName(Map<String, String> condition) {
        return mapper.getObjectByName(condition);
    }

    @Override
    public List<ObjectType> getListObjectOneLevelByCustomerId(Map<String, String> condition) {
        return mapper.getListObjectOneLevelByCustomerId(condition);
    }

    @Override
    public List<ObjectType> getListObjectTwoLevelByCustomerId(Map<String, String> condition) {
        return mapper.getListObjectTwoLevelByCustomerId(condition);
    }

    @Override
    public ObjectType getObjectTypeById(Map<String, String> condition) {
        return mapper.getObjectTypeById(condition);
    }

    @Override
    public List<ObjectName> getObjectsByObjectTypeId(Map<String, String> condition) {
        return mapper.getObjectsByObjectTypeId(condition);
    }

    @Override
    public List<ObjectType> getListObjectOneLevelByProjectId(Map<String, String> condition) {
        return mapper.getListObjectOneLevelByProjectId(condition);
    }

    @Override
    public List<ObjectType> getListObjectTwoLevelByProjectId(Map<String, String> condition) {
        return mapper.getListObjectTwoLevelByProjectId(condition);
    }

    @Override
    public List<ObjectType> getListObjectTwoLevelByCusSys(Map<String, String> condition) {
        return mapper.getListObjectTwoLevelByCusSys(condition);
    }

    @Override
    public List<ObjectType> getListObjectTwoLevelByProSys(Map<String, String> condition) {
        return mapper.getListObjectTwoLevelByProSys(condition);
    }

    @Override
    public List<ObjectType> getListObjectTypeBySystemTypeIdAndProjectId(Map<String, Object> condition) {
        return mapper.getListObjectTypeBySystemTypeIdAndProjectId(condition);
    }

    @Override
    public List<ObjectType> getListAreaBySystemTypeIdAndProjectId(Map<String, Object> condition) {
        return mapper.getListAreaBySystemTypeIdAndProjectId(condition);
    }

    @Override
    public List<ObjectType> getListObjectByListId(Map<String, Object> condition) {
        return mapper.getListObjectByListId(condition);
    }

    @Override
    public void updateObjectType(ObjectName objectName) {
        this.mapper.updateObjectType(objectName);

    }

    @Override
    public ObjectName getObjectById(Integer id) {
        return mapper.getObjectById(id);
    }

    @Override
    public void updateObjectTypeId(Map<String, Integer> condition) {

    }

    @Override
    public ObjectType getCountDeviceByObjectId(Map<String, String> condition) {
        return mapper.getCountDeviceByObjectId(condition);
    }

    @Override
    public String getObjectTypeIds(Map<String, String> condition) {
        return mapper.getObjectTypeIds(condition);
    }

    @Override
    public List<ObjectType> getListObjectByDeviceType(Map<String, String> condition) {
        return mapper.getListObjectByDeviceType(condition);
    }

    @Override
    public List<ObjectType> getListObjectMst() {
        return mapper.getListObjectMst();
    }

    @Override
    public ObjectTypeMst getObjectTypeIdByObjectId(Integer id) {
        return mapper.getObjectTypeIdByObjectId(id);
    }

    @Override
    public ObjectName getObjectLastest() {
        return mapper.getObjectLastest();
    }

}