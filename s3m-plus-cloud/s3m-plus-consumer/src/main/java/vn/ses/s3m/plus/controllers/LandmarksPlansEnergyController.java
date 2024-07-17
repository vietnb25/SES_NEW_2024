package vn.ses.s3m.plus.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.form.LandmarksPlansEnergyForm;
import vn.ses.s3m.plus.response.LandmarksPlansEnergyResponse;
import vn.ses.s3m.plus.service.LandmarksPlanssEnergyService;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/common/landmarks-energy-plans")
public class LandmarksPlansEnergyController {
    @Autowired
    private LandmarksPlanssEnergyService service;

    @GetMapping("list-landmark")
    ResponseEntity<List<LandmarksPlansEnergyResponse>> getListLandmarks(
            @RequestParam("date") String date,
            @RequestParam("customer") String customer,
            @RequestParam("project") String project,
            @RequestParam("systemType") String systemType,
            @RequestParam("deviceId") String deviceId

    ) {
        String schema = Schema.getSchemas(Integer.valueOf(customer));
        List<LandmarksPlansEnergyResponse> ls = new ArrayList<>();
        if (date != null && date.equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Map<String, String> condition = new HashMap<>();
        condition.put("year", date);
        condition.put("schema", schema);
        condition.put("project", project);
        condition.put("system", systemType);
        condition.put("deviceId", deviceId);
        List<LandmarksPlansEnergy> lsDto = this.service.getListDataLandmarks(condition);
        for (LandmarksPlansEnergy d : lsDto) {
            ls.add(new LandmarksPlansEnergyResponse(d));
        }
        return new ResponseEntity<List<LandmarksPlansEnergyResponse>>(ls, HttpStatus.OK);
    }

    @GetMapping("list-plan")
    ResponseEntity<List<LandmarksPlansEnergyResponse>> getListPlans(@RequestParam("date") String date,
                                                                    @RequestParam("customer") String customer,
                                                                    @RequestParam("project") String project,
                                                                    @RequestParam("systemType") String systemType,
                                                                    @RequestParam("deviceId") String deviceId) {
        List<LandmarksPlansEnergyResponse> ls = new ArrayList<>();
        if (date != null && date.equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String schema = Schema.getSchemas(Integer.valueOf(customer));
        Map<String, String> condition = new HashMap<>();
        condition.put("year", date);
        condition.put("schema", schema);
        condition.put("project", project);
        condition.put("system", systemType);
        condition.put("deviceId", deviceId);
        List<LandmarksPlansEnergy> lsDto = this.service.getListDataPlans(condition);
        for (LandmarksPlansEnergy d : lsDto) {
            ls.add(new LandmarksPlansEnergyResponse(d));
        }
        return new ResponseEntity<>(ls, HttpStatus.OK);
    }

    @PutMapping("plan")
    ResponseEntity<?> updatePlan(@RequestBody LandmarksPlansEnergyForm form, @RequestParam("customer") String cus) {
        if (form.getId() == null || form.getId().equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        form.setSchema(Schema.getSchemas(Integer.valueOf(cus)));
        this.service.updatePlans(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("landmark")
    ResponseEntity<?> updateLanmark(@RequestBody LandmarksPlansEnergyForm form, @RequestParam("customer") String cus) {
        if (form.getId() == null || form.getId().equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        form.setSchema(Schema.getSchemas(Integer.valueOf(cus)));
        this.service.updateLandmarks(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("landmarks")
    ResponseEntity<?> insertLanmarks(
            @RequestParam("customer") String cus,
            @RequestParam("project") String project,
            @RequestParam("systemType") String systemType,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("date") String date
    ) {


        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(Integer.valueOf(cus));
        condition.put("year", date);
        condition.put("schema", schema);
        condition.put("project", project);
        condition.put("system", systemType);
        condition.put("deviceId", deviceId);
        List<LandmarksPlansEnergy> lsDto = this.service.getListDataLandmarks(condition);
        if (lsDto.size() > 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
        this.service.insertLandmarks(condition);
        return new ResponseEntity<>(HttpStatus.OK);
        }

    }

    @PostMapping("plans")
    ResponseEntity<?> insertPlans(
            @RequestParam("customer") String cus,
            @RequestParam("project") String project,
            @RequestParam("systemType") String systemType,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("date") String date
    ) {
        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(Integer.valueOf(cus));
        condition.put("year", date);
        condition.put("schema", schema);
        condition.put("project", project);
        condition.put("system", systemType);
        condition.put("deviceId", deviceId);
        List<LandmarksPlansEnergy> lsDto = this.service.getListDataPlans(condition);
        if (lsDto.size() > 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else {
        this.service.insertPlans(condition);
        return new ResponseEntity<>(HttpStatus.OK);
        }

    }
}
