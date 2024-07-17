package vn.ses.s3m.plus.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.SystemType;
import vn.ses.s3m.plus.service.SystemTypeService;

/**
 * Controller Xử lý kiểu hệ thống
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-09
 */
@RestController
@RequestMapping ("/common/system-type")
public class SystemTypeController {

    @Autowired
    private SystemTypeService systemTypeService;

    /**
     * Danh sách loại hệ thống
     *
     * @return Danh sách loại hệ thống
     */
    @GetMapping ("/list")
    ResponseEntity<List<SystemType>> getListSystemType() {
        return new ResponseEntity<List<SystemType>>(systemTypeService.getSystemTypes(), HttpStatus.OK);
    }

    /**
     * Thông tin loại hệ thống theo Id
     *
     * @param systemTypeId Mã hệ thống
     * @return Loại hệ thống
     */
    @GetMapping ("/{systemTypeId}")
    ResponseEntity<SystemType> getSystemTypeById(@PathVariable ("systemTypeId") final Integer systemTypeId) {
        return new ResponseEntity<SystemType>(systemTypeService.getSystemTypeById(systemTypeId), HttpStatus.OK);
    }
}
