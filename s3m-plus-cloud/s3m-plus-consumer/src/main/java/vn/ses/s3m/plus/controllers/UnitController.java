package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.Unit;
import vn.ses.s3m.plus.service.UnitService;

@RestController
@RequestMapping ("/common/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("")
    public ResponseEntity<List<Unit>> getUnits() {
        List<Unit> respone = new ArrayList<>();
        respone = unitService.getListUnit();

        return new ResponseEntity<List<Unit>>(respone, HttpStatus.OK);
    }
}