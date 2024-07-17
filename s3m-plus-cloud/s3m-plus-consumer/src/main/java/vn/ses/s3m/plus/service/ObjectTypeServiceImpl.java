package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ObjectTypeMapper;
import vn.ses.s3m.plus.dao.SelectDeviceMapper;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.dto.SelectDevice;

@Service
public class ObjectTypeServiceImpl implements ObjectTypeService {
    @Autowired
    private ObjectTypeMapper objectTypeMapper;

    @Autowired
    private SelectDeviceMapper selectDeviceMapper;

    @Override
    public List<ObjectType> getAllObjectType(Map<String, Object> conditon) {
        return objectTypeMapper.getAllObjectType(conditon);
    }

    @Override
    public List<ObjectType> getObjectTypeByType(Map<String, Object> conditon) {
        return objectTypeMapper.getObjectTypeByType(conditon);
    }

    @Override
    public void addObjectType(String schema, ObjectType objectType) {
        objectTypeMapper.addObjectType(schema, objectType);
    }

    @Override
    public void updateObjectType(String schema, ObjectType objectType) {
        objectTypeMapper.updateObjectType(schema, objectType);
    }

    @Override
    public void deleteObjectTypeById(String schema, ObjectType objectType) {
        objectTypeMapper.deleteObjectTypeById(schema, objectType);
    }

    @Override
    public ObjectType getObjectTypeById(String schema, Integer id) {
        return objectTypeMapper.getObjectTypeById(schema, id);
    }

    @Override
    public List<ObjectTypeMst> getListObjectTypeMst() {
        return objectTypeMapper.getListObjectTypeMst();
    }

    @Override
    public List<ObjectTypeMst> searchObjectType(Map<String, String> condtion) {
        return objectTypeMapper.searchObjectType(condtion);
    }

    @Override
    public void deleteObjectTypeMstById(Integer id) {
        objectTypeMapper.deleteObjectTypeMstById(id);
    }

    @Override
    public void add(ObjectTypeMst objectTypeMst) {
        objectTypeMapper.add(objectTypeMst);
    }

    @Override
    public void update(ObjectTypeMst objectTypeMst) {
        objectTypeMapper.update(objectTypeMst);
    }

    @Override
    public ObjectTypeMst getObjectTypeById(Map<String, String> condtion) {
        return objectTypeMapper.getObjectTypeById(condtion);
    }

    @Override
    public ObjectTypeMst getObjectTypeByName(Map<String, String> condtion) {
        return objectTypeMapper.getObjectTypeByName(condtion);
    }

    @Override
    public List<ObjectTypeMst> getObjectTypeByIds(Map<String, String> condtion) {
        return objectTypeMapper.getObjectTypeByIds(condtion);
    }

    @Override
    public List<SelectDevice> getObjectTypeSelectDevice(Map<String, String> con) {
        return this.selectDeviceMapper.getObjectTypeSelectDevice(con);
    }
}
