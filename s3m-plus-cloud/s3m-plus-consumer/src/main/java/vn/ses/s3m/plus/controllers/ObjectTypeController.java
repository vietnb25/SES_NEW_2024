package vn.ses.s3m.plus.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.DeviceTypeMst;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.form.ObjectTypeForm;
import vn.ses.s3m.plus.response.ObjectTypeResponse;
import vn.ses.s3m.plus.service.DeviceTypeMstService;
import vn.ses.s3m.plus.service.ObjectTypeService;

@RestController
@RequestMapping ("/common/objectType")
@Slf4j
public class ObjectTypeController {
    @Autowired
    private ObjectTypeService objectTypeService;
    
    @Autowired
    private DeviceTypeMstService service;

    @GetMapping ("")
    public ResponseEntity<List<ObjectType>> getAllObjectType(@RequestParam ("customerId") final Integer customerId) {
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        List<ObjectType> listObjectType = objectTypeService.getAllObjectType(condition);
        return new ResponseEntity<List<ObjectType>>(listObjectType, HttpStatus.OK);
    }

    @GetMapping ("/getObjectById")
    public ResponseEntity<?> getObjectById(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("objectTypeId") final Integer objectTypeId) {
        String schema = Schema.getSchemas(customerId);
        ObjectType objectType = objectTypeService.getObjectTypeById(schema, objectTypeId);
        System.out.println(objectType);
        return new ResponseEntity<>(objectType, HttpStatus.OK);
    }

    @PostMapping ("/add/{customerId}")
    public ResponseEntity<ObjectTypeResponse> addObjectType(@PathVariable final String customerId,
        @RequestBody final ObjectTypeForm data) {

        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        System.out.println(data);
        ObjectType objectType = new ObjectType();
        objectType.setSystemTypeId(1);
        objectType.setDeleteFlag(0);
        objectType.setTypeDefault(0);
        objectType.setObjectTypeName(data.getObjectTypeName());
        objectType.setImg(data.getImg());
        LocalDateTime ldt = LocalDateTime.now();
        objectType.setCreateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
            .format(ldt));
        objectTypeService.addObjectType(schema, objectType);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping ("/update/{customerId}")
    public ResponseEntity<?> updateObjectType(@PathVariable final String customerId,
        @RequestBody final ObjectTypeForm data) {
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        LocalDateTime ldt = LocalDateTime.now();

        ObjectType objectType = new ObjectType();
        objectType.setObjectTypeId(data.getObjectTypeId());
        objectType.setSystemTypeId(data.getSystemTypeId());
        objectType.setObjectTypeName(data.getObjectTypeName());
        objectType.setImg(data.getImg());
        objectType.setStatus(data.getStatus());
        objectType.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
            .format(ldt));
        objectTypeService.updateObjectType(schema, objectType);
        return new ResponseEntity<>(objectType, HttpStatus.OK);
    }

    @PutMapping ("/delete/{customerId}")
    public ResponseEntity<?> getObjectTypeById(@PathVariable final String customerId,
        @RequestBody final ObjectTypeForm data) {
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        ObjectType objectType = new ObjectType();
        objectType.setObjectTypeId(data.getObjectTypeId());
        objectType.setDeleteFlag(data.getDeleteFlag());
        objectTypeService.deleteObjectTypeById(schema, objectType);
        return new ResponseEntity<>(objectType, HttpStatus.OK);
    }

    @GetMapping ("/listObjectType")
    public ResponseEntity<List<ObjectTypeMst>> getListObjectType() {
        List<ObjectTypeMst> listObjectType = objectTypeService.getListObjectTypeMst();
        return new ResponseEntity<List<ObjectTypeMst>>(listObjectType, HttpStatus.OK);
    }

    @GetMapping ("/searchObjectType")
    public ResponseEntity<List<ObjectTypeMst>> searchObjectType(@RequestParam ("keyword") final String keyword) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("keyword", keyword);
        List<ObjectTypeMst> listObjectType = objectTypeService.searchObjectType(condition);
        return new ResponseEntity<List<ObjectTypeMst>>(listObjectType, HttpStatus.OK);
    }

    @DeleteMapping ("/deleteObjectType/{id}")
    public ResponseEntity<?> deleteObjectType(@PathVariable final String id) {

        if (id != null && id.length() > 0) {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("id", id);
            objectTypeService.deleteObjectTypeMstById(Integer.parseInt(id));
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            log.info("deleteObjectType END");
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping ("/addObjectTypeMst")
    public ResponseEntity<?> addObjectTypeMst(@RequestBody final ObjectTypeMst data) {

        List<String> errors = new ArrayList<>();
        List<DeviceTypeMst> list = service.getDeviceTypes();
        
        Map<String, String> condition = new HashMap<>();
        condition.put("name", data.getName());
        ObjectTypeMst checkObjectType = objectTypeService.getObjectTypeByName(condition);

        if (checkObjectType != null) {
            errors.add(Constants.ObjectValidate.OBJECT_NAME_EXIST);
        }

        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
            
        objectTypeService.add(data);

        for(DeviceTypeMst x : list) {
        	  x.setId(x.getId());
              x.setName(x.getName());
              x.setObjectTypeIds(x.getObjectTypeIds() + "," + data.getId());
              x.setImg(x.getImg());
              service.update(x);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping ("/updateObjectType")
    public ResponseEntity<?> update(@RequestBody final ObjectTypeMst data) {

        ObjectTypeMst object = new ObjectTypeMst();
        object.setId(data.getId());
        object.setName(data.getName());
        object.setImg(data.getImg());

        objectTypeService.update(object);
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    @GetMapping ("/getObjectTypeById/{id}")
    public ResponseEntity<?> getObjectTypeById(@PathVariable ("id") final String id) {

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("objectTypeId", id);
        ObjectTypeMst objectType = objectTypeService.getObjectTypeById(condition);
        return new ResponseEntity<>(objectType, HttpStatus.OK);
    }

    @GetMapping ("/getObjectTypeByIds")
    public ResponseEntity<List<ObjectTypeMst>> getObjectTypeByIds(
        @RequestParam ("objectTypeIds") final String objectTypeIds) {

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("objectTypeIds", objectTypeIds);
        List<ObjectTypeMst> listObjectTypeIds = objectTypeService.getObjectTypeByIds(condition);

        return new ResponseEntity<List<ObjectTypeMst>>(listObjectTypeIds, HttpStatus.OK);
    }
}
