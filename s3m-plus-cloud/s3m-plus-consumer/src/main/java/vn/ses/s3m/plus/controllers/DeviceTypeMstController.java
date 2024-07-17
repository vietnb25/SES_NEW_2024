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
import vn.ses.s3m.plus.dto.DeviceTypeMst;
import vn.ses.s3m.plus.dto.ObjectTypeMst;
import vn.ses.s3m.plus.response.DeviceTypeMstResonse;
import vn.ses.s3m.plus.service.DeviceTypeMstService;
import vn.ses.s3m.plus.service.ObjectTypeService;

@RestController
@RequestMapping ("/common/device-type-mst")
public class DeviceTypeMstController {
    @Autowired
    private DeviceTypeMstService service;

    @Autowired
    private ObjectTypeService objectTypeService;

    @GetMapping ("/list")
    public ResponseEntity<List<DeviceTypeMstResonse>> getListBySystemTypeAndProjectAndCustomer(
        @RequestParam ("customer") String customer, @RequestParam (value = "project", required = false) String project,
        @RequestParam ("systemType") String systemType) {
        List<DeviceTypeMstResonse> ls = new ArrayList<>();
        Map<String, String> condition = new HashMap<>();
        condition.put("customer", customer);
        condition.put("systemType", systemType);
        condition.put("project", project);
        List<DeviceTypeMst> list = this.service.getDeviceTypesBySystemTypeAndCustomerAndProject(condition);
        for (DeviceTypeMst dv : list) {
            ls.add(new DeviceTypeMstResonse(dv));
        }
        return new ResponseEntity<List<DeviceTypeMstResonse>>(ls, HttpStatus.OK);
    }

    @GetMapping ("/listDeviceType")
    public ResponseEntity<List<DeviceTypeMstResonse>> getListDeviceType() {

        List<DeviceTypeMstResonse> ls = new ArrayList<>();
        List<DeviceTypeMst> list = service.getDeviceTypes();

        List<ObjectTypeMst> listObjectType = objectTypeService.getListObjectTypeMst();

        for (DeviceTypeMst deviceTypeMst : list) {
            String objectType = "";

            if (deviceTypeMst.getObjectTypeIds() != null) {
                String[] objectTypeIds = deviceTypeMst.getObjectTypeIds()
                    .split(",");
                for (String id : objectTypeIds) {
                    for (ObjectTypeMst objectTypeMst : listObjectType) {
                        if (Integer.parseInt(id) == objectTypeMst.getId()) {
                            objectType += objectTypeMst.getName() + ", ";
                        }
                    }
                }
                deviceTypeMst.setObjectTypeName(objectType);
            }
        }

        for (DeviceTypeMst dv : list) {
            ls.add(new DeviceTypeMstResonse(dv));
        }

        return new ResponseEntity<List<DeviceTypeMstResonse>>(ls, HttpStatus.OK);
    }

    @GetMapping ("/searchDeviceType")
    public ResponseEntity<List<DeviceTypeMstResonse>> searchDeviceType(@RequestParam ("keyword") final String keyword) {

        List<DeviceTypeMstResonse> ls = new ArrayList<>();
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("keyword", keyword);
        List<DeviceTypeMst> list = service.searchDeviceType(condition);

        List<ObjectTypeMst> listObjectType = objectTypeService.getListObjectTypeMst();

        for (DeviceTypeMst deviceTypeMst : list) {
            String objectType = "";

            if (deviceTypeMst.getObjectTypeIds() != null) {
                String[] objectTypeIds = deviceTypeMst.getObjectTypeIds()
                    .split(",");
                for (String id : objectTypeIds) {
                    for (ObjectTypeMst objectTypeMst : listObjectType) {
                        if (Integer.parseInt(id) == objectTypeMst.getId()) {
                            objectType += objectTypeMst.getName() + ", ";
                        }
                    }
                }
                deviceTypeMst.setObjectTypeName(objectType);
            }
        }

        for (DeviceTypeMst dv : list) {
            ls.add(new DeviceTypeMstResonse(dv));
        }
        return new ResponseEntity<List<DeviceTypeMstResonse>>(ls, HttpStatus.OK);
    }

    @DeleteMapping ("/deleteDeviceType/{id}")
    public ResponseEntity<?> deleteDeviceType(@PathVariable ("id") final String id) {
        if (id != null && id.length() > 0) {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("id", id);
            service.deleteDeviceType(Integer.parseInt(id));
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping ("/addDeviceType")
    public ResponseEntity<?> addDeviceType(@RequestBody final DeviceTypeMst data) {

        List<String> errors = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        condition.put("name", data.getName());
        DeviceTypeMst checkDeviceType = service.getDeviceTypeByName(condition);

        if (checkDeviceType != null) {
            errors.add(Constants.DeviceValidation.DEVICE_TYPE_NAME_EXIST);
        }

        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        service.add(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping ("/updateDeviceType")
    public ResponseEntity<?> updateDeviceType(@RequestBody final DeviceTypeMst data) {

        DeviceTypeMst deviceType = new DeviceTypeMst();
        deviceType.setId(data.getId());
        deviceType.setName(data.getName());
        deviceType.setObjectTypeIds(data.getObjectTypeIds());
        deviceType.setImg(data.getImg());

        service.update(deviceType);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping ("/getDeviceTypeById/{id}")
    public ResponseEntity<?> getDeviceTypeById(@PathVariable ("id") final Integer id) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("id", id);
        DeviceTypeMst deviceType = service.getDeviceTypeById(condition);
        return new ResponseEntity<>(deviceType, HttpStatus.OK);
    }
}
