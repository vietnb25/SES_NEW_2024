package vn.ses.s3m.plus.controllers;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ses.s3m.plus.dto.MaterialType;
import vn.ses.s3m.plus.dto.TypeTime;
import vn.ses.s3m.plus.service.MaterialTypeService;
import vn.ses.s3m.plus.service.TypeTimeService;

import java.util.List;

@RestController
@RequestMapping("common/material-type")
public class MaterialTypeController {
    @Autowired
    private MaterialTypeService service;
    @Autowired
    private TypeTimeService typeTimeService;

    Logger log = LoggerFactory.getLogger(MaterialTypeController.class);

    @GetMapping("/list")
    public ResponseEntity<List<MaterialType>> listAllMaterialType(
    ) {
        log.info("MaterialTypeController --> listAllMaterialType");
        List<MaterialType> ls = this.service.getListMaterialType();
        List< TypeTime> lsTime = this.typeTimeService.getTypeTime();
        if(ls.size() > 0) {
        return new ResponseEntity<>(ls, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(ls, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMaterialType(@RequestBody MaterialType type) {
        log.info("MaterialTypeController --> addMaterialType start");
        this.service.addMaterialType(type);
        log.info("MaterialTypeController --> addMaterialType error");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMaterialType(@RequestBody MaterialType type) {
        log.info("MaterialTypeController --> updateMaterialType start");
        this.service.updateMaterialType(type);
        log.info("MaterialTypeController --> updateMaterialType error");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
