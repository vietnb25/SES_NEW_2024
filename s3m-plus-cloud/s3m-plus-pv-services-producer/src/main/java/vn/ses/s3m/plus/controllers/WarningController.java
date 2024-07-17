package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.DataInverter;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.service.DataInverterService;
import vn.ses.s3m.plus.service.WarningService;

@CrossOrigin
@RestController
@RequestMapping ("/pv/warning")
public class WarningController {

    private static final Integer PAGE_SIZE = 50;

    @Autowired
    private WarningService warningService;

    @Autowired
    private DataInverterService dataInverterService;

    /**
     * Lấy thông tin cảnh báo theo thời gian.
     *
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId Thời gian kết thúc.
     * @return Danh sách tổng cảnh báo theo từng thời điểm.
     */
    @GetMapping ("/")
    public ResponseEntity<?> getWarnings(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("projectId") final String projectId) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        // list warning
        List<Warning> warnings = warningService.getTotalWarningPV(condition);
        // totalDeviceHasWarning
        Integer devicesWarning = warningService.getAllDeviceHasWarningPV(condition);
        // tổng cảnh báo
        Map<String, Long> warningMap = new HashMap<>();

        warningMap.put("devicesWarning", (long) devicesWarning);

        for (Warning warning : warnings) {
            Integer warningType = warning.getWarningType();
            switch (warningType) {
                case Constants.WarningTypePV.NHIET_DO_CAO:
                    warningMap.put("nhietDoCao", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.MAT_KET_NOI_AC:
                    warningMap.put("matKetNoiAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.MAT_KET_NOI_DC:
                    warningMap.put("matKetNoiDC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.DIEN_AP_CAO_AC:
                    warningMap.put("dienApCaoAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.DIEN_AP_THAP_AC:
                    warningMap.put("dienApThapAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.TAN_SO_THAP:
                    warningMap.put("tanSoThap", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.TAN_SO_CAO:
                    warningMap.put("tanSoCao", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.MAT_NGUON_LUOI:
                    warningMap.put("matNguonLuoi", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.CHAM_DAT:
                    warningMap.put("chamDat", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.HONG_CAU_CHI:
                    warningMap.put("hongCauChi", warning.getTotalDevice());
                    break;
                case Constants.WarningTypePV.DONG_MO_CUA:
                    warningMap.put("dongMoCua", warning.getTotalDevice());
                    break;
                default:
                    break;
            }
        }
        return new ResponseEntity<>(warningMap, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị cảnh báo theo id dự án và warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId ID của dự án.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/type/{warningType}")
    public ResponseEntity<?> detailWarningByType(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("projectId") final String projectId, @RequestParam ("page") final Integer page) {

        Map<String, Object> condition = new HashMap<>();
        if (!warningType.equals("ALL")) {
            condition.put("warningType", warningType);
        }
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put("projectId", projectId);

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        List<Warning> warnings = warningService.getDetailWarningType(condition);

        int totalData = warningService.countWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);

        mapData.put("data", warnings);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng thiết bị.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo thiết bị
     */
    @GetMapping ("/detail")
    public ResponseEntity<?> showDataWarningByDevice(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("page") final Integer page) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("warningType", warningType);

        List<DataInverter> dataWarning = dataInverterService.getDataInverterByDevice(condition);

        Map<String, Object> mapData = new HashMap<>();

        mapData.put("dataWarning", dataWarning);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }
}
