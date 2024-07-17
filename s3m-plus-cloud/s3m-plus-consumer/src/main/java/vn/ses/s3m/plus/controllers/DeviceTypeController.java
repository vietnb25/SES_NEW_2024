package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.DeviceType;
import vn.ses.s3m.plus.dto.DeviceTypeMst;
import vn.ses.s3m.plus.service.DeviceTypeService;

/**
 * Controller Xử lý loại thiết bị
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-09
 */
@RestController
@RequestMapping ("/common/device-type")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    /**
     * Lấy danh sách loại thiết bị
     *
     * @return Danh sách loại thiết bị
     */
    @GetMapping ("/list")
    public ResponseEntity<List<DeviceTypeMst>> getListDeviceType() {
        return new ResponseEntity<List<DeviceTypeMst>>(deviceTypeService.getDeviceTypes(), HttpStatus.OK);
    }

    /**
     * Danh sách loại thiết bị theo loại hệ thống
     *
     * @param systemTypeId Mã loại thiết bị
     * @return Đối tượng loại thiết bị
     */
    @GetMapping ("/listDeviceType/{systemTypeId}")
    public ResponseEntity<List<DeviceType>> getDeviceTypesBySystemTypeId(
        @PathVariable ("systemTypeId") final String systemTypeId) {

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("systemTypeId", systemTypeId);

        List<DeviceType> listDeviceType = deviceTypeService.getDeviceTypesBySystemTypeId(condition);

        return new ResponseEntity<List<DeviceType>>(listDeviceType, HttpStatus.OK);

    }

}
