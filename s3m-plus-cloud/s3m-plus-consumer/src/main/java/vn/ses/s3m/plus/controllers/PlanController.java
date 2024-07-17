package vn.ses.s3m.plus.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.form.AreaForm;
import vn.ses.s3m.plus.form.CustomerForm;
import vn.ses.s3m.plus.form.PlanForm;
import vn.ses.s3m.plus.response.AreaResponse;
import vn.ses.s3m.plus.response.PlanResponse;
import vn.ses.s3m.plus.response.SuperManagerResponse;
import vn.ses.s3m.plus.service.PlanService;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/common/plan")
@Slf4j
public class PlanController {
    @Autowired
    private PlanService planService;


    @GetMapping("")
    public ResponseEntity<List<Plan>> getListPlan(@RequestParam ("customerId") final Integer customerId,
                                                  @RequestParam (value = "projectId", required = false) final Integer projectId,
                                                  @RequestParam ("systemTypeId") final Integer systemTypeId,
                                                  @RequestParam ("startDate") final String startDate,
                                                  @RequestParam ("endDate") final String endDate) {
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("systemTypeId", systemTypeId);
        List<Plan> listPlan = planService.getAllPlan(condition);
        return new ResponseEntity<List<Plan>>(listPlan, HttpStatus.OK);

    }

    @PostMapping ("/add/{customerId}")
    public ResponseEntity<PlanResponse> addPlan(@PathVariable final String customerId,
                                                @RequestBody final PlanForm data) {

        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        System.out.println(data);
            Plan plan = new Plan();
            plan.setSystemTypeId(data.getSystemTypeId());
            plan.setOrganizationCreate(data.getOrganizationCreate());
            plan.setContent(data.getContent());
            plan.setOrganizationExecution(data.getOrganizationExecution());
            plan.setCompletionTime(data.getCompletionTime());
            plan.setResultExecution(data.getResultExecution());
            plan.setOrganizationTest(data.getOrganizationTest());

            plan.setEndDate(data.getEndDate());
            plan.setProjectId(data.getProjectId());
            plan.setStatus(0);
            LocalDateTime ldt = LocalDateTime.now();
        if (data.getStartDate() == null || data.getStartDate().isEmpty()) {
            // Nếu data.getStartDate() là null hoặc rỗng, đặt giá trị mặc định
            plan.setStartDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));
        } else {
            // Nếu ngày được chọn từ frontend, sử dụng giá trị từ data.getStartDate()
            plan.setStartDate(data.getStartDate());
        }
            plan.setCreateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));
            planService.addPlan(schema, plan);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update/{customerId}")
    public ResponseEntity<?> updatePlan(@PathVariable final String customerId, @RequestBody final PlanForm data) {
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        LocalDateTime ldt = LocalDateTime.now();

        Plan plan = new Plan();
        plan.setPlanId(data.getPlanId());
        plan.setSystemTypeId(data.getSystemTypeId());
        plan.setProjectId(data.getProjectId());
        plan.setContent(data.getContent());
        plan.setCompletionTime(data.getCompletionTime());
        plan.setResultExecution(data.getResultExecution());
        plan.setOrganizationTest(data.getOrganizationTest());
        plan.setOrganizationCreate(data.getOrganizationCreate());
        plan.setOrganizationExecution(data.getOrganizationExecution());
        plan.setEndDate(data.getEndDate());
        plan.setStartDate(data.getStartDate());
        plan.setStatus(data.getStatus());
        plan.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));
        planService.updatePlan(schema, plan);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }
    @GetMapping("/getPlanById")
    public ResponseEntity<?> getPlanById(@RequestParam ("customerId") final Integer customerId,
                                         @RequestParam ("planId") final Integer planId) {
        String schema = Schema.getSchemas(customerId);
        Plan plan = planService.getPlanById(schema, planId);
        System.out.println(plan);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }
//    @DeleteMapping("/deletePlanById/{customerId}")
//    public ResponseEntity<?> deletePlanById(
//            @PathVariable("customerId") final String customerId,
//            @RequestParam ("planId") final Integer planId) {
//        String schema = Schema.getSchemas(Integer.valueOf(customerId));
//        System.out.println(schema);
//        System.out.println(planId);
//        planService.deletePlanById(schema, planId);
//
//        return ResponseEntity.ok("Bản ghi đã được xóa.");
//    }
    @DeleteMapping("/deletePlanById")
    public ResponseEntity<?> deletePlanById(
            @RequestParam ("customerId") final Integer customerId,
            @RequestParam ("planId") final Integer planId) {
        String schema = Schema.getSchemas(customerId);
        System.out.println(schema);
        System.out.println(planId);
        planService.deletePlanById(schema, planId);

        return ResponseEntity.ok("Bản ghi đã được xóa.");
    }

}
