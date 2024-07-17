package vn.ses.s3m.plus.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
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

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.LoadType;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.form.LoadTypeForm;
import vn.ses.s3m.plus.form.ObjectTypeForm;
import vn.ses.s3m.plus.response.ObjectTypeResponse;
import vn.ses.s3m.plus.service.LoadTypeService;

@RestController
@RequestMapping("/common/loadType")
public class LoadTypeController {

    @Autowired
    private LoadTypeService loadTypeService;

    @GetMapping("/list")
    public ResponseEntity<List<LoadType>> getListLoadType() {
        List<LoadType> respone = new ArrayList<>();

        respone = loadTypeService.getListLoadType();

        return new ResponseEntity<List<LoadType>>(respone, HttpStatus.OK);
    }

    @GetMapping("/listLoad/{projectId}")
    public ResponseEntity<?> getListLoadBySystemTypeIdAndProjectId(@PathVariable("projectId") final Integer projectId,
                                                                   @RequestParam("systemTypeId") final Integer systemTypeId) {
        List<LoadType> respone = new ArrayList<>();
        Map<String, Object> condition = new HashMap<String, Object>();

        if (systemTypeId != 0 && projectId != 0) {
            condition.put("systemTypeId", systemTypeId);
            condition.put("projectId", projectId);
        }
        respone = loadTypeService.getListLoadBySystemTypeIdAndProjectId(condition);

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    @GetMapping("/getLoadTypeById")
    public ResponseEntity<?> getLoadTypeById(@RequestParam("customerId") final Integer customerId,
                                             @RequestParam("loadTypeId") final Integer loadTypeId) {
        String schema = Schema.getSchemas(customerId);
        LoadType reponse = loadTypeService.getLoadTypeById(schema, loadTypeId);

        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }

    @PostMapping("/add/{customerId}")
    public ResponseEntity<ObjectTypeResponse> addLoadType(@PathVariable final String customerId,
                                                          @RequestBody final LoadTypeForm data) {

        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        LocalDateTime ldt = LocalDateTime.now();

        LoadType loadType = new LoadType();
        loadType.setLoadTypeName(data.getLoadTypeName());
        loadType.setDescription(data.getDescription());
        loadType.setCreateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));
        loadTypeService.addLoadType(schema, loadType);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{customerId}")
    public ResponseEntity<?> updateLoadType(@PathVariable final String customerId, @RequestBody final LoadTypeForm data) {

        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        LocalDateTime ldt = LocalDateTime.now();

        LoadType loadType = new LoadType();
        loadType.setLoadTypeId(data.getLoadTypeId());
        loadType.setLoadTypeName(data.getLoadTypeName());
        loadType.setDescription(data.getDescription());
        loadType.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));
        loadTypeService.updateLoadType(schema, loadType);
        return new ResponseEntity<>(loadType, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LoadType>> searchLoadType(@RequestParam("keyword") final String keyword) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("keyword", keyword);
        List<LoadType> respone = new ArrayList<>();
        respone = loadTypeService.getAllLoadType(condition);

        return new ResponseEntity<List<LoadType>>(respone, HttpStatus.OK);
    }

    @GetMapping("/listLoadType")
    public ResponseEntity<List<LoadType>> getListLoadTypeMst() {
        List<LoadType> respone = new ArrayList<>();

        respone = loadTypeService.getListLoadTypeMst();

        return new ResponseEntity<List<LoadType>>(respone, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLoadTypeById(
            @PathVariable("id") final String id) {
        loadTypeService.deleteLoadTypeById(Integer.valueOf(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/checkLoadTypeDevice")
    public ResponseEntity<List<Device>> checkLoadTypeDevice(@RequestParam("id") final Integer id) {
        List<Device> respone = new ArrayList<>();
        respone = loadTypeService.checkLoadTypeDevice(id);
        return new ResponseEntity<List<Device>>(respone, HttpStatus.OK);
    }
    @GetMapping("/list-load-type-by-project")
    public ResponseEntity<List<LoadType>> getListLoadTypeByProject(
            @RequestParam("customer") Integer customer,
            @RequestParam("project") Integer project,
            @RequestParam("typeSystem") Integer typeSystem

    ) {
        Map<String, Object> con = new HashMap<>();
        con.put("customer", customer);
        con.put("project", project);
        con.put("typeSystem", typeSystem);
        List<LoadType> respone = this.loadTypeService.getLoadTypeByProjectAndSystemType(con);

        return new ResponseEntity<List<LoadType>>(respone, HttpStatus.OK);
    }

}
