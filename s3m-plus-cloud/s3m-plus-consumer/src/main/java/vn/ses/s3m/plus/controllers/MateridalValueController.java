package vn.ses.s3m.plus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ses.s3m.plus.dto.MaterialValue;
import vn.ses.s3m.plus.dto.TypeTime;
import vn.ses.s3m.plus.form.MaterialForm;
import vn.ses.s3m.plus.service.MaterialValueService;
import vn.ses.s3m.plus.service.TypeTimeService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("common/material-value")
public class MateridalValueController {

    @Autowired
    private MaterialValueService materialValueService;

    @Autowired
    private TypeTimeService typeTimeService;
    Logger log = LoggerFactory.getLogger(MateridalValueController.class);
    @GetMapping("list-by-project-and-material-type")
    public ResponseEntity<List<MaterialValue>> getListByProjectAndMaterialType(
            @RequestParam("project") Integer project,
            @RequestParam("material") Integer materialType
    ) {
        log.info("MateridalValueController --> listAllMateriaValue");
        Map<String, Object> con= new HashMap<>();
        con.put("project", project);
        con.put("materialId", materialType);
        List<MaterialValue> ls = this.materialValueService.getMaterialValueByProjectAndType(con);
        if(ls.size() > 0) {
            return new ResponseEntity<>(ls, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(ls, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping ("add-or-update")
    public ResponseEntity<?> updateMaterialValue(@RequestBody MaterialForm form) {
        log.info("MateridalValueController --> update start");
        Map<String, Object> con= new HashMap<>();
        con.put("project", form.getProjectId());
        con.put("materialId", form.getMaterialId());
        List<MaterialValue> ls = this.materialValueService.getMaterialValueByProjectAndType(con);
        List<TypeTime> lsTypeTime = this.typeTimeService.getTypeTime();
        if(ls.size() <=0) {
            log.info("MateridalValueController --> create start");
            for (TypeTime typeTime: lsTypeTime) {
                MaterialValue value = new MaterialValue();
                value.setProjectId(form.getProjectId());
                value.setMaterialId(form.getMaterialId());
                value.setTypeTime(typeTime.getId());
                if(typeTime.getId() == 1) {
                    value.setMaterialPrice(form.getPeakHour());
                }
                if(typeTime.getId() == 2) {
                    value.setMaterialPrice(form.getNormalHour());
                }
                if(typeTime.getId() == 3) {
                    value.setMaterialPrice(form.getNonPeakHour());
                }
                if(typeTime.getId() == 4) {
                    value.setMaterialPrice(form.getVat());
                }
                this.materialValueService.addMaterialValue(value);

            }
            log.info("MateridalValueController --> create success");
        }else {
            log.info("MateridalValueController --> update start");
            for (MaterialValue value : ls) {
                if (value.getTypeTime() == 1) {
                    value.setMaterialPrice(form.getPeakHour());
                }
                if (value.getTypeTime() == 2) {
                    value.setMaterialPrice(form.getNormalHour());
                }
                if (value.getTypeTime() == 3) {
                    value.setMaterialPrice(form.getNonPeakHour());
                }
                if (value.getTypeTime() == 4) {
                    value.setMaterialPrice(form.getVat());
                }
                this.materialValueService.updateMaterialValue(value);
                log.info("MateridalValueController --> update success");
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
