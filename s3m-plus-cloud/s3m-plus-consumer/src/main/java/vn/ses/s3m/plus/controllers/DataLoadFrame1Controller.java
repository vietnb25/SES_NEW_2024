package vn.ses.s3m.plus.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.service.DataLoadFrame1Service;

/**
 * Lấy ra ngày của bản tin mới nhất
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-09
 */
@RestController
@RequestMapping ("/common/data-load-frame1")
@Validated
public class DataLoadFrame1Controller {

    @Autowired
    private DataLoadFrame1Service dataService;

    /**
     * Lấy ra ngày của bản tin mới nhất
     *
     * @return Ngày của bản tin mới nhất
     */
    @GetMapping ("/getDateNewDevice/{customerId}")
    public ResponseEntity<DataLoadFrame1> getDateNewDevice(@PathVariable ("customerId") final Integer customerId) {
        String schema = Schema.getSchemas(customerId);
        return new ResponseEntity<DataLoadFrame1>(dataService.getDateNewDevice(schema), HttpStatus.OK);
    }
}
