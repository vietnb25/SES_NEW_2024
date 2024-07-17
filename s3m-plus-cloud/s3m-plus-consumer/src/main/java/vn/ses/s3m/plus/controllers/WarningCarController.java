package vn.ses.s3m.plus.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import vn.ses.s3m.plus.dto.WarningCar;
import vn.ses.s3m.plus.form.WarningCarForm;
import vn.ses.s3m.plus.service.WarningCarService;

/**
 * Xử lý về phiếu yêu cầu hành động khắc phục.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@Validated
@RestController
@RequestMapping ("/common/warning-car")
public class WarningCarController {
	
    private static final Integer PAGE_SIZE = 20;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String DEVICE_ID = "deviceId";

    private static final Integer TYPE_LOAD = 1;

    
    @Autowired
    private WarningCarService warningCarService;

		
    /** Logging */
    private final Log log = LogFactory.getLog(WarningCarController.class);

    /**
     * Thêm mới một phiếu CAR.
     *
     * @param areaForm Thông tin phiếu CAR.
     * @return Thông tin phiếu CAR, 200(Thêm mới phiếu CAR thành công).
     */
    @PostMapping ("/add/{customerId}")
    public ResponseEntity<WarningCar> addWarningCar(@PathVariable final String customerId,
    		@RequestBody final WarningCarForm warningCarForm) {
    	
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        log.info("WarningCarController.addWarningCar: start!");
        LocalDateTime ldt = LocalDateTime.now();
        WarningCar warningCar = new WarningCar();
        warningCar.setSystemTypeId(warningCarForm.getSystemTypeId());
        warningCar.setProjectId(warningCarForm.getProjectId());
        warningCar.setDeviceId(warningCarForm.getDeviceId());
        warningCar.setStatus(1);
        warningCar.setCreateId(warningCarForm.getCreateId());
        warningCar.setOrganizationCreate(warningCarForm.getOrganizationCreate());
        warningCar.setContent(warningCarForm.getContent());
        warningCar.setCreateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
                .format(ldt));
        warningCar.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
                .format(ldt));
        warningCarService.addWarningCar(schema, warningCar);

        log.info("WarningCarController.addWarningCar: end!");


        return new ResponseEntity<WarningCar>(warningCar ,HttpStatus.OK);
    }
    
    @GetMapping ("")
    public ResponseEntity<?> getWarningCars(
    	@RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("customerId") final Integer customerId,
        @RequestParam(value = "fromDate", required = false) final String fromDate,
        @RequestParam(value = "toDate", required = false) final String toDate,
        @RequestParam (value = "projectId", required = false) final Integer projectId, @RequestParam ("page") final Integer page,    
        @RequestParam (value = "deviceId", required = false) final Integer deviceId) {
    	
        log.info("getWarningCars START");
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);

        if (projectId != null) {
            condition.put(PROJECT_ID, projectId);
        }

        if (deviceId != null && deviceId != 0) {
            condition.put("deviceId", deviceId);
        }

        condition.put("systemTypeId", systemTypeId);

        // Check for fromDate and toDate before adding them to the condition
        if (fromDate != null && toDate != null) {
            condition.put("fromDate", fromDate);
            condition.put("toDate", toDate);
        }

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        List<WarningCar> warningCars = warningCarService.getWarningCars(condition);
        for (WarningCar warnCar : warningCars) {
            String tDate = warnCar.getCreateDate()
                .substring(0, 19);
            warnCar.setCreateDate(tDate);
        }
        Integer countStatus = warningCarService.getCountWarningCarByStatus(condition);
        List<WarningCar> totalData = warningCarService.getCountListWarningCar(condition);
        double totalPage = Math.ceil((double) totalData.size() / PAGE_SIZE);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", warningCars);  
        mapData.put("countStatus", countStatus); 
        log.info("getWarningCars END");

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }
    
    
    @PutMapping("/update/{customerId}")
    public ResponseEntity<?> updateWarningCar(@PathVariable final String customerId,
       @RequestBody final WarningCarForm warningCarForm) {
    	
        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        log.info("WarningCarController.updateWarningCar: start!");
        LocalDateTime ldt = LocalDateTime.now();
     
        WarningCar warningCar = new WarningCar();
        warningCar.setId(warningCarForm.getId());
        warningCar.setSystemTypeId(warningCarForm.getSystemTypeId());
        warningCar.setProjectId(warningCarForm.getProjectId());
        warningCar.setDeviceId(warningCarForm.getDeviceId());
        warningCar.setContent(warningCarForm.getContent());
        
        if(warningCarForm.getResultExecution() != null) {
        	warningCar.setStatus(3);
        	String completionDate = warningCarForm.getCompletionTime();
        	if(completionDate == null || completionDate == "") {
            	warningCar.setCompletionTime (DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
              .format(ldt));
        	}else {
            	warningCar.setCompletionTime(warningCarForm.getCompletionTime());
        	}
        	warningCar.setOrganizationTest(warningCarForm.getOrganizationTest());
        }else if(warningCarForm.getReasonMethod() == null || warningCarForm.getReasonMethod() == "") {     	   
        	warningCar.setStatus(1);
        }else {
        	warningCar.setStatus(2);
        }
        
        warningCar.setCreateId(warningCarForm.getCreateId());
        warningCar.setOrganizationCreate(warningCarForm.getOrganizationCreate());
        warningCar.setContent(warningCarForm.getContent());                           
        warningCar.setOrganizationExecution(warningCarForm.getOrganizationExecution());
        warningCar.setReasonMethod(warningCarForm.getReasonMethod());
        warningCar.setResultExecution(warningCarForm.getResultExecution());
        warningCar.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH)
                .format(ldt));
        warningCarService.updateWarningCars(schema, warningCar);

        log.info("WarningCarController.updateWarningCar: end!");


        return new ResponseEntity<>(warningCar ,HttpStatus.OK);
    }
    
    @GetMapping ("/getWarningCarById")
    public ResponseEntity<?> getWarningCarById(
        @RequestParam ("customerId") final Integer customerId, 
        @RequestParam ("warningCarId") final Integer warningCarId) {
    	
        log.info("getWarningCarsById START");
        String schema = Schema.getSchemas(customerId);
  
        WarningCar warningCar = warningCarService.getWarningCarById(schema, warningCarId);
       
        log.info("getWarningCarsById END");

        return new ResponseEntity<>(warningCar ,HttpStatus.OK);
    }
    
}
