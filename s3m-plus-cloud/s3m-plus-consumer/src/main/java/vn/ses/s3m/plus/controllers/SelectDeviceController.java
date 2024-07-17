package vn.ses.s3m.plus.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ses.s3m.plus.dto.SelectDevice;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.ObjectTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common/select-device")
public class SelectDeviceController {
    @Autowired
    AreaService service;

    @Autowired
    ObjectTypeService objectTypeService;
    @GetMapping("area")
    public ResponseEntity<List<SelectDevice>> getArea(@RequestParam("systemType") String systemType,
                                                      @RequestParam("project") String project) {
        Map<String, String> con = new HashMap<>();
        con.put("systemType", systemType);
        con.put("project", project);
        List<SelectDevice> ls = this.service.getLocationSelectDevice(con);
        return new ResponseEntity<List<SelectDevice>>(ls, HttpStatus.OK);
    }
    @GetMapping("object-type")
    public ResponseEntity<List<SelectDevice>> getObjectType(@RequestParam("systemType") String systemType, @RequestParam("project") String project) {
        Map<String, String> con = new HashMap<>();
        con.put("systemType", systemType);
        con.put("project", project);
        List<SelectDevice> ls = this.objectTypeService.getObjectTypeSelectDevice(con);
        return new ResponseEntity<List<SelectDevice>>(ls, HttpStatus.OK);
    }
}
