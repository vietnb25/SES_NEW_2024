package vn.ses.s3m.plus.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.*;
import vn.ses.s3m.plus.response.DeviceLostSignalResponse;
import vn.ses.s3m.plus.service.*;

@RestController
@RequestMapping ("/common/warning")
public class WarningListController {

    // khai báo tham số
    private static final Integer PAGE_SIZE = 20;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String DEVICE_ID = "deviceId";

    private static final String WARNING_TYPE = "warningType";

    private static final Integer TYPE_GRID = 5;

    private static final Integer TYPE_SOLAR = 2;

    private static final Integer TYPE_METER = 1;

    private static final Integer TYPE_WIND = 4;

    private static final Integer TYPE_BATTERY = 3;

    // Chưa định nghĩa trên tài liệu
    private static final Integer TYPE_DEVICE_STMV = 0;

    // Chưa định nghĩa trên tài liệu
    private static final Integer TYPE_DEVICE_SGMV = 0;

    /** Logging */
    private final Log log = LogFactory.getLog(WarningListController.class);

    private String folderName;

    @Autowired
    private WarningService warningService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private DataLoadFrame1Service dataLoadFrame1Service;

    @Autowired
    private DataLoadFrame2Service dataLoadFrame2Service;

    @Autowired
    private DataInverterService dataInverterService;

    @Autowired
    private DataTempHumidityService dataTempHumidityService;

    @Autowired
    private DataRmuDrawer1Service dataRmuDrawer1Service;

    @Autowired
    private DataPDAMSService dataPDAMSService;

    @Autowired
    private DataPDHTRService dataPDHTRService;
    
    @Autowired
    private DataFlowService dataFlowService;
    
    @Autowired
    private DataPressureService dataPressureService;

    @Autowired
    private DeviceService deviceService;

    /**
     * Lấy thông tin cảnh báo theo thời gian.
     *
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId Thời gian kết thúc.
     * @return Danh sách tổng cảnh báo theo từng thời điểm.
     */
    @GetMapping ("")
    public ResponseEntity<?> getWarnings(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "dvId", required = false) final String dvId,
        @RequestParam (value = "ids", required = false) final String ids) {
        log.info("getWarnings START");

        String deviceId = "";

        if (dvId != "") {
            deviceId = dvId;
        } else {
            deviceId = null;
        }
       
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", schema);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put("systemTypeId", systemTypeId);
        condition.put("deviceId", deviceId);
        
    	if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

        // list warning
        List<Warning> warnings = warningService.getTotalWarning(condition);

        // totalDeviceHasWarning (by WarningType)
        Integer devicesWarning = warningService.getAllDeviceHasWarning(condition);
        // tổng cảnh báo
        Map<String, Long> warningMap = new HashMap<>();

        warningMap.put("devicesWarning", (long) devicesWarning);

        for (Warning warning : warnings) {
            Integer warningType = warning.getWarningType();
            switch (warningType) {
                case Constants.WarningTypeMeter.NGUONG_AP_CAO:
                    warningMap.put("nguongApCao", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.NGUONG_AP_THAP:
                    warningMap.put("nguongApThap", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.QUA_TAI:
                    warningMap.put("quaTai", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.HE_SO_CONG_SUAT_THAP:
                    warningMap.put("heSoCongSuatThap", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.TAN_SO_CAO:
                    condition.put("warningType", "105, 209");
                    warningMap.put("tanSoCao", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.TAN_SO_THAP:
                    condition.put("warningType", "106, 210");
                    warningMap.put("tanSoThap", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.LECH_PHA:
                    warningMap.put("lechPha", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.SONG_HAI_DONG_DIEN_BAC_N:
                    condition.put("warningType", "108, 109, 110, 111");
                    warningMap.put("songHai", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.SONG_HAI_DIEN_AP_BAC_N:
                    condition.put("warningType", "108, 109, 110, 111");
                    warningMap.put("songHai", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.TONG_MEO_SONG_HAI_DIEN_AP:
                    condition.put("warningType", "108, 109, 110, 111");
                    warningMap.put("songHai", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.TONG_MEO_SONG_HAI_DONG_DIEN:
                    condition.put("warningType", "108, 109, 110, 111");
                    warningMap.put("songHai", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeMeter.NGUOC_PHA:
                    warningMap.put("nguocPha", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeMeter.MAT_DIEN_TONG:
                    warningMap.put("matDienTong", warning.getTotalDevice());
                    break;

                // Thiết bị Inverter
                case Constants.WarningTypeInverter.NHIET_DO_CAO:
                    condition.put("warningType", "208, 301");
                    warningMap.put("nhietDoCao", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeInverter.NHIET_DO_THAP:
                    condition.put("warningType", "214, 302");
                    warningMap.put("nhietDoThap", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeInverter.MAT_KET_NOI_AC:
                    warningMap.put("matKetNoiAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.MAT_KET_NOI_DC:
                    warningMap.put("matKetNoiDC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.DIEN_AP_CAO_AC:
                    warningMap.put("dienApCaoAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.DIEN_AP_THAP_AC:
                    warningMap.put("dienApThapAC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.TAN_SO_THAP:
                    condition.put("warningType", "106, 210");
                    warningMap.put("tanSoThap", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeInverter.TAN_SO_CAO:
                    condition.put("warningType", "105, 209");
                    warningMap.put("tanSoCao", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeInverter.MAT_NGUON_LUOI:
                    warningMap.put("matNguonLuoi", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.CHAM_DAT:
                    warningMap.put("chamDat", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.HONG_CAU_CHI:
                    warningMap.put("hongCauChi", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.DONG_MO_CUA:
                    condition.put("warningType", "206, 403");
                    warningMap.put("dongMoCua", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeInverter.DIEN_AP_CAO_DC:
                    warningMap.put("dienApCaoDC", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeInverter.MEMORY_LOSS:
                    warningMap.put("matBoNho", warning.getTotalDevice());

                    // Thiết bị Temp-Humidity sensor
                case Constants.WarningTypeTempHumidity.NHIET_DO_CAO:
                    condition.put("warningType", "208, 301");
                    warningMap.put("nhietDoCao", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeTempHumidity.NHIET_DO_THAP:
                    condition.put("warningType", "214, 302");
                    warningMap.put("nhietDoThap", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeTempHumidity.DO_AM_CAO:
                    condition.put("warningType", "303, 304");
                    warningMap.put("doAm", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeTempHumidity.DO_AM_THAP:
                    condition.put("warningType", "303, 304");
                    warningMap.put("doAm", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;

                // Thiến bị Status sensor
                case Constants.WarningTypeStatus.FI_TU_RMU:
                    warningMap.put("FITuRMU", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.KHOANG_TON_THAT:
                    warningMap.put("khoangTonThat", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.DONG_MO_CUA:
                    condition.put("warningType", "206, 403");
                    warningMap.put("dongMoCua", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                        .toString()));
                    break;
                case Constants.WarningTypeStatus.MUC_DAU_THAP:
                    warningMap.put("mucDauThap", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.ROLE_GAS:
                    warningMap.put("roleGas", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.CHAM_VO:
                    warningMap.put("chamVo", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.MUC_DAU_CAO:
                    warningMap.put("mucDauCao", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.CAM_BIEN_HONG_NGOAI:
                    warningMap.put("camBienHongNgoai", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.AP_SUAT_NOI_BO_MBA:
                    warningMap.put("apSuatNoiBoMBA", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.ROLE_NHIET_DO_DAU:
                    warningMap.put("roleNhietDoDau", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.NHIET_DO_CUON_DAY:
                    warningMap.put("nhietDoCuonDay", warning.getTotalDevice());
                    break;
                case Constants.WarningTypeStatus.KHI_GAS_MBA:
                    warningMap.put("khiGasMBA", warning.getTotalDevice());
                    break;
//                    Thiết bị phóng điện
                case Constants.WarningTypeDischarge.PHONG_DIEN_HTR:
                    condition.put("warningType", "501,601");
                    warningMap.put("phongDien", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                            .toString()));
                    break;
                case Constants.WarningTypeDischarge.PHONG_DIEN_AMS:
                    condition.put("warningType", "501,601");
                    warningMap.put("phongDien", Long.parseLong(warningService.getAllDeviceHasWarning(condition)
                            .toString()));
                    break;
                default:
                    break;
            }
        }

        log.info("getWarnings END");
        return new ResponseEntity<>(warningMap, HttpStatus.OK);
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
    public ResponseEntity<?> showDataWarningByDevice(@RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("warningType") final String warningType, @RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("projectId") final Integer projectId,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("category") final Integer warningCategory, @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);

        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put(DEVICE_ID, deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put(WARNING_TYPE, warningType);
        condition.put(PROJECT_ID, projectId);

        String[] year = fromDate.split("-");
        String currentYear = year[0];
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);

        Map<String, Object> mapData = new HashMap<>();

        mapData.put("warningCategory", warningCategory);

        if (systemTypeId == TYPE_METER) {
            List<DataLoadFrame1> dataWarning = dataLoadFrame1Service.getWarningDataLoadByDeviceId(condition);
            mapData.put("dataWarning", dataWarning);
            {
                String settingValue = settingService.getSettingValue(condition);
                mapData.put("settingValue", settingValue);
            }

        } else if (systemTypeId == TYPE_SOLAR) {
            // tìm thông tin loại thiết bị để xác định cảnh báo
            Device device = deviceService.getDeviceByDeviceId(condition);

            if (device.getDeviceType() == Constants.DeviceType.INVERTER) {
                List<DataInverter1> dataWarning = dataInverterService.getWarningDataInverterByDeviceId(condition);
                mapData.put("dataWarning", dataWarning);
                mapData.put("deviceType", Constants.DeviceType.INVERTER);
            }
            // else if (device.getDeviceType() == Constants.DeviceType.COMBINER) {
            // List<DataCombiner1> dataWarning = operationCombinerPVService.getDataCombinerPV(condition);
            // mapData.put("dataWarning", dataWarning);
            // mapData.put("deviceType", Constants.DeviceType.COMBINER);
            // } else if (device.getDeviceType() == Constants.DeviceType.STRING) {
            // List<DataString1> dataWarning = operationStringPVService.getOperationStringPV(condition);
            // mapData.put("dataWarning", dataWarning);
            // mapData.put("deviceType", Constants.DeviceType.STRING);
            // }

        } else if (systemTypeId == TYPE_GRID) {
            List<DataRmuDrawer1> dataWarning = dataRmuDrawer1Service.getWarningDataRMUByDeviceId(condition);
            mapData.put("dataWarning", dataWarning);
            // Tìm cài đặt của nhiệt độ
            if (Integer.parseInt(warningType) == Constants.WARNING_RMU.NHIET_DO) {
                condition.put("type", TYPE_GRID);
                List<Setting> listSetting = settingService.getSettingValues(condition);
                String after = "", before = "";
                for (Setting setting : listSetting) {

                    if ( (setting.getWarningType() != null)
                        && setting.getWarningType() == Constants.WARNING_RMU.NHIET_DO) {
                        if (setting.getDescription()
                            .contains("SAW_ID1")) {
                            before = setting.getSettingValue();
                        } else {
                            after = setting.getSettingValue();
                        }
                    }
                }
                String settingValue = before + "," + after;
                mapData.put("settingValue", settingValue);
            } else {
                String settingValue = settingService.getSettingValue(condition);
                mapData.put("settingValue", settingValue);
            }
        } else if (systemTypeId == TYPE_WIND) {

        } else if (systemTypeId == TYPE_BATTERY) {
        }
        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    @GetMapping ("/listWarnedDevice")
    public ResponseEntity<?> listWarnedDevice(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam (value = "warningType", required = false) final String warningType,
    	@RequestParam (value = "ids", required = false) final String ids){

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", schema);
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put("systemTypeId", systemTypeId);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        if (warningType != null && warningType.compareTo("ALL") != 0) {
            condition.put("warningType", warningType);
        }
    	if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
        List<Device> warnedDevice = deviceService.getWarnedDevice(condition);
        for (Device item : warnedDevice) {
            condition.put("deviceId", item.getDeviceId());
            List<Warning> warnings = warningService.getWarningsByCustomerId(condition);
            Collections.sort(warnings, (o1, o2) -> o2.getWarningLevel()
                .compareTo(o1.getWarningLevel()));
            item.setListWarning(warnings);
        }

        return new ResponseEntity<>(warnedDevice, HttpStatus.OK);
    }

    @GetMapping ("/getInfoWarnedDevice")
    public ResponseEntity<?> getInfoWarnedDevice(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "deviceId") final Integer deviceId,
        @RequestParam (value = "warningType") final String warningType,
        @RequestParam (value = "toDate") final String toDate) {
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> inforData = new HashMap<>();
        condition.put("schema", schema);
        condition.put("deviceId", deviceId);
        condition.put("warningType", warningType);
        condition.put("toDate", toDate);
        String[] year = toDate.split("-");
        String currentYear = year[0];
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        Object data = new Object();
        String setting = settingService.getSettingValue(condition);
        Device device = deviceService.getDeviceByDeviceId(condition);
        Integer deviceType = device.getDeviceType();
        inforData.put("setting", setting);

        if (deviceType == 1) {
            data = new DataLoadFrame1();
            data = dataLoadFrame1Service.getInforDataLoadByTime(condition);
            inforData.put("data", data);

        } else if (deviceType == 2) {
            data = new DataInverter1();
            data = dataInverterService.getInforDataInverterByTime(condition);
            inforData.put("data", data);
        } else if (deviceType == 3) {
            data = new DataTempHumidity();
            data = dataTempHumidityService.getInforDataTempHumidityByTime(condition);
            inforData.put("data", data);
        }else if (deviceType == 5) {
            data = dataPDHTRService.getInforDeviceByWarningHTR02(condition);
            inforData.put("data", data);
        }else if (deviceType == 6) {
            data = dataPDAMSService.getInforDeviceByWarningAMS01(condition);
            inforData.put("data", data);
        }else if (deviceType == 7) {
            data = dataPressureService.getInforDataPressureByTime(condition);
            inforData.put("data", data);
        }else if (deviceType == 10) {
            data = dataFlowService.getInforDataFlowByTime(condition);
            inforData.put("data", data);
        }

        return new ResponseEntity<>(inforData, HttpStatus.OK);
    }

    @GetMapping ("/getInfoWarnedDeviceFrame2")
    public ResponseEntity<?> getInfoWarnedDeviceFrame2(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "deviceId") final Integer deviceId,
        @RequestParam (value = "warningType") final String warningType,
        @RequestParam (value = "toDate") final String toDate) {
        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> inforDataFrame2 = new HashMap<>();
        condition.put("schema", schema);
        condition.put("deviceId", deviceId);
        condition.put("warningType", warningType);
        condition.put("toDate", toDate);
        Object dataFrame2 = new Object();
        String setting = settingService.getSettingValue(condition);
        Device device = deviceService.getDeviceByDeviceId(condition);
        Integer deviceType = device.getDeviceType();
        inforDataFrame2.put("setting", setting);
        String[] year = toDate.split("-");
        String currentYear = year[0];
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        if (deviceType == 1) {
            dataFrame2 = new DataLoadFrame2();
            dataFrame2 = dataLoadFrame2Service.getInforDataFrame2LoadByTime(condition);
            inforDataFrame2.put("dataFrame2", dataFrame2);
        }
        return new ResponseEntity<>(inforDataFrame2, HttpStatus.OK);
    }

    @GetMapping ("/getListDataWarning")
    public ResponseEntity<?> getListDataWarning(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "deviceId") final Integer deviceId,
        @RequestParam (value = "warningType") final String warningType,
        @RequestParam (value = "fromDate") final String fromDate,
        @RequestParam (value = "toDate") final String toDate) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> inforData = new HashMap<>();
        condition.put("schema", schema);
        condition.put("deviceId", deviceId);
        condition.put("warningType", warningType);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        String[] year = toDate.split("-");
        String currentYear = year[0];
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        Device device = deviceService.getDeviceByDeviceId(condition);
        Integer deviceType = device.getDeviceType();
        List<Setting> settingList = settingService.getSettingHistory(condition);
        inforData.put("settingList", settingList);
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        if (deviceType == 1) {
            List<DataLoadFrame1> data = new ArrayList<DataLoadFrame1>();
            condition.put("fiveMinute", true);
            data = dataLoadFrame1Service.getListWarnedData(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataLoadFrame1> dataCurent = dataLoadFrame1Service.getListWarnedData(condition);

            for (DataLoadFrame1 item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataLoadFrame1>() {
                @Override
                public int compare(DataLoadFrame1 o1, DataLoadFrame1 o2) {
                    return o1.getId() - o2.getId();
                }
            });
            inforData.put("data", data);

        } else if (deviceType == 2) {
            List<DataInverter1> data = new ArrayList<DataInverter1>();
            condition.put("fiveMinute", true);
            data = dataInverterService.getListWarnedData(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataInverter1> dataCurent = dataInverterService.getListWarnedData(condition);
            for (DataInverter1 item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataInverter1>() {
                @Override
                public int compare(DataInverter1 o1, DataInverter1 o2) {
                    return Integer.parseInt(o1.getId()
                        .toString()) - Integer.parseInt(
                            o2.getId()
                                .toString());
                }
            });
            inforData.put("data", data);
        } else if (deviceType == 3) {
            List<DataTempHumidity> data = new ArrayList<DataTempHumidity>();
            condition.put("fiveMinute", true);
            data = dataTempHumidityService.getListWarnedData(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataTempHumidity> dataCurent = dataTempHumidityService.getListWarnedData(condition);
            for (DataTempHumidity item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataTempHumidity>() {
                @Override
                public int compare(DataTempHumidity o1, DataTempHumidity o2) {
                    return Integer.parseInt(o1.getId()
                        .toString()) - Integer.parseInt(
                            o2.getId()
                                .toString());
                }
            });
            inforData.put("data", data);
        }else if(deviceType == 5) {
            condition.put("fromDate", sdf.format(Timestamp.valueOf(fromDate)));
            condition.put("toDate", sdf.format(Timestamp.valueOf(toDate)));
            List<DataPDHTR02> data = this.dataPDHTRService.getListHTRIndicatorByDeviceId(condition);
            inforData.put("data", data);
        } else if(deviceType == 6) {
            List<DataPDAMS01> data = this.dataPDAMSService.getListAMSIndicatorByDeviceId(condition);
        } else if (deviceType == 7) {
            List<DataPressure> data = new ArrayList<DataPressure>();
            condition.put("fiveMinute", true);
            data = dataPressureService.getListWarnedData(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataPressure> dataCurent = dataPressureService.getListWarnedData(condition);
            for (DataPressure item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataPressure>() {
                @Override
                public int compare(DataPressure o1, DataPressure o2) {
                    return Integer.parseInt(o1.getId()
                        .toString()) - Integer.parseInt(
                            o2.getId()
                                .toString());
                }
            });
            inforData.put("data", data);
        } else if (deviceType == 10) {
            List<DataFlow> data = new ArrayList<DataFlow>();
            condition.put("fiveMinute", true);
            data = dataFlowService.getListWarnedData(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataFlow> dataCurent = dataFlowService.getListWarnedData(condition);
            for (DataFlow item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataFlow>() {
                @Override
                public int compare(DataFlow o1, DataFlow o2) {
                    return Integer.parseInt(o1.getId()
                        .toString()) - Integer.parseInt(
                            o2.getId()
                                .toString());
                }
            });
            inforData.put("data", data);
        }
        return new ResponseEntity<>(inforData, HttpStatus.OK);
    }

    @GetMapping ("/getListDataWarningFrame2")
    public ResponseEntity<?> getListDataWarningFrame2(@RequestParam ("customerId") final Integer customerId,
        @RequestParam ("systemTypeId") final Integer systemTypeId,
        @RequestParam (value = "deviceId") final Integer deviceId,
        @RequestParam (value = "warningType") final String warningType,
        @RequestParam (value = "fromDate") final String fromDate,
        @RequestParam (value = "toDate") final String toDate) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> inforData = new HashMap<>();
        condition.put("schema", schema);
        condition.put("deviceId", deviceId);
        condition.put("warningType", warningType);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        String[] year = toDate.split("-");
        String currentYear = year[0];
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        Device device = deviceService.getDeviceByDeviceId(condition);
        Integer deviceType = device.getDeviceType();
        List<Setting> settingList = settingService.getSettingHistory(condition);
        inforData.put("settingList", settingList);
        if (deviceType == 1) {
            List<DataLoadFrame2> data = new ArrayList<DataLoadFrame2>();
            condition.put("fiveMinute", true);
            data = dataLoadFrame2Service.getListWarnedDataFrame2(condition);

            condition.put("fiveMinute", false);
            condition.put("limit", 1);
            List<DataLoadFrame2> dataCurent = dataLoadFrame2Service.getListWarnedDataFrame2(condition);
            for (DataLoadFrame2 item : dataCurent) {
                data.add(item);
            }
            Collections.sort(data, new Comparator<DataLoadFrame2>() {
                @Override
                public int compare(DataLoadFrame2 o1, DataLoadFrame2 o2) {
                    return (int) (o1.getId() - o2.getId());
                }
            });
            inforData.put("data", data);

        }

        return new ResponseEntity<>(inforData, HttpStatus.OK);
    }

    //Lấy thiết bị cảnh báo mất tín hiệu
    @GetMapping("/listDeviceLostSignal")
    public ResponseEntity<List<DeviceLostSignalResponse>> listDeviceLostSignal(@RequestParam ("customerId") final Integer customerId,
                                                  @RequestParam (value = "projectId", required = false) final Integer projectId,
                                                  @RequestParam (value = "systemTypeId", required = false) final Integer systemTypeId) {
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        if (systemTypeId != null) {
            condition.put("systemTypeId", systemTypeId);
        }


        List<DeviceLostSignalResponse> listDeviceLostSignal = deviceService.getWarnedDeviceLostSignal(condition);
        return new ResponseEntity<List<DeviceLostSignalResponse>>(listDeviceLostSignal, HttpStatus.OK);

    }
}