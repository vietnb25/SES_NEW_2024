package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.ObjectName;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.form.ObjectForm;
import vn.ses.s3m.plus.response.ObjectResponse;
import vn.ses.s3m.plus.service.ObjectService;

@RestController
@RequestMapping ("/common/object")
public class ObjectController {

    @Autowired
    private ObjectService objectService;

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("/list/{systemTypeId}")
    public ResponseEntity<List<ObjectType>> getListObjectType(
        @PathVariable ("systemTypeId") final Integer systemTypeId) {
        List<ObjectType> respone = new ArrayList<>();
        Map<String, String> condition = new HashMap<String, String>();

        if (systemTypeId != 0) {
            condition.put("systemTypeId", String.valueOf(systemTypeId));
        }
        respone = objectService.getListObject(condition);

        return new ResponseEntity<List<ObjectType>>(respone, HttpStatus.OK);
    }

    @DeleteMapping ("/delete")
    public ResponseEntity<?> deleteObjectById(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("id") final Integer id) {
        String schema = Schema.getSchemas(customerId);
        System.out.println(schema);
        System.out.println(id);
        objectService.deleteObjectById(schema, id);

        return ResponseEntity.ok("Bản ghi đã được xóa.");
    }

    @GetMapping ("/getObjectById")
    public ResponseEntity<?> getObjectById(@RequestParam ("id") final Integer id) {
        ObjectName objectType = objectService.getObjectById(id);
        return new ResponseEntity<>(objectType, HttpStatus.OK);
    }

    @GetMapping ("/getAllObjectType")
    public ResponseEntity<List<ObjectResponse>> getAllObjectType() {

        List<ObjectResponse> objectRes = new ArrayList<>();
        List<ObjectName> listObjectType = objectService.getAllObjectType();
        for (ObjectName object : listObjectType) {
            ObjectResponse dr = new ObjectResponse(object);
            objectRes.add(dr);
        }
        return new ResponseEntity<List<ObjectResponse>>(objectRes, HttpStatus.OK);
    }

    @PutMapping ("/update")
    public ResponseEntity<?> updateObjectType(@RequestBody final ObjectForm data) {

        ObjectName object = new ObjectName();
        object.setId(data.getId());
        object.setObjectTypeId(data.getObjectTypeId());
        object.setObjectName(data.getObjectName());
        object.setProjectId(data.getProjectId());

        objectService.updateObjectType(object);
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    @GetMapping ("/listObjectType")
    public ResponseEntity<?> getListObjectTypeBySystemTypeIdAndProjectId(
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("projectId") final Integer projectId) {
        List<ObjectType> respone = new ArrayList<>();
        Map<String, Object> condition = new HashMap<String, Object>();

        if (systemTypeId != 0 && projectId != 0) {
            condition.put("systemTypeId", systemTypeId);
            condition.put("projectId", projectId);
        }
        respone = objectService.getListObjectTypeBySystemTypeIdAndProjectId(condition);

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    @GetMapping ("/listArea")
    public ResponseEntity<?> getListAreaBySystemTypeIdAndProjectId(
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("projectId") final Integer projectId) {
        List<ObjectType> respone = new ArrayList<>();
        Map<String, Object> condition = new HashMap<String, Object>();

        if (systemTypeId != 0 && projectId != 0) {
            condition.put("systemTypeId", systemTypeId);
            condition.put("projectId", projectId);
        }
        respone = objectService.getListAreaBySystemTypeIdAndProjectId(condition);

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    @GetMapping ("/getObjectTypeByDeviceType")
    public ResponseEntity<?> getObjectTypeByDeviceType(@RequestParam ("projectId") final String projectId,
        @RequestParam ("deviceTypeId") final String deviceTypeId) {

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("deviceTypeId", deviceTypeId);
        String objectTypeIds = objectService.getObjectTypeIds(condition);
        Map<String, String> conditions = new HashMap<String, String>();
        if (objectTypeIds == null || objectTypeIds == "") {
            return null;
        } else {
            conditions.put("objectTypeIds", objectTypeIds);
            conditions.put("deviceTypeId", deviceTypeId);
            conditions.put("projectId", projectId);
            List<ObjectType> objectList = objectService.getListObjectByDeviceType(conditions);
            return new ResponseEntity<>(objectList, HttpStatus.OK);
        }

    }

    @GetMapping ("/getListObjectMst")
    public ResponseEntity<?> getListObjectMst() {
        List<ObjectType> listObjectMst = objectService.getListObjectMst();

        return new ResponseEntity<>(listObjectMst, HttpStatus.OK);
    }

    @PostMapping ("/add")
    public ResponseEntity<?> addObjectType(@RequestBody final ObjectForm data) {

        List<String> errors = new ArrayList<>();

        Map<String, String> conditionCheck = new HashMap<String, String>();
        conditionCheck.put("objectName", data.getObjectName());
        ObjectName checkObjectName = objectService.getObjectByName(conditionCheck);

        if (checkObjectName != null) {
            errors.add(Constants.ObjectValidate.OBJECT_NAME_EXIST);
        }

        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            objectService.addObjectType(data);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping ("/getObjectTypeIdById/{id}")
    public ResponseEntity<?> getObjectTypeIdById(@PathVariable ("id") final Integer id) {

        ObjectTypeMst objTypeId = objectService.getObjectTypeIdByObjectId(id);

        return new ResponseEntity<>(objTypeId, HttpStatus.OK);
    }

    @GetMapping ("/getObjectId")
    public ResponseEntity<?> getObjectId() {

        ObjectName objectId = objectService.getObjectLastest();

        return new ResponseEntity<>(objectId, HttpStatus.OK);
    }
}