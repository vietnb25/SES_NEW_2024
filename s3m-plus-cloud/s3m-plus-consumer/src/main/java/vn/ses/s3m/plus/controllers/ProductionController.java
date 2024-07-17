package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Production;
import vn.ses.s3m.plus.response.PlanResponse;
import vn.ses.s3m.plus.service.ProductionService;

@RestController
@RequestMapping ("/common/")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("production")
    public ResponseEntity<List<Production>> getListProduction(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId) {
        List<Production> respone = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        respone = productionService.getListProduction(condition);

        return new ResponseEntity<List<Production>>(respone, HttpStatus.OK);
    }

    @GetMapping ("production_step")
    public ResponseEntity<List<Production>> getListProductionStep(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("productionId") final Integer productionId,
        @RequestParam (value = "projectId", required = false) final Integer projectId) {
        List<Production> respone = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        condition.put("productionId", productionId);

        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        respone = productionService.getListProductionStep(condition);
        return new ResponseEntity<List<Production>>(respone, HttpStatus.OK);
    }

    @PostMapping ("/add_production/{customerId}")
    public ResponseEntity<PlanResponse> addProduction(@PathVariable final String customerId,
        @RequestBody final Production data) {

        String schema = Schema.getSchemas(Integer.parseInt(customerId));
        Production production = new Production();
        production.setProductionName(data.getProductionName());
        production.setProjectId(data.getProjectId());
        production.setUnit(data.getUnit());
        productionService.addProduction(schema, production);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping ("/add_production_step/{customerId}")
    public ResponseEntity<PlanResponse> addProductionStep(@PathVariable final String customerId,
        @RequestBody final Production data) {
        if(data.getProductionId() != null) {
            String schema = Schema.getSchemas(Integer.parseInt(customerId));
            Production production = new Production();
            production.setProductionStepName(data.getProductionStepName());
            production.setProductionId(data.getProductionId());
            productionService.addProductionStep(schema, production);
        }




        return new ResponseEntity<>(HttpStatus.OK);
    }
}