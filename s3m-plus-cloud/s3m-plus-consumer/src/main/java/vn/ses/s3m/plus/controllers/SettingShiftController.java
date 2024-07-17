package vn.ses.s3m.plus.controllers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Plan;
import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.dto.SettingShift;
import vn.ses.s3m.plus.dto.ShiftSetting;
import vn.ses.s3m.plus.form.SettingShiftForm;
import vn.ses.s3m.plus.response.SettingShiftResponse;
import vn.ses.s3m.plus.service.SettingService;
import vn.ses.s3m.plus.service.SettingShiftService;
import vn.ses.s3m.plus.service.ShiftSettingService;

/**
 * Controller xử lý cài đặt
 *
 * @author Wasiq Bhamla
 * @since 13 thg 1, 2023
 */
@RestController
@RequestMapping ("/common/setting-shift")
@Slf4j
public class SettingShiftController {

    @Autowired
    private ShiftSettingService shiftSettingService;

    @Autowired
    private SettingShiftService settingShiftService;


    /**
     * Lấy ra danh sách cài đặt.
     *
     * @param projectId Id dự án.
     * @param customerId Id khách hàng.
     * @return Danh sách cài đặt.
     */
    @GetMapping ("/")
    public ResponseEntity<List<ShiftSetting>> getListSetting( @RequestParam (value = "projectId", required = false) final Integer projectId) {
        log.info("getListSetting START");

        Map<String, Object> condition = new HashMap<String, Object>();
        if (projectId != null) {
            condition.put("projectId", projectId);
            List<ShiftSetting> listSettings = shiftSettingService.getSettingShift(condition);
            log.info("getListSetting END");
            return new ResponseEntity<List<ShiftSetting>>(listSettings, HttpStatus.OK);
        } else {

            log.info("getListSetting END");

            return new ResponseEntity<List<ShiftSetting>>(HttpStatus.OK);
        }
    }

    @PostMapping("/add/{projectId}")
    public ResponseEntity<?> addSettingShift(@RequestBody final ShiftSetting shiftSetting,
                                                @PathVariable (value = "projectId") final String projectId) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("shiftName", shiftSetting.getShiftName());
        condition.put("startTime", shiftSetting.getStartTime());
        condition.put("endTime", shiftSetting.getEndTime());
        shiftSettingService.addSettingShift(condition);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Lấy ra thông tin cài đặt theo Id
     *
     * @param settingId Id cài đặt.
     * @return Trả về thông tin cài đặt được lấy theo id.
     */
    @GetMapping ("/{settingId}")
    public ResponseEntity<ShiftSetting> getSettingById(@PathVariable final Integer settingId) {

        log.info("SettingShiftController.getSettingById START");

        ShiftSetting setting = shiftSettingService.getShiftsettingById(settingId);;
        log.info("END");

        return new ResponseEntity<ShiftSetting>(setting, HttpStatus.OK);
    }

    /**
     * Lấy ra thông tin cài đặt theo Id
     *
     * @param settingId Id cài đặt.
     * @return Trả về thông tin cài đặt được lấy theo id.
     */
    @GetMapping ("/getByProjectId")
    public ResponseEntity<ShiftSetting> getSettingByProjectId(@RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("projectId") final Integer projectId) {

        log.info("SettingShiftController.getSettingByProjectId START");
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        condition.put("customerId", customerId);
        condition.put("systemTypeId", systemTypeId);
        ShiftSetting setting = shiftSettingService.getShiftsetting(condition);
        log.info("END");

        return new ResponseEntity<ShiftSetting>(setting, HttpStatus.OK);
    }

    /**
     * Chỉnh sửa thông tin cài đặt
     *
     * @param settingId Id cài đặt.
     * @param setting Đối tượng cài đặt truyền vào.
     * @return Trả về giá trị cài đặt sau khi chỉnh sửa.
     * @return Trả về mã lỗi 400.
     */
    @PutMapping ("/update/{settingId}")
    public ResponseEntity<?> editSetting(@PathVariable final Integer settingId,
        @RequestBody final ShiftSetting setting) {

        log.info("SettingController.editSetting START");

        try {
            // lấy thời gian cập nhật
            Date createDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat();
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTime = formatter.format(createDate);
            setting.setToDate(strTime);
            setting.setUpdateDate(strTime);
            shiftSettingService.updateShiftHistory(setting);

            // Shift_history_code (updateDate)
            formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String strCode = formatter.format(createDate);

            setting.setShiftHistoryCode(setting.getCustomerId() + "-" + setting.getSystemTypeId() + "-"
                + setting.getProjectId() + "-" + strCode);

            shiftSettingService.updateShiftSetting(setting);
            setting.setCreateDate(strTime);
            setting.setId(null);
            shiftSettingService.addShiftHistory(setting);
            log.info("END");

            return new ResponseEntity<ShiftSetting>(setting, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("END");

            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping ("/get-list-by-project")
    public ResponseEntity<List<SettingShiftResponse>> getListByProject(
            @RequestParam ("project") final Integer project) {
        log.info("SettingShiftController.getListByProjectId START");
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("project", project);
        List<SettingShift> ls = this.settingShiftService.getSettingShiftByProject(condition);
        List<SettingShiftResponse> response = new ArrayList<>();
        for (SettingShift s: ls) {
            response.add(new SettingShiftResponse(s));
        }

        if(ls.size() <= 0) {
            log.info("SettingShiftController.getListByProjectId END -> NO CONTENT");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }else {
            log.info("SettingShiftController.getListByProjectId END -> SUCCESS");
            return new ResponseEntity<List<SettingShiftResponse>>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/add-by-project")
    public ResponseEntity<?> addSettingShiftProject(@RequestBody SettingShiftForm st) {
        log.info("SettingShiftController.addSettingShiftProject START");
        if(st.getProjectId() != null && st.getShiftName() != null &&
        st.getEndTime() != null && st.getStartTime() != null
        ){
            this.settingShiftService.addSettingShift(st);
            log.info("SettingShiftController.addSettingShiftProject SUCCESS");
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            log.info("SettingShiftController.addSettingShiftProject ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/lock/{id}")
    public ResponseEntity<?> lockSettingShift(@PathVariable("id") Integer id) {
        settingShiftService.updateStatusSettingShift(0, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteSettingShift(@PathVariable("id") Integer id) {
        settingShiftService.deleteSettingShift(-1, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/unlock")
    public ResponseEntity<?> unlockSettingShift(@RequestParam("id") Integer id) {
        log.info("SettingShiftController.unlockSettingShift START");
        this.settingShiftService.updateStatusSettingShift(1, id);
        log.info("SettingShiftController.unlockSettingShift SUCCESS");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update-setting-shift")
    public ResponseEntity<?> updateSettingShift(@RequestBody final SettingShiftForm form) {
        LocalDateTime ldt = LocalDateTime.now();
        SettingShift settingShift = new SettingShift();
        settingShift.setId(form.getId());
        settingShift.setShiftName(form.getShiftName());
        settingShift.setEndTime(form.getEndTime());
        settingShift.setStartTime(form.getStartTime());
        settingShift.setStatus(form.getStatus());
        settingShift.setUpdateDate(DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).format(ldt));

        settingShiftService.updateSettingShift(settingShift);
        return new ResponseEntity<>(settingShift, HttpStatus.OK);
    }

    @GetMapping("/getSettingShiftById")
    public ResponseEntity<?> getSettingShiftById(@RequestParam ("id") final Integer id) {
        SettingShift settingShift = settingShiftService.getSettingShiftById(id);
        return new ResponseEntity<>(settingShift, HttpStatus.OK);
    }

    @GetMapping ("/list")
    public ResponseEntity<List<ShiftSetting>> getListSettingByProjectAndStatus(
            @RequestParam (value = "projectId") final Integer projectId,
            @RequestParam (value = "status") final Integer status

    ) {
        log.info("getListSetting START");

        Map<String, Object> condition = new HashMap<String, Object>();
        if (projectId != null) {
            condition.put("projectId", projectId);
            condition.put("status", status);
            List<ShiftSetting> listSettings = shiftSettingService.getSettingShift(condition);
            log.info("getListSetting END");
            return new ResponseEntity<List<ShiftSetting>>(listSettings, HttpStatus.OK);
        } else {

            log.info("getListSetting END");

            return new ResponseEntity<List<ShiftSetting>>(HttpStatus.OK);
        }
    }

}
