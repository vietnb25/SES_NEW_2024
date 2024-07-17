package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.DataPowerResult;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.DeviceMst;
import vn.ses.s3m.plus.dto.ObjectName;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.DeviceMstForm;
import vn.ses.s3m.plus.response.DeviceMstResponse;
import vn.ses.s3m.plus.response.DeviceResponse;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.ObjectService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.WarningService;

/**
 * Controller Xử lý thiết bị
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-09
 */
@RestController
@RequestMapping ("/common/device")
@Slf4j
public class DeviceController {

    // Tham số công suất AC & DC
    private static final Integer N = 1000;

    // Mã đối tượng Inverter
    private static final Integer OBJECT_TYPE_INVERTER = 34;

    // Mã đối tượng Combiner
    private static final Integer OBJECT_TYPE_COMBINER = 35;

    // Mã đối tượng String
    private static final Integer OBJECT_TYPE_STRING = 36;

    // Mã đối tượng GRID
    private static final Integer OBJECT_TYPE_RMU = 20;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private ObjectService objectService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CustomerService customerService;

    @Value ("${consumer.producer.export-folder}")
    private String folderName;

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("/list")
    public ResponseEntity<List<DeviceMstResponse>> getListDevice(
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "systemType", required = false) final String systemType,
        @RequestParam (value = "deviceType", required = false) final String deviceType) {

        List<DeviceMstResponse> deviceRes = new ArrayList<>();

        Map<String, String> condition = new HashMap<String, String>();
        if (projectId != null && projectId != "") {
            condition.put("projectId", projectId);
        }
        if (systemType != null && systemType != "") {
            condition.put("systemType", systemType);
        }
        if (deviceType != null && deviceType != "") {
            condition.put("deviceType", deviceType);
        }
        List<DeviceMst> devices = deviceService.getDevices(condition);

        for (DeviceMst device : devices) {
            DeviceMstResponse dr = new DeviceMstResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceMstResponse>>(deviceRes, HttpStatus.OK);
    }

    // CHECKSTYLE: ON

    /**
     * Lấy danh sách thiết bị theo từ khóa
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách thiết bị
     */
    @GetMapping ("/search")
    public ResponseEntity<List<DeviceMstResponse>> getListDeviceByCondition(
        @RequestParam ("keyword") final String keyword, @RequestParam ("customerId") final String customerId,
        @RequestParam ("projectId") final String projectId) {

        List<DeviceMstResponse> deviceRes = new ArrayList<>();

        Map<String, String> condition = new HashMap<String, String>();

        condition.put("keyword", keyword);

        if (customerId != null) {
            condition.put("customerId", customerId);
        }

        if (projectId != null) {
            condition.put("projectId", projectId);
        }

        List<DeviceMst> devices = deviceService.getDevices(condition);

        for (DeviceMst device : devices) {
            DeviceMstResponse dr = new DeviceMstResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceMstResponse>>(deviceRes, HttpStatus.OK);
    }

    /**
     * Lấy thông tin thiết bị theo id
     *
     * @param deviceId Mã thiêts bị
     * @return Đối tượng thiết bị
     */
    @GetMapping ("/{deviceId}")
    public ResponseEntity<DeviceMstResponse> getDeviceByDeviceId(@PathVariable ("deviceId") final String deviceId) {

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("deviceId", deviceId);

        DeviceMst device = deviceService.getDeviceById(condition);
        DeviceMstResponse deviceRes = new DeviceMstResponse(device);
        return new ResponseEntity<DeviceMstResponse>(deviceRes, HttpStatus.OK);
    }

    /**
     * Lấy id của thiết bị
     *
     * @return Đối tượng thiết bị
     */
    @GetMapping ("/getDeviceId")
    public ResponseEntity<DeviceResponse> getDeviceId() {

        Map<String, String> conditionCheck = new HashMap<String, String>();
        conditionCheck.put("deviceCode", null);
        Device deviceCheck = deviceService.getDeviceByDeviceCode(conditionCheck);

        DeviceResponse deviceRes = new DeviceResponse(deviceCheck);

        return new ResponseEntity<DeviceResponse>(deviceRes, HttpStatus.OK);
    }

    /**
     * Lấy device code của thiết bị
     *
     * @return Đối tượng thiết bị
     */
    @GetMapping ("/getDeviceCode")
    public ResponseEntity<DeviceMstResponse> getDeviceCode() {
        Map<String, String> conditionCheck = new HashMap<String, String>();
        conditionCheck.put("deviceCode", null);
        DeviceMst deviceCheck = deviceService.checkDeviceByDeviceCode(conditionCheck);

        DeviceMstResponse deviceRes = new DeviceMstResponse(deviceCheck);

        return new ResponseEntity<DeviceMstResponse>(deviceRes, HttpStatus.OK);
    }

    /**
     * Thêm mới thiết bị
     *
     * @param device Đối tượng thiết bị
     * @return Trạng thái thêm mới(200: Thành công, 400: Các lỗi thêm mới)
     */
    @PostMapping ("/addDevice")
    public ResponseEntity<?> addDeviceMst(@Valid @RequestBody final DeviceMstForm device) {
        List<String> errors = new ArrayList<>();
        Map<String, String> conditionCheck = new HashMap<String, String>();
        conditionCheck.put("deviceCode", device.getDeviceCode());
        DeviceMst deviceCheck = deviceService.checkDeviceByDeviceCode(conditionCheck);

        if (deviceCheck != null) {
            errors.add(Constants.DeviceValidation.DEVICE_CODE_EXIST);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            deviceService.addDeviceMst(device);
            callToReceiver(device.getDeviceCode());
            Map<String, Object> condition = new HashMap<String, Object>();
            if (device.getDeviceTypeId() == 1) {
                condition.put("device_id", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                deviceService.insertSettingMeter(condition);
            } else if (device.getDeviceTypeId() == 2) {
                condition.put("deviceId", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                deviceService.insertSettingInverter(condition);
            } else if (device.getDeviceTypeId() == 3) {
                condition.put("device_id", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                deviceService.insertSettingCbnd(condition);
            } else if (device.getDeviceTypeId() == 4) {
                condition.put("deviceId", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                deviceService.insertSettingCbtt(condition);
            } else if (device.getDeviceTypeId() == 5 || device.getDeviceTypeId() == 6) {
                condition.put("deviceId", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                condition.put("deviceTypeId", device.getDeviceTypeId());
                deviceService.insertSettingCbpd(condition);
            } else if (device.getDeviceTypeId() == 7) {
                condition.put("deviceId", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                condition.put("deviceTypeId", device.getDeviceTypeId());
                deviceService.insertSettingCbax(condition);
            } else if (device.getDeviceTypeId() == 10) {
                condition.put("deviceId", device.getId());
                condition.put("projectId", device.getProjectId());
                condition.put("customerId", device.getCustomerId());
                condition.put("deviceTypeId", device.getDeviceTypeId());
                deviceService.insertSettingCbll(condition);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Chỉnh sửa thiết bị
     *
     * @param device Đối tượng thiết bị
     * @param deviceId Mã thiết bị
     * @return Trạng thái cập nhật(200: Thành công, 400: Các lỗi cập nhật)
     */

    @PutMapping ("/updateDevice/{deviceId}")
    public ResponseEntity<?> editDevice(@Valid @RequestBody final DeviceMstForm device,
        @PathVariable ("deviceId") final String deviceId) {

        List<String> errors = new ArrayList<>();

        Map<String, String> conditionCheck = new HashMap<String, String>();
        conditionCheck.put("deviceCode", device.getDeviceCode());
        DeviceMst deviceCheck = deviceService.checkDeviceByDeviceCode(conditionCheck);

        if (deviceCheck != null && !deviceCheck.getDeviceCode()
            .equals(deviceCheck.getDeviceCode())) {
            errors.add(Constants.DeviceValidation.DEVICE_CODE_EXIST);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("deviceId", deviceId);
        condition.put("deviceName", device.getDeviceName());
        condition.put("address", device.getAddress());
        condition.put("latitude", device.getLatitude());
        condition.put("longitude", device.getLongitude());
        condition.put("priority_flag", device.getPriority_flag());
        condition.put("location", device.getLocation());
        condition.put("load_type_id", device.getLoad_type_id());
        condition.put("manufacturer", device.getManufacturer());
        condition.put("model", device.getModel());
        condition.put("pn", device.getPn());
        condition.put("in", device.getIn());
        condition.put("vsc", device.getVsc());
        condition.put("vpr", device.getVpr());
        condition.put("f", device.getF());
        condition.put("delta_p0", device.getDelta_p0());
        condition.put("delta_pk", device.getDelta_pk());
        condition.put("i0", device.getI0());
        condition.put("un", device.getUn());
        condition.put("m_oil", device.getM_oil());
        condition.put("m_all", device.getM_all());
        condition.put("exp_oil", device.getExp_oil());
        condition.put("exp_wind", device.getExp_wind());
        condition.put("hot_spot_factor", device.getHot_spot_factor());
        condition.put("loss_ratio", device.getLoss_ratio());
        condition.put("const_k11", device.getConst_k11());
        condition.put("const_k21", device.getConst_k21());
        condition.put("const_k22", device.getConst_k22());
        condition.put("hot_spot_temp", device.getHot_spot_temp());
        condition.put("hot_spot_gradient", device.getHot_spot_gradient());
        condition.put("avg_oil_temp_rise", device.getAvg_oil_temp_rise());
        condition.put("top_oil_temp_rise", device.getTop_oil_temp_rise());
        condition.put("bottom_oil_temp_rise", device.getBottom_oil_temp_rise());
        condition.put("const_time_oil", device.getConst_time_oil());
        condition.put("const_time_winding", device.getConst_time_winding());
        condition.put("vn", device.getVn());
        condition.put("cable_length", device.getCable_length());
        condition.put("rho", device.getRho());
        condition.put("inc", device.getInc());
        condition.put("pdc_max", device.getPdc_max());
        condition.put("vdc_max", device.getVdc_max());
        condition.put("vdc_rate", device.getVdc_rate());
        condition.put("vac_rate", device.getVac_rate());
        condition.put("idc_max", device.getIdc_max());
        condition.put("iac_rate", device.getIac_rate());
        condition.put("iac_max", device.getIac_max());
        condition.put("pac", device.getPac());
        condition.put("eff", device.getEff());
        condition.put("p_max", device.getP_max());
        condition.put("vmp", device.getVmp());
        condition.put("imp", device.getImp());
        condition.put("voc", device.getVoc());
        condition.put("isc", device.getIsc());
        condition.put("gstc", device.getGstc());
        condition.put("tstc", device.getTstc());
        condition.put("gnoct", device.getGnoct());
        condition.put("tnoct", device.getTnoct());
        condition.put("cp_max", device.getCp_max());
        condition.put("cvoc", device.getCvoc());
        condition.put("cisc", device.getCisc());
        condition.put("ns", device.getNs());
        condition.put("sensor_radiation_id", device.getSensor_radiation_id());
        condition.put("sensor_temperature_id", device.getSensor_temperature_id());
        condition.put("sim_no", device.getSim_no());
        condition.put("battery_capacity", device.getBattery_capacity());
        condition.put("work_date", device.getWork_date());
        condition.put("reference_device_id", device.getReference_device_id());
        condition.put("uid", device.getUid());
        condition.put("db_id", device.getDb_id());
        condition.put("description", device.getDescription());
        condition.put("fuelTypeId", device.getFuelTypeId());
        condition.put("fuelFormId", device.getFuelFormId());
        condition.put("pdm", device.getPdm());
        condition.put("object_id", device.getObjectId());
        deviceService.updateDeviceMst(condition);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Xóa thiết bị
     *
     * @param deviceId Mã thiết bị
     * @return Trạng thái xóa thiết bị(200: Thành công)
     */
    @DeleteMapping ("/delete/{deviceId}")
    public ResponseEntity<Void> deleteSuperManager(@PathVariable ("deviceId") final Long deviceId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("deviceId", deviceId);
        deviceService.deleteDevice(condition);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị theo CustomerId
     *
     * @param customerId Mã Khách hàng
     * @return Danh sách thiết bị
     */

    @GetMapping ("/listDevice/{customerId}")
    public ResponseEntity<?> getListDeviceByCustomerId(@PathVariable ("customerId") final Integer customerId) {

        List<ObjectType> respone = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        condition.put("customerId", String.valueOf(customerId));
        List<ObjectType> objectOneLevel = objectService.getListObjectOneLevelByCustomerId(condition);
        List<ObjectType> objectTwoLevel = objectService.getListObjectTwoLevelByCustomerId(condition);

        // handle object one level
        for (ObjectType object : objectOneLevel) {
            ObjectType objType = new ObjectType();
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeId", String.valueOf(object.getObjectTypeId()));
            con.put("customerId", String.valueOf(customerId));
            List<Device> list = deviceService.getDeviceByObjectTypeId(con);
            objType.setObjectTypeId(object.getObjectTypeId());
            if (list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {
                    Map<String, String> condi = new HashMap<>();
                    condi.put("deviceId", String.valueOf(list.get(i)
                        .getDeviceId()));
                    condi.put("fromDate", fromDate);
                    condi.put("toDate", toDate);
                    condi.put("schema", schema);
                    Device device = deviceService.getDataInstance(condi);
                    if (device != null) {
                        List<Warning> warning = warningService.getWarningInstance(condi);
                        if (warning.size() > 0) {
                            objType.setStatus("warning");
                        } else {
                            objType.setStatus("active");
                        }
                    } else {
                        if (objType.getStatus() == null || objType.getStatus() == "offline") {
                            objType.setStatus("offline");
                        }

                    }
                }

                ObjectType obj = objectService.getObjectTypeById(con);

                objType.setCountDevice(list.size());
                objType.setObjectTypeName(obj.getObjectTypeName());
                objType.setSystemTypeId(obj.getSystemTypeId());
                objType.setTypeClass(obj.getTypeClass());
                objType.setDeviceTypeId(list.get(0)
                    .getDeviceType());
                objType.setImg(obj.getImg());
                respone.add(objType);

            }
        }

        // handle object two level
        for (ObjectType object : objectTwoLevel) {
            Map<String, String> co = new HashMap<>();
            co.put("objectTypeId", String.valueOf(object.getObjectTypeId()));
            co.put("customerId", String.valueOf(customerId));
            List<ObjectName> list = objectService.getObjectsByObjectTypeId(co);
            for (ObjectName objName : list) {
                co.put("objectName", objName.getObjectName());
                List<Device> listDevice = deviceService.getDeviceByObjectName(co);
                if (listDevice.size() > 0) {
                    for (int i = 0; i < listDevice.size(); i++) {
                        Map<String, String> condi = new HashMap<>();
                        condi.put("deviceId", String.valueOf(listDevice.get(i)
                            .getDeviceId()));
                        condi.put("fromDate", fromDate);
                        condi.put("toDate", toDate);
                        condi.put("schema", schema);
                        Device device = deviceService.getDataInstance(condi);
                        if (device != null) {
                            List<Warning> warning = warningService.getWarningInstance(condi);
                            if (warning.size() > 0) {
                                object.setStatus("warning");
                            } else {
                                object.setStatus("active");
                            }
                        } else {
                            if (object.getStatus() == null || object.getStatus() == "offline") {
                                object.setStatus("offline");
                            }

                        }
                    }
                    ObjectType obj = objectService.getObjectTypeById(co);
                    object.setCountDevice(list.size());
                    object.setObjectTypeName(obj.getObjectTypeName());
                    object.setSystemTypeId(obj.getSystemTypeId());
                    object.setTypeClass(obj.getTypeClass());
                    object.setDeviceTypeId(listDevice.get(0)
                        .getDeviceType());
                    object.setImg(obj.getImg());
                }
            }
            respone.add(object);
        }
        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị theo projectId
     *
     * @param projectId Mã dự án
     * @return Danh sách thiết bị
     */

    @GetMapping ("/listDeviceByProject/{customerId}/{projectId}")
    public ResponseEntity<?> getListDeviceByprojectId(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId) {

        List<ObjectType> respone = new ArrayList<>();

        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        List<ObjectType> objectOneLevel = objectService.getListObjectOneLevelByProjectId(condition);
        List<ObjectType> objectTwoLevel = objectService.getListObjectTwoLevelByProjectId(condition);
        // handle object one level
        for (ObjectType object : objectOneLevel) {
            ObjectType objType = new ObjectType();
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeId", String.valueOf(object.getObjectTypeId()));
            con.put("projectId", String.valueOf(projectId));
            List<Device> list = deviceService.getDeviceByObjectTypeId(con);
            objType.setObjectTypeId(object.getObjectTypeId());
            if (list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {
                    Map<String, String> condi = new HashMap<>();
                    condi.put("deviceId", String.valueOf(list.get(i)
                        .getDeviceId()));
                    condi.put("fromDate", fromDate);
                    condi.put("toDate", toDate);
                    condi.put("schema", schema);
                    Device device = deviceService.getDataInstance(condi);
                    if (device != null) {
                        List<Warning> warning = warningService.getWarningInstance(condi);
                        if (warning.size() > 0) {
                            objType.setStatus("warning");
                        } else {
                            objType.setStatus("active");
                        }
                    } else {
                        if (objType.getStatus() == null || objType.getStatus() == "offline") {
                            objType.setStatus("offline");
                        }

                    }
                }

                ObjectType obj = objectService.getObjectTypeById(con);

                objType.setCountDevice(list.size());
                objType.setObjectTypeName(obj.getObjectTypeName());
                objType.setSystemTypeId(obj.getSystemTypeId());
                objType.setTypeClass(obj.getTypeClass());
                objType.setDeviceTypeId(list.get(0)
                    .getDeviceType());
                objType.setImg(obj.getImg());
                respone.add(objType);

            }

        }

        // handle object two level
        for (ObjectType object : objectTwoLevel) {
            Map<String, String> co = new HashMap<>();
            co.put("objectTypeId", String.valueOf(object.getObjectTypeId()));
            co.put("customerId", String.valueOf(customerId));
            co.put("projectId", String.valueOf(projectId));
            List<ObjectName> list = objectService.getObjectsByObjectTypeId(co);
            for (ObjectName objName : list) {
                co.put("objectName", objName.getObjectName());
                List<Device> listDevice = deviceService.getDeviceByObjectName(co);
                if (listDevice.size() > 0) {
                    for (int i = 0; i < listDevice.size(); i++) {
                        Map<String, String> condi = new HashMap<>();
                        condi.put("deviceId", String.valueOf(listDevice.get(i)
                            .getDeviceId()));
                        condi.put("fromDate", fromDate);
                        condi.put("toDate", toDate);
                        condi.put("schema", schema);
                        Device device = deviceService.getDataInstance(condi);
                        if (device != null) {
                            List<Warning> warning = warningService.getWarningInstance(condi);
                            if (warning.size() > 0) {
                                object.setStatus("warning");
                            } else {
                                object.setStatus("active");
                            }
                        } else {
                            if (object.getStatus() == null || object.getStatus() == "offline") {
                                object.setStatus("offline");
                            }

                        }
                    }
                    ObjectType obj = objectService.getObjectTypeById(co);
                    object.setCountDevice(list.size());
                    object.setObjectTypeName(obj.getObjectTypeName());
                    object.setSystemTypeId(obj.getSystemTypeId());
                    object.setTypeClass(obj.getTypeClass());
                    object.setDeviceTypeId(listDevice.get(0)
                        .getDeviceType());
                    object.setImg(obj.getImg());
                }
            }
            respone.add(object);
        }
        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị theo customerId và systemTypeId và deviceTypeId
     *
     * @param customerId Mã khách hàng
     * @param systemType Mã thiết bị
     * @param deviceType Kiểu thiết bị
     * @return Danh sách thiết bị //
     */
    @GetMapping ("listDeviceOneLevelByCusSys/{customerId}/{objectTypeId}")
    public ResponseEntity<?> getListDeviceByCusSys(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("objectTypeId") final Integer objectTypeId) {
        List<Device> respone = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        condition.put("customerId", String.valueOf(customerId));
        condition.put("objectTypeId", String.valueOf(objectTypeId));
        List<Device> list = deviceService.getDeviceByObjectTypeId(condition);
        // List<ObjectType> objects = deviceService.getListObjectByCusSys(condition);

        for (Device device : list) {
            Device deviceRs = new Device();
            Integer systemType = device.getSystemTypeId();
            Long deviceId = device.getDeviceId();
            Integer deviceTypeId = device.getDeviceType();
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeId", String.valueOf(objectTypeId));
            con.put("schema", schema);
            con.put("deviceId", String.valueOf(deviceId));

            if (systemType == 2) {
                if (deviceTypeId == 1) {
                    deviceRs = deviceService.getDataDeviceInverterByObjectType(con);
                }
                if (deviceTypeId == 3) {
                    deviceRs = deviceService.getDataDeviceCombinerByObjectType(con);
                }
                if (deviceTypeId == 4) {
                    deviceRs = deviceService.getDataDeviceStringByObjectType(con);
                }
            }
            if (systemType == 1) {
                deviceRs = deviceService.getDataDeviceLoadByObjectType(con);
            }

            if (deviceRs == null) {
                deviceRs = deviceService.getDataDevice(con);
                deviceRs.setOperatingStatus("offline");
            } else {
                Map<String, String> condi = new HashMap<>();
                condi.put("deviceId", String.valueOf(device.getDeviceId()));
                condi.put("fromDate", fromDate);
                condi.put("toDate", toDate);
                condi.put("schema", schema);
                Device instance = deviceService.getDataInstance(condi);

                if (instance != null) {
                    List<Warning> warning = warningService.getWarningInstance(condi);
                    if (warning.size() > 0) {
                        deviceRs.setOperatingStatus("warning");
                    } else {
                        deviceRs.setOperatingStatus("active");
                    }
                } else {
                    if (deviceRs.getOperatingStatus() == null) {
                        deviceRs.setOperatingStatus("offline");
                    }
                    if (device.getOperatingStatus() == "offline") {
                        deviceRs.setOperatingStatus("offline");
                    }
                }
            }

            respone.add(deviceRs);
        }

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị theo projectId và systemTypeId
     *
     * @param projectId Mã dự án
     * @param systemType Mã thiết bị
     * @return Danh sách thiết bị
     */
    @GetMapping ("listDeviceOneLevelByProSys/{customerId}/{projectId}/{objectTypeId}")
    public ResponseEntity<?> getListDeviceByProSys(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId,
        @PathVariable ("objectTypeId") final Integer objectTypeId) {

        List<Device> respone = new ArrayList<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", String.valueOf(projectId));
        condition.put("objectTypeId", String.valueOf(objectTypeId));
        List<Device> list = deviceService.getDeviceByObjectTypeId(condition);

        for (Device device : list) {
            Device deviceRs = new Device();
            Integer systemType = device.getSystemTypeId();
            Long deviceId = device.getDeviceId();
            Integer deviceTypeId = device.getDeviceType();
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeId", String.valueOf(objectTypeId));
            con.put("schema", schema);
            con.put("deviceId", String.valueOf(deviceId));

            if (systemType == 2) {
                if (deviceTypeId == 1) {
                    deviceRs = deviceService.getDataDeviceInverterByObjectType(con);
                }
                if (deviceTypeId == 3) {
                    deviceRs = deviceService.getDataDeviceCombinerByObjectType(con);
                }
                if (deviceTypeId == 4) {
                    deviceRs = deviceService.getDataDeviceStringByObjectType(con);
                }
            }

            if (systemType == 1) {
                deviceRs = deviceService.getDataDeviceLoadByObjectType(con);
            }

            if (deviceRs == null) {
                deviceRs = deviceService.getDataDevice(con);
                deviceRs.setOperatingStatus("offline");
            } else {

                Map<String, String> condi = new HashMap<>();
                condi.put("deviceId", String.valueOf(device.getDeviceId()));
                condi.put("fromDate", fromDate);
                condi.put("toDate", toDate);
                condi.put("schema", schema);
                Device instance = deviceService.getDataInstance(condi);

                if (instance != null) {
                    List<Warning> warning = warningService.getWarningInstance(condi);
                    if (warning.size() > 0) {
                        deviceRs.setOperatingStatus("warning");
                    } else {
                        deviceRs.setOperatingStatus("active");
                    }
                } else {
                    if (deviceRs.getOperatingStatus() == null || device.getOperatingStatus() == "offline") {
                        deviceRs.setOperatingStatus("offline");
                    }

                }
            }

            respone.add(deviceRs);
        }

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy thông tin thiết bị và cảnh báo tức thời theo deviceId
     *
     * @param deviceId id thiết bị
     * @return Object chứa thông tin thiết bị và cảnh báo
     */
    @GetMapping ("info/{customerId}/{deviceId}")
    public ResponseEntity<?> getInfoDeviceAndWarning(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("deviceId") final Integer deviceId) {

        List<Object> result = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 25;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatWithDay = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        condition.put("schema", schema);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("customerId", String.valueOf(customerId));
        condition.put("deviceId", String.valueOf(deviceId));

        Device infoDevice = deviceService.getInfoDevice(condition);

        if (infoDevice != null) {
            List<Warning> warning = warningService.getWarningInstance(condition);

            if (warning != null) {
                result.add(warning);
            } else {
                result.add(new Warning());
            }

            String startDevice = dateFormatWithDay.format(infoDevice.getCreateDate());
            String nowDevice = dateFormatWithDay.format(new Date());
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            Date startDate = new Date();
            Date nowDate = new Date();

            try {
                startDate = dateFormatWithDay.parse(startDevice);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                nowDate = dateFormatWithDay.parse(nowDevice);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            c1.setTime(startDate);
            c2.setTime(nowDate);

            long noDay = (c2.getTime()
                .getTime()
                - c1.getTime()
                    .getTime())
                / (24 * 3600 * 1000);
            infoDevice.setDayOnline(noDay);

            result.add(infoDevice);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Lấy dữ liệu thiết bị tức thời theo deviceId
     *
     * @param deviceId id thiết bị
     * @return Object chứa thông tin thiết bị và cảnh báo
     */
    @GetMapping ("instance/{customerId}/{systemType}/{deviceTypeId}/{deviceId}")
    public ResponseEntity<Device> getDataInstaceByDeviceId(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("systemType") final Integer systemType,
        @PathVariable ("deviceTypeId") final Integer deviceTypeId, @PathVariable ("deviceId") final Integer deviceId) {

        Device respone = new Device();

        Map<String, String> condition = new HashMap<>();
        Map<String, Object> con = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -30);
        Date fromDate = calendar.getTime();
        String fromDateStr = DateUtils.toString(fromDate, "yyyy-MM-dd HH:mm:ss");

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthFormatWithTime = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");
        // String fromDate = dateFormatWithTime.format(currentTime.getTime());
        String curYear = yearFormatWithTime.format(new Date());
        String curMonth = monthFormatWithTime.format(new Date());
        String curDay = dayFormatWithTime.format(new Date());

        condition.put("schema", schema);
        condition.put("fromDate", fromDateStr);
        condition.put("toDate", toDate);
        condition.put("deviceId", String.valueOf(deviceId));
        condition.put("year", curYear);
        condition.put("day", curDay);
        condition.put("month", curMonth);

        if (deviceTypeId == 1) {
            respone = deviceService.getDataInstanceDeviceLoad(condition);
            Float epDay = deviceService.getEnergyDeviceLoadInDay(condition);
            Float epMonth = deviceService.getEnergyDeviceLoadInMonth(condition);
            if (respone == null) {
                respone = deviceService.getDataInstanceLoad(condition);
                if (respone == null) {
                    condition.put("fromDate", null);
                    respone = deviceService.getDataInstanceDeviceLoad(condition);
                    if (respone != null) {
                        respone.setOperatingStatus("offline");
                        String day = dayFormatWithTime.format(respone.getSendDate());
                        String month = monthFormatWithTime.format(respone.getSendDate());
                        condition.put("day", day);
                        condition.put("month", month);
                        epDay = deviceService.getEnergyDeviceLoadInDay(condition);
                        epMonth = deviceService.getEnergyDeviceLoadInMonth(condition);
                    }
                }
            }

            if (epDay != null && epMonth != null) {
                respone.setEpDay(epDay);
                respone.setEpMonth(epMonth);
            }

        }
        if (deviceTypeId == 3) {
            respone = deviceService.getDataInstanceDeviceSensor(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceDeviceSensor(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        if (deviceTypeId == 4) {
            respone = deviceService.getDataInstanceDeviceSensorStatus(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceDeviceSensor(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }

        if (deviceTypeId == 2) {
            respone = deviceService.getDataInstanceInverter(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceInverter(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        if (deviceTypeId == 17) {
            respone = deviceService.getDataInstanceDeviceCombiner(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceDeviceCombiner(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        if (deviceTypeId == 16) {
            respone = deviceService.getDataInstanceDeviceString(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceDeviceString(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        if (deviceTypeId == 9) {
            con.put("schema", schema);
            con.put("deviceId", deviceId);
            respone = deviceService.getDataInstanceGateway(con);

            if (respone == null) {
                respone = deviceService.getDataInstanceInverter(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }

        if (deviceTypeId == 5) {
            respone = deviceService.getDataInstanceHTR02(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceHTR02(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }

        if (deviceTypeId == 6) {
            respone = deviceService.getDataInstanceAMS01(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceAMS01(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        
        if (deviceTypeId == 7) {
            respone = deviceService.getDataInstancePressure(condition);
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstancePressure(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }
        
        if (deviceTypeId == 10) {
        	condition.put("viewType", "3");
            respone = deviceService.getDataInstanceFlowAccumulation(condition);
            if(respone != null) {         	           
	            Float tAccumulationDay = respone.getT();
	
	            condition.put("viewType", "2");
	            respone = deviceService.getDataInstanceFlowAccumulation(condition);
	            Float tAccumulationMonth = respone.getT();
	            
	            respone = deviceService.getDataInstanceFlow(condition);
	            respone.setTAccumulationDay(tAccumulationDay); 
	            respone.setTAccumulationMonth(tAccumulationMonth); 
            }
            if (respone == null) {
                condition.put("fromDate", null);
                respone = deviceService.getDataInstanceFlow(condition);
                if (respone != null) {
                    respone.setOperatingStatus("offline");
                }
            }
        }

        // if (systemType == 5) {
        // respone = deviceService.getDataInstanceDeviceRMU(condition);
        // if (respone == null) {
        // condition.put("fromDate", null);
        // respone = deviceService.getDataInstanceDeviceRMU(condition);
        // if (respone != null) {
        // respone.setOperatingStatus("offline");
        // }
        // }
        // }

        if (respone != null) {
            String status = "";
            if (respone.getOperatingStatus() != null) {
                status = respone.getOperatingStatus();
            }
            if (status.equals("offline")) {
            } else {
                List<Warning> warning = warningService.getWarningInstance(condition);
                if (warning != null) {
                    respone.setOperatingStatus("warning");
                } else {
                    respone.setOperatingStatus("active");
                }
            }
        } else {
            con.put("deviceId", deviceId);
            respone = deviceService.getDeviceByDeviceId(con);
            respone.setOperatingStatus("offline");
        }

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách dữ liệu tức thời theo deviceId
     *
     * @param deviceId id thiết bị
     * @return Danh sách thiết bị chứa
     * @throws ParseException
     */
    @GetMapping ("listDataInstance/{customerId}/{deviceId}/{optionTime}")
    public ResponseEntity<?> getListDataInstance(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("deviceId") final Integer deviceId,
        @RequestParam (value = "fromDate", required = false) final String fDate,
        @RequestParam (value = "toDate", required = false) final String tDate,
        @PathVariable ("optionTime") final Integer optionTime) throws ParseException {

        List<Device> respone = new ArrayList<>();
        List<Device> responeHumidity = new ArrayList<>();
        List<Device> responeRatioIndicator = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        String fromDate = null;
        String toDate = null;
        String conditionMinute = "15";
        if (optionTime == 0) {
            conditionMinute = "5";
        }
        if (optionTime == 1) {
            conditionMinute = "15";
        }
        if (optionTime == 2) {
            conditionMinute = "30";
        }
        if (optionTime == 3) {
            conditionMinute = "60";
            condition.put("viewTime", "4");
        }
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");
        String curYear = yearFormatWithTime.format(new Date());

        if (fDate != null && fDate != "") {
            fromDate = fDate;
            Date year = yearFormatWithTime.parse(fromDate);
            DateFormat yearfomat = new SimpleDateFormat("yyyy");
            curYear = yearfomat.format(year);
        } else {
            fromDate = dateFormatWithTime.format(new Date());
        }

        if (tDate != null && tDate != "") {
            Date startDate = dateFormatWithTime.parse(fromDate);
            Date endDate = dateFormatWithTime.parse(tDate);
            Date year = yearFormatWithTime.parse(fromDate);

            DateFormat yearfomat = new SimpleDateFormat("yyyy");

            fromDate = fDate;
            toDate = tDate;
            curYear = yearfomat.format(year);
        }

        condition.put("schema", schema);
        condition.put("customerId", String.valueOf(customerId));
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceId", String.valueOf(deviceId));
        condition.put("year", curYear);
        condition.put("minute", conditionMinute);

        Device infoDevice = deviceService.getInfoDevice(condition);

        if (infoDevice.getSystemTypeId() == 1) {
            respone = deviceService.getListDataInstanceLoad(condition);
            List<Device> data = deviceService.getDataEpLoad(condition);
            for (int i = 0; i < respone.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j)
                        .getSendDate()
                        .equals(respone.get(i)
                            .getSendDate())) {
                        respone.get(i)
                            .setEp(data.get(j)
                                .getEp());
                    }
                }
            }
        }
        
        if (infoDevice.getSystemTypeId() == 2) {
            if (infoDevice.getDeviceType() == 2) {
                respone = deviceService.getListDataInstanceInverter(condition);
                List<Device> data = deviceService.getDataEpInverter(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
            
            if (infoDevice.getDeviceType() == 3) {
                respone = deviceService.getListDataInstanceCombiner(condition);
                List<Device> data = deviceService.getDataEpCombiner(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
            
            if (infoDevice.getDeviceType() == 4) {
                respone = deviceService.getListDataInstanceString(condition);
                List<Device> data = deviceService.getDataEpString(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
        }

        responeHumidity = deviceService.getListDataInstanceLoadHumidity(condition);
        if (infoDevice.getDeviceType() == 5 || infoDevice.getDeviceType() == 6) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstanceRatioIndicator(condition);
        }

        if (infoDevice.getDeviceType() == 9) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstanceGateway(condition);
        }
        
        if (infoDevice.getDeviceType() == 7) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstancePressure(condition);
        }
        
        if (infoDevice.getDeviceType() == 10) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstanceFlow(condition);
        }

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("dataInstance", respone);
        mapData.put("dataHumidity", responeHumidity);
        // mapData.put("responeRatioIndicator", responeRatioIndicator);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Lấy danh sách đối tượng Grid theo customerId và systemTypeId
     *
     * @param customerId Mã khách hàng
     * @param systemType Mã thiết bị
     * @return Danh sách thiết bị //
     */
    @GetMapping ("listDeviceTwoLevelByCusSys/{customerId}/{objectTypeId}")
    public ResponseEntity<?> getListObjGridByCusSys(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("objectTypeId") final Integer objectTypeId) {
        List<Object> respone = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        condition.put("customerId", String.valueOf(customerId));
        condition.put("objectTypeId", String.valueOf(objectTypeId));
        List<ObjectType> objects = objectService.getListObjectTwoLevelByCusSys(condition);

        for (ObjectType object : objects) {
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeName", object.getObjectTypeName());
            con.put("schema", schema);

            List<Device> list = deviceService.getDataDeviceByObjectTwoLevel(con);

            for (Device device : list) {
                Map<String, String> condi = new HashMap<>();
                condi.put("deviceId", String.valueOf(device.getDeviceId()));
                condi.put("fromDate", fromDate);
                condi.put("toDate", toDate);
                condi.put("schema", schema);
                Device instance = deviceService.getDataInstance(condi);

                if (instance != null) {
                    List<Warning> warning = warningService.getWarningInstance(condi);
                    if (warning.size() > 0) {
                        object.setStatus("warning");
                    } else {
                        object.setStatus("active");
                    }
                } else {
                    if (object.getStatus() == null || object.getStatus() == "offline") {
                        object.setStatus("offline");
                    } else {
                    }

                }
            }
            object.setDeviceTypeId(1);
        }

        respone.addAll(objects);

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách đối tượng Grid theo customerId và systemTypeId
     *
     * @param customerId Mã khách hàng
     * @param projectId Mã dự án
     * @param systemType Mã thiết bị
     * @return Danh sách thiết bị //
     */
    @GetMapping ("listDeviceTwoLevelByProSys/{customerId}/{projectId}/{objectTypeId}")
    public ResponseEntity<?> getListObjGridByProSys(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("projectId") final Integer projectId,
        @PathVariable ("objectTypeId") final Integer objectTypeId) {
        List<Object> respone = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        condition.put("objectTypeId", String.valueOf(objectTypeId));
        condition.put("projectId", String.valueOf(projectId));
        List<ObjectType> objects = objectService.getListObjectTwoLevelByProSys(condition);

        for (ObjectType object : objects) {
            Map<String, String> con = new HashMap<>();
            con.put("objectTypeName", object.getObjectTypeName());
            con.put("schema", schema);
            con.put("projectId", String.valueOf(projectId));

            List<Device> list = deviceService.getDataDeviceByObjectTwoLevel(con);

            for (Device device : list) {
                Map<String, String> condi = new HashMap<>();
                condi.put("deviceId", String.valueOf(device.getDeviceId()));
                condi.put("fromDate", fromDate);
                condi.put("toDate", toDate);
                condi.put("schema", schema);
                Device instance = deviceService.getDataInstance(condi);

                if (instance != null) {
                    List<Warning> warning = warningService.getWarningInstance(condi);
                    if (warning.size() > 0) {
                        object.setStatus("warning");
                    } else {
                        object.setStatus("active");
                    }
                } else {
                    if (object.getStatus() == null || object.getStatus() == "offline") {
                        object.setStatus("offline");
                    } else {
                    }

                }
            }
            object.setCountDevice(list.size());
        }

        respone.addAll(objects);

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị Grid theo objectTypeId
     *
     * @param objectTypeId Mã đối tượng
     * @return Danh sách thiết bị //
     */
    @GetMapping ("listDeviceByObject/{customerId}/{objectTypeId}/{systemTypeId}")
    public ResponseEntity<?> getListDeviceByObjTypeId(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("objectTypeId") final Integer objectTypeId,
        @PathVariable ("systemTypeId") final Integer systemTypeId,
        @RequestParam ("objectTypeName") final String objectTypeName,
        @RequestParam (value = "projectId", required = false) final String projectId) {

        List<Device> respone = new ArrayList<>();

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        if (objectTypeName != "") {
            Map<String, String> condition = new HashMap<>();
            String schema = Schema.getSchemas(customerId);
            condition.put("schema", schema);
            condition.put("objectTypeId", String.valueOf(objectTypeId));
            condition.put("objectTypeName", objectTypeName);
            condition.put("customerId", String.valueOf(customerId));
            if (projectId != null && projectId != "") {
                condition.put("projectId", projectId);
            }
            if (systemTypeId == 5) {
                respone = deviceService.getDataDeviceRMUTwoLevelByObjectName(condition);
            }
            if (systemTypeId == 1) {
                respone = deviceService.getDataDeviceMeterTwoLevelByObjectName(condition);
            }

            for (Device device : respone) {
                Map<String, String> condi = new HashMap<>();
                condi.put("deviceId", String.valueOf(device.getDeviceId()));
                condi.put("fromDate", fromDate);
                condi.put("toDate", toDate);
                condi.put("schema", schema);

                Device instance = new Device();
                if (systemTypeId == 5) {
                    instance = deviceService.getDataInstanceRMU(condi);
                    if (instance != null) {
                        device.setT(instance.getT());
                        device.setH(instance.getH());
                        device.setUab(instance.getUab());
                        device.setIa(instance.getIa());
                        device.setIndicator(instance.getIndicator());
                    }
                }
                if (systemTypeId == 1) {
                    instance = deviceService.getDataInstanceLoad(condi);
                    if (instance != null) {
                        device.setPTotal(instance.getPTotal());
                        device.setQTotal(instance.getQTotal());
                        device.setUab(instance.getUab());
                        device.setIa(instance.getIa());
                        device.setPfa(instance.getPfa());
                    }
                }

                if (instance != null) {
                    List<Warning> warning = warningService.getWarningInstance(condi);
                    if (warning.size() > 0) {
                        device.setStatusDevice("warning");
                    } else {
                        device.setStatusDevice("active");
                    }
                } else {
                    if (device.getStatusDevice() == null || device.getStatusDevice() == "offline") {
                        device.setStatusDevice("offline");
                    } else {
                    }

                }
            }
            return new ResponseEntity<>(respone, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("/listCaculateFlag/")
    public ResponseEntity<List<DeviceResponse>> getListDeviceCaculateFlag(
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "systemType", required = false) final String systemType,
        @RequestParam (value = "area", required = false) final String area,
        @RequestParam (value = "objectType", required = false) final String objectType,
        @RequestParam (value = "load", required = false) final String loadType) {

        List<DeviceResponse> deviceRes = new ArrayList<>();

        Map<String, String> condition = new HashMap<String, String>();
        if (projectId != null && projectId != "") {
            condition.put("projectId", projectId);
        }
        if (systemType != null && systemType != "") {
            condition.put("systemType", systemType);
        }
        if (area != null) {
            condition.put("area", area);
        }
        if (objectType != null && objectType != "") {
            condition.put("objectType", objectType);
        }
        if (loadType != null && loadType != "") {
            condition.put("loadType", loadType);
        }
        List<Device> devices = deviceService.getDevicesCalculateFlag(condition);
        for (Device device : devices) {
            DeviceResponse dr = new DeviceResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceResponse>>(deviceRes, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị
     *
     * @return Danh sách thiết bị
     */
    // CHECKSTYLE:OFF
    @GetMapping ("/listAllFlag/")
    public ResponseEntity<List<DeviceResponse>> getListDeviceAllFlag(
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "systemType", required = false) final String systemType,
        @RequestParam (value = "area", required = false) final String area,
        @RequestParam (value = "objectType", required = false) final String objectType,
        @RequestParam (value = "load", required = false) final String loadType,
        @RequestParam (value = "deviceType", required = false) final String deviceType) {

        List<DeviceResponse> deviceRes = new ArrayList<>();

        Map<String, String> condition = new HashMap<String, String>();
        if (projectId != null && projectId != "") {
            condition.put("projectId", projectId);
        }
        if (systemType != null && systemType != "") {
            condition.put("systemType", systemType);
        }
        if (area != null) {
            condition.put("area", area);
        }
        if (objectType != null && objectType != "") {
            condition.put("objectType", objectType);
        }
        if (loadType != null && loadType != "") {
            condition.put("loadType", loadType);
        }
        if (deviceType != null && deviceType != "") {
            condition.put("deviceType", deviceType);
        }
        List<Device> devices = deviceService.getDevicesAllFlag(condition);
        for (Device device : devices) {
            DeviceResponse dr = new DeviceResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceResponse>>(deviceRes, HttpStatus.OK);
    }

    @GetMapping ("/list-by-device-type")
    public ResponseEntity<List<DeviceResponse>> getListDeviceByDeviceType(
        @RequestParam (value = "customer", required = false) final String customer,
        @RequestParam (value = "project", required = false) final String projectId,
        @RequestParam (value = "systemType", required = false) final String systemType,
        @RequestParam (value = "deviceType", required = false) final String deviceType) {

        List<DeviceResponse> deviceRes = new ArrayList<>();
        Map<String, String> condition = new HashMap<>();
        if (customer != null && !customer.equals("")) {
            condition.put("customer", customer);
        }
        if (projectId != null && !projectId.equals("")) {
            condition.put("project", projectId);
        }
        if (systemType != null && !systemType.equals("")) {
            condition.put("systemType", systemType);
        }
        if (deviceType != null && !deviceType.equals("")) {
            condition.put("deviceType", deviceType);
        }
        List<Device> devices = deviceService.getListByDeviceType(condition);
        for (Device device : devices) {
            DeviceResponse dr = new DeviceResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceResponse>>(deviceRes, HttpStatus.OK);
    }

    @GetMapping ("/list-by-ids")
    public ResponseEntity<List<DeviceResponse>> getListDeviceByIds(
        @RequestParam (value = "ids", required = false) final String ids) {
        List<DeviceResponse> deviceRes = new ArrayList<>();
        Map<String, String> condition = new HashMap<>();
        condition.put("listIdDevice", ids);
        List<Device> devices = deviceService.getDeviceListByListId(condition);
        for (Device device : devices) {
            DeviceResponse dr = new DeviceResponse(device);
            deviceRes.add(dr);
        }

        return new ResponseEntity<List<DeviceResponse>>(deviceRes, HttpStatus.OK);
    }

    @GetMapping ("/getObjectType/{objectTypeId}")
    public ResponseEntity<String> getObjectTypeNameById(@PathVariable ("objectTypeId") final Integer objectTypeId) {

        HashMap<String, Object> condition = new HashMap<>();
        condition.put("objectTypeId", objectTypeId);
        String result = deviceService.getObjectNameById(condition);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    @GetMapping ("/getDeviceGateway")
    public ResponseEntity<?> getDeviceGateway() {
        List<DeviceMst> listGateway = deviceService.getDeviceGateway();

        return new ResponseEntity<List<DeviceMst>>(listGateway, HttpStatus.OK);
    }

    @GetMapping ("/getListObjectType")
    public ResponseEntity<?> getListObjectType(@RequestParam (value = "customerId") final String customerId,
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "systemTypeId", required = false) final String systemTypeId,
        @RequestParam (value = "projectIds", required = false) final String projectIds) {
        List<ObjectType> respone = new ArrayList<>();

        String proIds = "";

        if (projectIds == "") {
            proIds = null;
        } else {
            proIds = projectIds;
        }

        String schema = Schema.getSchemas(Integer.valueOf(customerId));

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        Map<String, String> condition = new HashMap<>();

        condition.put("customerId", customerId);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }
        if (systemTypeId != "" && systemTypeId != "0") {
            condition.put("systemTypeId", systemTypeId);
        }
        String[] listDeviceIds = deviceService.getDeviceIdByCustomerId(condition);
        if (listDeviceIds.length > 0) {
            String deviceId = String.join(",", listDeviceIds);
            condition.put("deviceId", deviceId);
            List<Integer> objectIds = deviceService.getObjectByDeviceId(condition);
            List<Integer> objIdsResult = objectIds.stream()
                .distinct()
                .collect(Collectors.toList());
            for (Integer objId : objIdsResult) {
                if (objId != null) {
                    condition.put("objectId", String.valueOf(objId));
                    ObjectType objType = deviceService.getObjectTypeByObjId(condition);
                    if (objType != null) {
                        condition.put("objectTypeId", String.valueOf(objType.getObjectTypeId()));
                        condition.put("projectIds", proIds);
                        List<Integer> listObjIds = deviceService.getListObjByObjectTypeId(condition);

                        List<Integer> listObjIdsResult = listObjIds.stream()
                            .distinct()
                            .collect(Collectors.toList());
                        objType.setCountDevice(listObjIdsResult.size());
                        for (Integer id : listObjIdsResult) {
                            Map<String, String> con = new HashMap<>();
                            con.put("objectTypeId", String.valueOf(id));
                            List<Device> list = deviceService.getDeviceByObjectTypeId(con);
                            for (int i = 0; i < list.size(); i++) {
                                Map<String, String> condi = new HashMap<>();
                                condi.put("deviceId", String.valueOf(list.get(i)
                                    .getDeviceId()));
                                condi.put("fromDate", fromDate);
                                condi.put("toDate", toDate);
                                condi.put("schema", schema);
                                Device device = deviceService.getDataInstance(condi);
                                if (device != null) {
                                    if (objType.getStatus() != null && !objType.getStatus()
                                        .equals("warning")) {
                                        if (device.getStatus() == 1) {
                                            objType.setStatus("warning");
                                        } else {
                                            objType.setStatus("active");
                                        }
                                    }
                                    if (objType.getStatus() == null) {
                                        if (device.getStatus() == 1) {
                                            objType.setStatus("warning");
                                        } else {
                                            objType.setStatus("active");
                                        }
                                    }
                                } else {
                                    if (objType.getStatus() == null || objType.getStatus() == "offline") {
                                        objType.setStatus("offline");
                                    }

                                }
                            }
                        }
                        respone.add(objType);
                    }

                    respone = respone.stream()
                        .collect(Collectors.toMap(obj -> obj.getObjectTypeId(), Function.identity(),
                            (existing, replacement) -> existing))
                        .values()
                        .stream()
                        .collect(Collectors.toList());

                }
            }
        }

        return new ResponseEntity<List<ObjectType>>(respone, HttpStatus.OK);
    }

    @GetMapping ("/getListObject")
    public ResponseEntity<?> getListObject(@RequestParam (value = "customerId") final String customerId,
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "objectTypeId", required = false) final String objectTypeId,
        @RequestParam (value = "systemTypeId", required = false) final String systemTypeId,
        @RequestParam (value = "projectIds", required = false) final String projectIds) {

        List<ObjectType> respone = new ArrayList<>();

        String proIds = "";

        if (projectIds == "") {
            proIds = null;
        } else {
            proIds = projectIds;
        }

        String schema = Schema.getSchemas(Integer.valueOf(customerId));

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());

        Map<String, String> condition = new HashMap<>();

        condition.put("customerId", customerId);
        if (!projectId.equals("0")) {
            condition.put("projectId", projectId);
        } else {
            condition.put("projectId", null);
        }
        if (systemTypeId != "" && systemTypeId != "0") {
            condition.put("systemTypeId", systemTypeId);
        }
        condition.put("objectTypeId", String.valueOf(objectTypeId));
        condition.put("projectIds", proIds);
        List<Integer> listObjIds = deviceService.getListObjByObjectTypeId(condition);

        List<Integer> objIdsResult = listObjIds.stream()
            .distinct()
            .collect(Collectors.toList());

        for (Integer id : objIdsResult) {
            condition.put("objectTypeId", String.valueOf(id));
            ObjectType objType = deviceService.getObjectByObjId(condition);
            if (objType != null) {
                objType.setObjectTypeId(Integer.valueOf(objectTypeId));
                List<Device> list = deviceService.getDeviceByObjectTypeId(condition);
                if (list.size() > 0) {
                    objType.setCountDevice(list.size());
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, String> condi = new HashMap<>();
                        condi.put("deviceId", String.valueOf(list.get(i)
                            .getDeviceId()));
                        condi.put("fromDate", fromDate);
                        condi.put("toDate", toDate);
                        condi.put("schema", schema);
                        Device device = deviceService.getDataInstance(condi);
                        if (device != null) {
                            if (device.getStatus() == 1) {
                                objType.setStatus("warning");
                            } else {
                                if (objType.getStatus() != "warning") {
                                    objType.setStatus("active");
                                }
                            }
                        } else {
                            if (objType.getStatus() == null || objType.getStatus() == "offline") {
                                objType.setStatus("offline");
                            }

                        }
                    }
                }

                respone.add(objType);
            }
        }

        return new ResponseEntity<List<ObjectType>>(respone, HttpStatus.OK);
    }

    @GetMapping ("/getListDevice")
    public ResponseEntity<?> getListDeviceInfo(@RequestParam (value = "customerId") final String customerId,
        @RequestParam (value = "projectId", required = false) final String projectId,
        @RequestParam (value = "objectId", required = false) final String objectId,
        @RequestParam (value = "systemTypeId", required = false) final String systemTypeId) {

        List<Device> respone = new ArrayList<>();

        String schema = Schema.getSchemas(Integer.valueOf(customerId));

        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        // CHECKSTYLE:OFF
        minute = ( (minute / 5) - 1) * 5 - 10;
        if (minute >= 60) {
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.MINUTE, 0);
        } else {
            currentTime.set(Calendar.MINUTE, minute);
        }

        String toDate = DateUtils.toString(new Date(), Constants.ES.DATETIME_FORMAT_YMDHMS);
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = dateFormatWithTime.format(currentTime.getTime());
        SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");

        String curYear = yearFormatWithTime.format(new Date());

        Map<String, String> condition = new HashMap<>();

        condition.put("customerId", customerId);
        if (!projectId.equals("0")) {
            condition.put("projectId", projectId);
        }
        if (systemTypeId != "" && systemTypeId != "0") {
            condition.put("systemTypeId", systemTypeId);
        }
        condition.put("objectTypeId", String.valueOf(objectId));

        respone = deviceService.getDeviceByObjectTypeId(condition);
        for (Device device : respone) {
            Map<String, String> condi = new HashMap<>();
            Device instance = null;
            condi.put("deviceId", String.valueOf(device.getDeviceId()));
            condi.put("schema", schema);
            condi.put("year", curYear);

            if (device.getDeviceType() == 1) {
                instance = deviceService.getDataInstanceLoad(condi);
                if (instance != null) {
                    if (instance.getLoadTypeName() != null) {
                        device.setLoadTypeName(instance.getLoadTypeName());
                    }
                }
                // System.out.println("instance: "+instance);
            }

            condi.put("fromDate", fromDate);
            condi.put("toDate", toDate);

            if (device.getDeviceType() == 1) {
                instance = deviceService.getDataInstanceLoad(condi);
                if (instance != null) {
                    device.setPTotal(instance.getPTotal());
                    device.setQTotal(instance.getQTotal());
                    device.setUab(instance.getUab());
                    device.setUbc(instance.getUbc());
                    device.setUca(instance.getUca());
                    device.setIb(instance.getIb());
                    device.setIc(instance.getIc());
                    device.setIa(instance.getIa());
                    device.setPfa(instance.getPfa());
                    device.setPfb(instance.getPfb());
                    device.setPfc(instance.getPfc());
                    System.out.println("ia: " + instance.getIa());
                    System.out.println("ib: " + instance.getIb());
                }
            }
            if (device.getDeviceType() == 3) {
                instance = deviceService.getDataInstanceSensor(condi);
                if (instance != null) {
                    device.setTSensor(instance.getTSensor());
                    device.setH(instance.getH());
                }
            }
            if (device.getDeviceType() == 4) {
                instance = deviceService.getDataInstanceSensorStatus(condi);
                if (instance != null) {
                    device.setStatus(instance.getStatus());
                } else {
                    device.setStatus(0);
                }
            }
            if (device.getDeviceType() == 9) {
                instance = deviceService.getDataInstanceGatewayStatus(condi);
                if (instance != null) {
                    device.setStatus(instance.getStatus());
                } else {
                    device.setStatus(0);
                }
            }

            if (device.getDeviceType() == 2) {
                instance = deviceService.getDataInstanceInverter(condi);
                if (instance != null) {
                    device.setPTotal(instance.getPTotal());
                    device.setQTotal(instance.getQTotal());
                    device.setUab(instance.getUab());
                    device.setIa(instance.getIa());
                    device.setPfa(instance.getPfa());
                }
            }

            if (device.getDeviceType() == 5) {
                instance = deviceService.getDataInstanceHTR02(condi);
                if (instance != null) {
                    device.setIndicator(instance.getIndicator());
                }
            }

            if (device.getDeviceType() == 6) {
                instance = deviceService.getDataInstanceAMS01(condi);
                if (instance != null) {
                    device.setIndicator(instance.getIndicator());
                }
            }
            
            if (device.getDeviceType() == 7) {
                instance = deviceService.getDataInstancePressure(condi);
                if (instance != null) {
                    device.setP(instance.getP());
                }
            }

            if (device.getDeviceType() == 10) {
                instance = deviceService.getDataInstanceFlow(condi);
                if (instance != null) {
                    device.setFs(instance.getFs());
                }
            }

            
            if (instance != null) {
                if (instance.getStatus() == 1) {
                    device.setStatusDevice("warning");
                } else {
                    device.setStatusDevice("active");
                }
            } else {
                device.setStatusDevice("offline");
            }
        }

        return new ResponseEntity<List<Device>>(respone, HttpStatus.OK);
    }

    @GetMapping ("listDataInstanceFrame2/{customerId}/{deviceId}/{optionTime}")
    public ResponseEntity<?> getListDataInstanceFrame2(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("deviceId") final Integer deviceId,
        @RequestParam (value = "fromDate", required = false) final String fDate,
        @RequestParam (value = "toDate", required = false) final String tDate,
        @PathVariable ("optionTime") final Integer optionTime) throws ParseException {
        log.info("getlistDataInstanceSTART");
        List<Device> respone = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        String fromDate = null;
        String toDate = null;
        String conditionMinute = "15";
        if (optionTime == 0) {
            conditionMinute = "5";
        }
        if (optionTime == 1) {
            conditionMinute = "15";
        }
        if (optionTime == 2) {
            conditionMinute = "30";
        }
        if (optionTime == 3) {
            conditionMinute = "60";
        }

        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");
        String curYear = yearFormatWithTime.format(new Date());

        if (fDate != null && fDate != "") {
            fromDate = fDate;
            Date year = yearFormatWithTime.parse(fromDate);
            DateFormat yearfomat = new SimpleDateFormat("yyyy");
            curYear = yearfomat.format(year);
        } else {
            fromDate = dateFormatWithTime.format(new Date());
        }

        if (tDate != null && tDate != "") {
            Date startDate = dateFormatWithTime.parse(fromDate);
            Date endDate = dateFormatWithTime.parse(tDate);
            Date year = yearFormatWithTime.parse(fromDate);

            DateFormat startDay = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            DateFormat endDay = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            DateFormat yearfomat = new SimpleDateFormat("yyyy");

            fromDate = startDay.format(startDate);
            toDate = endDay.format(endDate);
            curYear = yearfomat.format(year);
        }

        condition.put("schema", schema);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceId", String.valueOf(deviceId));
        condition.put("year", curYear);
        condition.put("minute", conditionMinute);
        condition.put("customerId", String.valueOf(customerId));
        Device infoDevice = deviceService.getInfoDevice(condition);

        if (infoDevice.getSystemTypeId() == 1) {
            respone = deviceService.getListDataInstanceLoadFrame2(condition);
        }
        if (infoDevice.getSystemTypeId() == 2) {
            respone = deviceService.getListDataInstanceLoadFrame2(condition);
        }
        if (infoDevice.getDeviceType() == 3) {
            respone = deviceService.getListDataInstanceLoadFrame2(condition);
        }
        if (infoDevice.getDeviceType() == 4) {
            respone = deviceService.getListDataInstanceLoadFrame2(condition);
        }
        if (infoDevice.getSystemTypeId() == 5) {
            respone = deviceService.getListDataInstanceLoadFrame2(condition);
        }

        return new ResponseEntity<>(respone, HttpStatus.OK);
    }

    private void callToReceiver(final String setting) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.S3mQueue.HOST_NAME);
        connectionFactory.setPort(Constants.S3mQueue.NUMBER_5672);
        connectionFactory.setUsername(Constants.S3mQueue.LOAD_USER_NAME);
        connectionFactory.setPassword(Constants.S3mQueue.LOAD_PASSWORD);

        try (Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(Constants.S3mQueue.EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);

            String message = setting;

            channel.basicPublish(Constants.S3mQueue.EXCHANGE_NAME, Constants.S3mQueue.TOPIC_SETTING_NAME, null,
                message.getBytes());
            log.info(" [JMS] UPDATE SETTING: " + message);
        }
    }

    @GetMapping ("list-by-object-type")
    public ResponseEntity<List<Device>> getListDeviceByObjectType(@RequestParam ("systemType") String systemType,
        @RequestParam ("project") String project, @RequestParam ("objectType") String objectType) {
        Map<String, String> con = new HashMap<>();
        con.put("systemType", systemType);
        con.put("objectType", objectType);
        con.put("project", project);
        List<Device> ls = this.deviceService.getDeviceByObjectType(con);
        return new ResponseEntity<List<Device>>(ls, HttpStatus.OK);
    }

    @GetMapping ("list-by-area")
    public ResponseEntity<List<Device>> getListDeviceByArea(@RequestParam ("systemType") String systemType,
        @RequestParam ("project") String project, @RequestParam ("area") String area) {
        Map<String, String> con = new HashMap<>();
        con.put("systemType", systemType);
        if (area.equals("null")) {
            con.put("location", null);
        } else {
            con.put("location", area);
        }
        con.put("project", project);
        List<Device> ls = this.deviceService.getDeviceByLoca(con);
        return new ResponseEntity<List<Device>>(ls, HttpStatus.OK);
    }

    @GetMapping ("list-by-load-type")
    public ResponseEntity<List<Device>> getListDeviceByLoadType(@RequestParam ("systemType") String systemType,
        @RequestParam ("project") String project, @RequestParam ("loadType") String loadType) {
        Map<String, String> con = new HashMap<>();
        con.put("systemType", systemType);
        con.put("loadType", loadType);
        con.put("project", project);
        List<Device> ls = this.deviceService.getDeviceByLoadType(con);
        return new ResponseEntity<List<Device>>(ls, HttpStatus.OK);
    }

    @GetMapping ("/exportDataInstance/{customerId}/{deviceId}/{optionTime}")
    public ResponseEntity<?> exportDataInstance(@PathVariable ("customerId") final Integer customerId,
        @PathVariable ("deviceId") final Integer deviceId,
        @RequestParam (value = "fromDate", required = false) final String fDate,
        @RequestParam (value = "toDate", required = false) final String tDate,
        @RequestParam (value = "projectName", required = false) final String projectName,
        @RequestParam (value = "deviceName", required = false) final String deviceName,
        @RequestParam (value = "optionName", required = false) final Integer optionName,
        @PathVariable ("optionTime") final Integer optionTime, @RequestParam ("typeDate") final Integer typeDate,
        @RequestParam (value = "optionNameChild", required = false) final Integer optionNameChild)
        throws ParseException {

        String reportName = "";
        String schema = Schema.getSchemas(customerId);
        String moduleName = "";
        String optionNameExport = "";

        List<Device> respone = new ArrayList<>();
        List<Device> responeFrame2 = new ArrayList<>();
        List<Device> responeHumidity = new ArrayList<>();

        Map<String, String> condition = new HashMap<>();
        String fromDate = null;
        String toDate = null;
        String conditionMinute = "";
        if (optionTime == 0) {
            conditionMinute = "5";
        }
        if (optionTime == 1) {
            conditionMinute = "15";
        }
        if (optionTime == 2) {
            conditionMinute = "30";
        }
        if (optionTime == 3) {
            conditionMinute = "60";
        }
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");
        String curYear = yearFormatWithTime.format(new Date());

        if (fDate != null && fDate != "") {
            fromDate = fDate;
            Date year = yearFormatWithTime.parse(fromDate);
            DateFormat yearfomat = new SimpleDateFormat("yyyy");
            curYear = yearfomat.format(year);
        } else {
            fromDate = dateFormatWithTime.format(new Date());
        }

        if (tDate != null && tDate != "") {
            Date startDate = dateFormatWithTime.parse(fromDate);
            Date endDate = dateFormatWithTime.parse(tDate);
            Date year = yearFormatWithTime.parse(fromDate);

            DateFormat startDay = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            DateFormat endDay = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            DateFormat yearfomat = new SimpleDateFormat("yyyy");

            fromDate = startDay.format(startDate);
            toDate = endDay.format(endDate);
            curYear = yearfomat.format(year);
        }

        condition.put("schema", schema);
        condition.put("customerId", String.valueOf(customerId));
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("deviceId", String.valueOf(deviceId));
        condition.put("year", curYear);
        condition.put("minute", conditionMinute);

        Device infoDevice = deviceService.getInfoDevice(condition);

        if (infoDevice.getSystemTypeId() == 1) {
            moduleName = "TẢI ĐIỆN";
        } else if (infoDevice.getSystemTypeId() == 2) {
            moduleName = "ĐIỆN MẶT TRỜI";
        } else if (infoDevice.getSystemTypeId() == 3) {
            moduleName = "ĐIỆN GIÓ";
        } else if (infoDevice.getSystemTypeId() == 4) {
            moduleName = "PIN LƯU TRỮ";
        } else if (infoDevice.getSystemTypeId() == 5) {
            moduleName = "LƯỚI ĐIỆN";
        }

        if (optionName == 1) {
            reportName = "DỮ LIỆU ĐIỆN ÁP";
            optionNameExport = "DIENAP";
        } else if (optionName == 2) {
            reportName = "DỮ LIỆU CÔNG SUẤT";
            optionNameExport = "CONGSUAT";
        } else if (optionName == 3) {
            reportName = "DỮ LIỆU DÒNG ĐIỆN";
            optionNameExport = "ĐONGDIEN";
        } else if (optionName == 4) {
            reportName = "DỮ LIỆU COSPHI";
            optionNameExport = "COSPHI";
        } else if (optionName == 5) {
            reportName = "DỮ LIỆU ĐIỆN NĂNG";
            optionNameExport = "DIENNANG";
        } else if (optionName == 6) {
            reportName = "DỮ LIỆU TẦN SỐ";
            optionNameExport = "TANSO";
        } else if (optionName == 7) {
            reportName = "DỮ LIỆU CHẤT LƯỢNG ĐIỆN NĂNG";
            optionNameExport = "CHATLUONGDIENNANG";
        } else if (optionName == 8) {
            reportName = "DỮ LIỆU SÓNG HÀI DÒNG ĐIỆN";
            optionNameExport = "SONGHAIDONGDIEN";
        } else if (optionName == 9) {
            reportName = "DỮ LIỆU SÓNG HÀI ĐIỆN ÁP";
            optionNameExport = "SONGHAIDIENAP";
        } else if (optionName == 10) {
            reportName = "DỮ LIỆU THÔNG SỐ PHÓNG ĐIỆN";
            optionNameExport = "THONGSOPHONGDIEN";
        } else if (optionName == 11) {
            reportName = "DỮ LIỆU ĐỘ ẨM";
            optionNameExport = "DOAM";
        } else if (optionName == 12) {
            reportName = "DỮ LIỆU RATIO EPPC";
            optionNameExport = "RATIOEPPC";
        } else if (optionName == 13) {
            reportName = "DỮ LIỆU INDICATOR";
            optionNameExport = "INDICATOR";
        }else if (optionName == 15) {
            reportName = "DỮ LIỆU LƯU LƯỢNG";
            optionNameExport = "LUULUONG";
        }else if (optionName == 16) {
            reportName = "DỮ LIỆU LƯU LƯỢNG TÍCH LŨY";
            optionNameExport = "LUULUONGTICHLUY";
        }else if (optionName == 17) {
            reportName = "DỮ LIỆU ÁP SUẤT";
            optionNameExport = "APSUAT";
        }

        if (infoDevice.getSystemTypeId() == 1) {
            respone = deviceService.getListDataInstanceLoad(condition);
            List<Device> data = deviceService.getDataEpLoad(condition);
            for (int i = 0; i < respone.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j)
                        .getSendDate()
                        .equals(respone.get(i)
                            .getSendDate())) {
                        respone.get(i)
                            .setEp(data.get(j)
                                .getEp());
                    }
                }
            }
        }
        if (infoDevice.getSystemTypeId() == 2) {
            if (infoDevice.getDeviceType() == 1) {
                respone = deviceService.getListDataInstanceInverter(condition);
                List<Device> data = deviceService.getDataEpInverter(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
            if (infoDevice.getDeviceType() == 3) {
                reportName = "DỮ LIỆU NHIỆT ĐỘ";
                optionNameExport = "NHIETDO";
                respone = deviceService.getListDataInstanceCombiner(condition);
                List<Device> data = deviceService.getDataEpCombiner(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
            if (infoDevice.getDeviceType() == 4) {
                respone = deviceService.getListDataInstanceString(condition);
                List<Device> data = deviceService.getDataEpString(condition);
                for (int i = 0; i < respone.size(); i++) {
                    for (int j = 0; j < data.size(); j++) {
                        if (data.get(j)
                            .getSendDate()
                            .equals(respone.get(i)
                                .getSendDate())) {
                            respone.get(i)
                                .setEp(data.get(j)
                                    .getEp());
                        }
                    }
                }
            }
        }
        if (infoDevice.getSystemTypeId() == 5) {
            respone = deviceService.getListDataInstanceRMU(condition);
            List<Device> data = deviceService.getDataEpRMU(condition);
            for (int i = 0; i < respone.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j)
                        .getSendDate()
                        .equals(respone.get(i)
                            .getSendDate())) {
                        respone.get(i)
                            .setEp(data.get(j)
                                .getEp());
                    }
                }
            }
        }

        if (infoDevice.getDeviceType() == 5 || infoDevice.getDeviceType() == 6) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstanceRatioIndicator(condition);
        }
        
        if (infoDevice.getDeviceType() == 7) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstancePressure(condition);
        }
        
        if (infoDevice.getDeviceType() == 10) {
            condition.put("deviceType", String.valueOf(infoDevice.getDeviceType()));
            respone = deviceService.getListDataInstanceFlow(condition);
        }

        if (optionName == 8 || optionName == 9) {
            responeFrame2 = deviceService.getListDataInstanceLoadFrame2(condition);
        }
        
        responeHumidity = deviceService.getListDataInstanceLoadHumidity(condition);

        // get Customer
        Map<String, String> cus = new HashMap<>();
        cus.put("customerId", customerId + "");
        Customer custtomer = customerService.getCustomer(cus);

        // get date now
        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = formatter.format(dateNow);
        long miliseconds = new Date().getTime();

        if (toDate == null) {
            toDate = fromDate;
        }
        String path = this.folderName + File.separator + convertToCamelCase(projectName).toUpperCase() + "_"
            + convertToCamelCase(moduleName).toUpperCase() + "_" + convertToCamelCase(deviceName).toUpperCase() + "_"
            + optionNameExport + "_" + convertToCamelCase(fromDate + toDate) + "_" + miliseconds;

        String fileNameExcel = convertToCamelCase(projectName).toUpperCase() + "_"
            + convertToCamelCase(moduleName).toUpperCase() + "_" + convertToCamelCase(deviceName).toUpperCase() + "_"
            + optionNameExport + "_" + convertToCamelCase(fromDate + toDate) + "_" + miliseconds;

        // if (result.size() <= 0) {
        // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        // }
        // Export data dien ap

        if (optionName == 1) {
            try {
                createDataVoltageExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                    .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data cong suat
        if (optionName == 2) {
            try {
                createDataWattageExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                    .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data dong dien
        if (optionName == 3) {
            try {
                createDataElectricExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                    .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data cosphi
        if (optionName == 4) {
            try {
                createDataCosphiExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                    .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data Ep
        if (optionName == 5) {
            try {
                createDataEpExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                    .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data nhiet do and tan so
        if (optionName == 6) {
            if (infoDevice.getDeviceType() == 3) {
                try {
                    createDataTExcel(responeHumidity, responeFrame2, responeHumidity, custtomer.getCustomerName()
                        .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName,
                        fromDate, toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    createDataFExcel(respone, responeFrame2, responeHumidity, custtomer.getCustomerName()
                        .toUpperCase(), custtomer.getDescription(), typeDate, reportName, moduleName, projectName,
                        fromDate, toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Export data do am
        if (optionName == 11) {
            try {
                createDataHExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                    custtomer.getCustomerName()
                        .toUpperCase(),
                    custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (optionName == 12) {
            try {
                createDataRatioEppcExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                    custtomer.getCustomerName()
                        .toUpperCase(),
                    custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName,
                    infoDevice.getDeviceType());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (optionName == 13) {
            try {
                createDataIndicatorExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                    custtomer.getCustomerName()
                        .toUpperCase(),
                    custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        if (optionName == 15 || optionName == 16 || optionName == 17 ) {
            try {
            	createDataPressureFlowExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                    custtomer.getCustomerName()
                        .toUpperCase(),
                    custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                    toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName, optionName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Export data song hai dong dien
        if (optionName == 8) {

            if (optionNameChild == 1) {
                try {
                    createDataIaExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (optionNameChild == 2) {
                try {
                    createDataIbExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (optionNameChild == 3) {
                try {
                    createDataIcExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        // Export data song hai dien ap
        if (optionName == 9) {

            // if (optionNameChild == 1) {
            // try {
            // createDataUabExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
            // custtomer.getCustomerName()
            // .toUpperCase(),
            // custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
            // toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            // } catch (Exception e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            // if (optionNameChild == 2) {
            // try {
            // createDataUbcExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
            // custtomer.getCustomerName()
            // .toUpperCase(),
            // custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
            // toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            // } catch (Exception e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            // if (optionNameChild == 3) {
            // try {
            // createDataUcaExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
            // custtomer.getCustomerName()
            // .toUpperCase(),
            // custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
            // toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
            // } catch (Exception e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // }
            if (optionNameChild == 1) {
                try {
                    createDataUanExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (optionNameChild == 2) {
                try {
                    createDataUbnExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (optionNameChild == 3) {
                try {
                    createDataUcnExcel(respone.size() > 0 ? respone : responeHumidity, responeFrame2, responeHumidity,
                        custtomer.getCustomerName()
                            .toUpperCase(),
                        custtomer.getDescription(), typeDate, reportName, moduleName, projectName, fromDate,
                        toDate == null ? fromDate : toDate, strDate, path, fileNameExcel, deviceName);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
          
        }

        log.info("ReportController.downloadReport() START");
        File f = new File(path);
        if (f.exists()) {
            log.info("ReportController.downloadReport() check file exists");
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + f.getName() + ".zip";
            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("ReportController.downloadReport() END");
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);

        } else {
            log.info("ReportController.downloadReport() error");
            return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
        }
    }

    private void createDataRatioEppcExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName, Integer deviceType) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);
        sheet1.setColumnWidth(9, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;

        if (deviceType == 5) {
            // Cột THỜI GIAN
            region = new CellRangeAddress(10, 10, 0, 1);
            sheet1.addMergedRegion(region);
            cell1 = sheet1.getRow(10)
                .getCell(0);
            cell1.setCellValue("THỜI GIAN");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 2, 2);
            cell1 = sheet1.getRow(10)
                .getCell(2);
            cell1.setCellValue("LFB RATIO(peak)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 3, 3);
            cell1 = sheet1.getRow(10)
                .getCell(3);
            cell1.setCellValue("LFB EPPC(eppc)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 4, 4);
            cell1 = sheet1.getRow(10)
                .getCell(4);
            cell1.setCellValue("MFB RATIO(peak)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 5, 5);
            cell1 = sheet1.getRow(10)
                .getCell(5);
            cell1.setCellValue("MFB EPPC(eppc)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 6, 6);
            cell1 = sheet1.getRow(10)
                .getCell(6);
            cell1.setCellValue("HFB RATIO(peak)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 7, 7);
            cell1 = sheet1.getRow(10)
                .getCell(7);
            cell1.setCellValue("HFB EPPC(eppc)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 8, 8);
            cell1 = sheet1.getRow(10)
                .getCell(8);
            cell1.setCellValue("MEAN RATIO(peak)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 9, 9);
            cell1 = sheet1.getRow(10)
                .getCell(9);
            cell1.setCellValue("MEAN EPPC(eppc)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        } else {

            region = new CellRangeAddress(10, 10, 0, 1);
            sheet1.addMergedRegion(region);
            cell1 = sheet1.getRow(10)
                .getCell(0);
            cell1.setCellValue("THỜI GIAN");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 2, 2);
            cell1 = sheet1.getRow(10)
                .getCell(2);
            cell1.setCellValue("RATIO(peak)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;

            if (deviceType == 5) {
                // Cột thời gian
                region = new CellRangeAddress(rowCount, rowCount, 0, 1);
                sheet1.addMergedRegion(region);
                cellData = sheet1.getRow(rowCount)
                    .getCell(0);
                cellData.setCellValue(viewTime.format(item.getSendDate()));
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(item.getLfbRatio() == null ? 0 : item.getLfbRatio());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 3, 3);
                cellData = sheet1.getRow(rowCount)
                    .getCell(3);
                cellData.setCellValue(item.getLfbEppc() == null ? 0 : item.getLfbEppc());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 4, 4);
                cellData = sheet1.getRow(rowCount)
                    .getCell(4);
                cellData.setCellValue(item.getMfbRatio() == null ? 0 : item.getMfbRatio());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 5, 5);
                cellData = sheet1.getRow(rowCount)
                    .getCell(5);
                cellData.setCellValue(item.getMfbEppc() == null ? 0 : item.getMfbEppc());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 6, 6);
                cellData = sheet1.getRow(rowCount)
                    .getCell(6);
                cellData.setCellValue(item.getHfbRatio() == null ? 0 : item.getHfbRatio());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 7, 7);
                cellData = sheet1.getRow(rowCount)
                    .getCell(7);
                cellData.setCellValue(item.getHfbEppc() == null ? 0 : item.getHfbEppc());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 8, 8);
                cellData = sheet1.getRow(rowCount)
                    .getCell(8);
                cellData.setCellValue(item.getMeanRatio() == null ? 0 : item.getMeanRatio());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                region = new CellRangeAddress(rowCount, rowCount, 9, 9);
                cellData = sheet1.getRow(rowCount)
                    .getCell(9);
                cellData.setCellValue(item.getMeanEppc() == null ? 0 : item.getMeanEppc());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            } else {
                region = new CellRangeAddress(rowCount, rowCount, 0, 1);
                sheet1.addMergedRegion(region);
                cellData = sheet1.getRow(rowCount)
                    .getCell(0);
                cellData.setCellValue(viewTime.format(item.getSendDate()));
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
                cellData.setCellValue(item.getRatio() == null ? 0 : item.getRatio());
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            }
            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataIndicatorExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Indicator");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getIndicator() == null ? 0 : item.getIndicator());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUcnExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ucn(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Ucn_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVCnH1() == null ? 0 : item.getVCnH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVCnH2() == null ? 0 : item.getVCnH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVCnH3() == null ? 0 : item.getVCnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVCnH3() == null ? 0 : item.getVCnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVCnH4() == null ? 0 : item.getVCnH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVCnH5() == null ? 0 : item.getVCnH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVCnH6() == null ? 0 : item.getVCnH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVCnH7() == null ? 0 : item.getVCnH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVCnH8() == null ? 0 : item.getVCnH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVCnH9() == null ? 0 : item.getVCnH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVCnH10() == null ? 0 : item.getVCnH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVCnH11() == null ? 0 : item.getVCnH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVCnH12() == null ? 0 : item.getVCnH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVCnH13() == null ? 0 : item.getVCnH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVCnH14() == null ? 0 : item.getVCnH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVCnH15() == null ? 0 : item.getVCnH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVCnH16() == null ? 0 : item.getVCnH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVCnH17() == null ? 0 : item.getVCnH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVCnH18() == null ? 0 : item.getVCnH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVCnH19() == null ? 0 : item.getVCnH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVCnH20() == null ? 0 : item.getVCnH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVCnH21() == null ? 0 : item.getVCnH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVCnH22() == null ? 0 : item.getVCnH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVCnH23() == null ? 0 : item.getVCnH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVCnH24() == null ? 0 : item.getVCnH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVCnH25() == null ? 0 : item.getVCnH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVCnH26() == null ? 0 : item.getVCnH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVCnH27() == null ? 0 : item.getVCnH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVCnH28() == null ? 0 : item.getVCnH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVCnH29() == null ? 0 : item.getVCnH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVCnH30() == null ? 0 : item.getVCnH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVCnH31() == null ? 0 : item.getVCnH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVcn() == null ? 0 : item.getThdVcn());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUbnExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ubn(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Ubn_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVBnH1() == null ? 0 : item.getVBnH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVBnH2() == null ? 0 : item.getVBnH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVBnH3() == null ? 0 : item.getVBnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVBnH3() == null ? 0 : item.getVBnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVBnH4() == null ? 0 : item.getVBnH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVBnH5() == null ? 0 : item.getVBnH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVBnH6() == null ? 0 : item.getVBnH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVBnH7() == null ? 0 : item.getVBnH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVBnH8() == null ? 0 : item.getVBnH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVBnH9() == null ? 0 : item.getVBnH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVBnH10() == null ? 0 : item.getVBnH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVBnH11() == null ? 0 : item.getVBnH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVBnH12() == null ? 0 : item.getVBnH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVBnH13() == null ? 0 : item.getVBnH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVBnH14() == null ? 0 : item.getVBnH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVBnH15() == null ? 0 : item.getVBnH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVBnH16() == null ? 0 : item.getVBnH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVBnH17() == null ? 0 : item.getVBnH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVBnH18() == null ? 0 : item.getVBnH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVBnH19() == null ? 0 : item.getVBnH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVBnH20() == null ? 0 : item.getVBnH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVBnH21() == null ? 0 : item.getVBnH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVBnH22() == null ? 0 : item.getVBnH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVBnH23() == null ? 0 : item.getVBnH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVBnH24() == null ? 0 : item.getVBnH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVBnH25() == null ? 0 : item.getVBnH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVBnH26() == null ? 0 : item.getVBnH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVBnH27() == null ? 0 : item.getVBnH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVBnH28() == null ? 0 : item.getVBnH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVBnH29() == null ? 0 : item.getVBnH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVBnH30() == null ? 0 : item.getVBnH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVBnH31() == null ? 0 : item.getVBnH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVbn() == null ? 0 : item.getThdVbn());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUanExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Uan(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Uan_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVAnH1() == null ? 0 : item.getVAnH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVAnH2() == null ? 0 : item.getVAnH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVAnH3() == null ? 0 : item.getVAnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVAnH3() == null ? 0 : item.getVAnH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVAnH4() == null ? 0 : item.getVAnH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVAnH5() == null ? 0 : item.getVAnH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVAnH6() == null ? 0 : item.getVAnH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVAnH7() == null ? 0 : item.getVAnH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVAnH8() == null ? 0 : item.getVAnH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVAnH9() == null ? 0 : item.getVAnH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVAnH10() == null ? 0 : item.getVAnH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVAnH11() == null ? 0 : item.getVAnH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVAnH12() == null ? 0 : item.getVAnH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVAnH13() == null ? 0 : item.getVAnH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVAnH14() == null ? 0 : item.getVAnH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVAnH15() == null ? 0 : item.getVAnH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVAnH16() == null ? 0 : item.getVAnH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVAnH17() == null ? 0 : item.getVAnH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVAnH18() == null ? 0 : item.getVAnH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVAnH19() == null ? 0 : item.getVAnH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVAnH20() == null ? 0 : item.getVAnH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVAnH21() == null ? 0 : item.getVAnH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVAnH22() == null ? 0 : item.getVAnH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVAnH23() == null ? 0 : item.getVAnH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVAnH24() == null ? 0 : item.getVAnH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVAnH25() == null ? 0 : item.getVAnH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVAnH26() == null ? 0 : item.getVAnH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVAnH27() == null ? 0 : item.getVAnH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVAnH28() == null ? 0 : item.getVAnH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVAnH29() == null ? 0 : item.getVAnH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVAnH30() == null ? 0 : item.getVAnH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVAnH31() == null ? 0 : item.getVAnH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVan() == null ? 0 : item.getThdVan());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUcaExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Uca(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Uca_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVCaH1() == null ? 0 : item.getVCaH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVCaH2() == null ? 0 : item.getVCaH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVCaH3() == null ? 0 : item.getVCaH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVCaH3() == null ? 0 : item.getVCaH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVCaH4() == null ? 0 : item.getVCaH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVCaH5() == null ? 0 : item.getVCaH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVCaH6() == null ? 0 : item.getVCaH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVCaH7() == null ? 0 : item.getVCaH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVCaH8() == null ? 0 : item.getVCaH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVCaH9() == null ? 0 : item.getVCaH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVCaH10() == null ? 0 : item.getVCaH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVCaH11() == null ? 0 : item.getVCaH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVCaH12() == null ? 0 : item.getVCaH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVCaH13() == null ? 0 : item.getVCaH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVCaH14() == null ? 0 : item.getVCaH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVCaH15() == null ? 0 : item.getVCaH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVCaH16() == null ? 0 : item.getVCaH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVCaH17() == null ? 0 : item.getVCaH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVCaH18() == null ? 0 : item.getVCaH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVCaH19() == null ? 0 : item.getVCaH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVCaH20() == null ? 0 : item.getVCaH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVCaH21() == null ? 0 : item.getVCaH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVCaH22() == null ? 0 : item.getVCaH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVCaH23() == null ? 0 : item.getVCaH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVCaH24() == null ? 0 : item.getVCaH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVCaH25() == null ? 0 : item.getVCaH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVCaH26() == null ? 0 : item.getVCaH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVCaH27() == null ? 0 : item.getVCaH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVCaH28() == null ? 0 : item.getVCaH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVCaH29() == null ? 0 : item.getVCaH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVCaH30() == null ? 0 : item.getVCaH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVCaH31() == null ? 0 : item.getVCaH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVca() == null ? 0 : item.getThdVca());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUbcExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ubc(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Ubc_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVBcH1() == null ? 0 : item.getVBcH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVBcH2() == null ? 0 : item.getVBcH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVBcH3() == null ? 0 : item.getVBcH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVBcH3() == null ? 0 : item.getVBcH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVBcH4() == null ? 0 : item.getVBcH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVBcH5() == null ? 0 : item.getVBcH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVBcH6() == null ? 0 : item.getVBcH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVBcH7() == null ? 0 : item.getVBcH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVBcH8() == null ? 0 : item.getVBcH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVBcH9() == null ? 0 : item.getVBcH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVBcH10() == null ? 0 : item.getVBcH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVBcH11() == null ? 0 : item.getVBcH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVBcH12() == null ? 0 : item.getVBcH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVBcH13() == null ? 0 : item.getVBcH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVBcH14() == null ? 0 : item.getVBcH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVBcH15() == null ? 0 : item.getVBcH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVBcH16() == null ? 0 : item.getVBcH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVBcH17() == null ? 0 : item.getVBcH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVBcH18() == null ? 0 : item.getVBcH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVBcH19() == null ? 0 : item.getVBcH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVBcH20() == null ? 0 : item.getVBcH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVBcH21() == null ? 0 : item.getVBcH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVBcH22() == null ? 0 : item.getVBcH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVBcH23() == null ? 0 : item.getVBcH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVBcH24() == null ? 0 : item.getVBcH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVBcH25() == null ? 0 : item.getVBcH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVBcH26() == null ? 0 : item.getVBcH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVBcH27() == null ? 0 : item.getVBcH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVBcH28() == null ? 0 : item.getVBcH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVBcH29() == null ? 0 : item.getVBcH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVBcH30() == null ? 0 : item.getVBcH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVBcH31() == null ? 0 : item.getVBcH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVbc() == null ? 0 : item.getThdVbc());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataUabExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Uab(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("Uab_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getVAbH1() == null ? 0 : item.getVAbH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getVAbH2() == null ? 0 : item.getVAbH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVAbH3() == null ? 0 : item.getVAbH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getVAbH3() == null ? 0 : item.getVAbH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getVAbH4() == null ? 0 : item.getVAbH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getVAbH5() == null ? 0 : item.getVAbH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getVAbH6() == null ? 0 : item.getVAbH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getVAbH7() == null ? 0 : item.getVAbH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getVAbH8() == null ? 0 : item.getVAbH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getVAbH9() == null ? 0 : item.getVAbH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getVAbH10() == null ? 0 : item.getVAbH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getVAbH11() == null ? 0 : item.getVAbH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getVAbH12() == null ? 0 : item.getVAbH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getVAbH13() == null ? 0 : item.getVAbH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getVAbH14() == null ? 0 : item.getVAbH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getVAbH15() == null ? 0 : item.getVAbH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getVAbH16() == null ? 0 : item.getVAbH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getVAbH17() == null ? 0 : item.getVAbH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getVAbH18() == null ? 0 : item.getVAbH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getVAbH19() == null ? 0 : item.getVAbH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getVAbH20() == null ? 0 : item.getVAbH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getVAbH21() == null ? 0 : item.getVAbH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getVAbH22() == null ? 0 : item.getVAbH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getVAbH23() == null ? 0 : item.getVAbH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getVAbH24() == null ? 0 : item.getVAbH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getVAbH25() == null ? 0 : item.getVAbH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getVAbH26() == null ? 0 : item.getVAbH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getVAbH27() == null ? 0 : item.getVAbH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getVAbH28() == null ? 0 : item.getVAbH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getVAbH29() == null ? 0 : item.getVAbH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getVAbH30() == null ? 0 : item.getVAbH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getVAbH31() == null ? 0 : item.getVAbH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdVab() == null ? 0 : item.getThdVab());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataIcExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ic(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("IC_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getIcH1() == null ? 0 : item.getIcH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getIcH2() == null ? 0 : item.getIcH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIcH3() == null ? 0 : item.getIcH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIcH3() == null ? 0 : item.getIcH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getIcH4() == null ? 0 : item.getIcH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getIcH5() == null ? 0 : item.getIcH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getIcH6() == null ? 0 : item.getIcH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getIcH7() == null ? 0 : item.getIcH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getIcH8() == null ? 0 : item.getIcH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getIcH9() == null ? 0 : item.getIcH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getIcH10() == null ? 0 : item.getIcH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getIcH11() == null ? 0 : item.getIcH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getIcH12() == null ? 0 : item.getIcH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getIcH13() == null ? 0 : item.getIcH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getIcH14() == null ? 0 : item.getIcH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getIcH15() == null ? 0 : item.getIcH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getIcH16() == null ? 0 : item.getIcH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getIcH17() == null ? 0 : item.getIcH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getIcH18() == null ? 0 : item.getIcH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getIcH19() == null ? 0 : item.getIcH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getIcH20() == null ? 0 : item.getIcH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getIcH21() == null ? 0 : item.getIcH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getIcH22() == null ? 0 : item.getIcH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getIcH23() == null ? 0 : item.getIcH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getIcH24() == null ? 0 : item.getIcH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getIcH25() == null ? 0 : item.getIcH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getIcH26() == null ? 0 : item.getIcH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getIcH27() == null ? 0 : item.getIcH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getIcH28() == null ? 0 : item.getIcH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getIcH29() == null ? 0 : item.getIcH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getIcH30() == null ? 0 : item.getIcH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getIcH31() == null ? 0 : item.getIcH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdIc() == null ? 0 : item.getThdIc());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataIbExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ib(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("IB_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getIbH1() == null ? 0 : item.getIbH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getIbH2() == null ? 0 : item.getIbH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIbH3() == null ? 0 : item.getIbH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIbH3() == null ? 0 : item.getIbH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getIbH4() == null ? 0 : item.getIbH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getIbH5() == null ? 0 : item.getIbH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getIbH6() == null ? 0 : item.getIbH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getIbH7() == null ? 0 : item.getIbH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getIbH8() == null ? 0 : item.getIbH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getIbH9() == null ? 0 : item.getIbH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getIbH10() == null ? 0 : item.getIbH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getIbH11() == null ? 0 : item.getIbH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getIbH12() == null ? 0 : item.getIbH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getIbH13() == null ? 0 : item.getIbH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getIbH14() == null ? 0 : item.getIbH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getIbH15() == null ? 0 : item.getIbH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getIbH16() == null ? 0 : item.getIbH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getIbH17() == null ? 0 : item.getIbH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getIbH18() == null ? 0 : item.getIbH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getIbH19() == null ? 0 : item.getIbH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getIbH20() == null ? 0 : item.getIbH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getIbH21() == null ? 0 : item.getIbH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getIbH22() == null ? 0 : item.getIbH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getIbH23() == null ? 0 : item.getIbH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getIbH24() == null ? 0 : item.getIbH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getIbH25() == null ? 0 : item.getIbH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getIbH26() == null ? 0 : item.getIbH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getIbH27() == null ? 0 : item.getIbH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getIbH28() == null ? 0 : item.getIbH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getIbH29() == null ? 0 : item.getIbH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getIbH30() == null ? 0 : item.getIbH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getIbH31() == null ? 0 : item.getIbH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdIb() == null ? 0 : item.getThdIb());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataIaExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        // set độ rộng của cột
        for (int i = 5; i < 34; i++) {
            sheet1.setColumnWidth(i, 5000);
        }

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("THD_Ia(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        for (int i = 1; i < 32; i++) {
            region = new CellRangeAddress(10, 10, 2 + i, 2 + i);
            cell1 = sheet1.getRow(10)
                .getCell(2 + i);
            cell1.setCellValue("IA_H" + i + "(%)");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Cell cellData;

        for (Device item : listDataF2) {
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getIaH1() == null ? 0 : item.getIaH1());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getIaH2() == null ? 0 : item.getIaH2());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIaH3() == null ? 0 : item.getIaH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getIaH3() == null ? 0 : item.getIaH3());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getIaH4() == null ? 0 : item.getIaH4());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getIaH5() == null ? 0 : item.getIaH5());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getIaH6() == null ? 0 : item.getIaH6());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 9, 9);
            cellData = sheet1.getRow(rowCount)
                .getCell(9);
            cellData.setCellValue(item.getIaH7() == null ? 0 : item.getIaH7());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 10, 10);
            cellData = sheet1.getRow(rowCount)
                .getCell(10);
            cellData.setCellValue(item.getIaH8() == null ? 0 : item.getIaH8());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 11, 11);
            cellData = sheet1.getRow(rowCount)
                .getCell(11);
            cellData.setCellValue(item.getIaH9() == null ? 0 : item.getIaH9());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 12, 12);
            cellData = sheet1.getRow(rowCount)
                .getCell(12);
            cellData.setCellValue(item.getIaH10() == null ? 0 : item.getIaH10());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 13, 13);
            cellData = sheet1.getRow(rowCount)
                .getCell(13);
            cellData.setCellValue(item.getIaH11() == null ? 0 : item.getIaH11());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 14, 14);
            cellData = sheet1.getRow(rowCount)
                .getCell(14);
            cellData.setCellValue(item.getIaH12() == null ? 0 : item.getIaH12());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 15, 15);
            cellData = sheet1.getRow(rowCount)
                .getCell(15);
            cellData.setCellValue(item.getIaH13() == null ? 0 : item.getIaH13());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 16, 16);
            cellData = sheet1.getRow(rowCount)
                .getCell(16);
            cellData.setCellValue(item.getIaH14() == null ? 0 : item.getIaH14());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 17, 17);
            cellData = sheet1.getRow(rowCount)
                .getCell(17);
            cellData.setCellValue(item.getIaH15() == null ? 0 : item.getIaH15());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 18, 18);
            cellData = sheet1.getRow(rowCount)
                .getCell(18);
            cellData.setCellValue(item.getIaH16() == null ? 0 : item.getIaH16());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 19, 19);
            cellData = sheet1.getRow(rowCount)
                .getCell(19);
            cellData.setCellValue(item.getIaH17() == null ? 0 : item.getIaH17());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 20, 20);
            cellData = sheet1.getRow(rowCount)
                .getCell(20);
            cellData.setCellValue(item.getIaH18() == null ? 0 : item.getIaH18());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 21, 21);
            cellData = sheet1.getRow(rowCount)
                .getCell(21);
            cellData.setCellValue(item.getIaH19() == null ? 0 : item.getIaH19());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 22, 22);
            cellData = sheet1.getRow(rowCount)
                .getCell(22);
            cellData.setCellValue(item.getIaH20() == null ? 0 : item.getIaH20());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 23, 23);
            cellData = sheet1.getRow(rowCount)
                .getCell(23);
            cellData.setCellValue(item.getIaH21() == null ? 0 : item.getIaH21());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 24, 24);
            cellData = sheet1.getRow(rowCount)
                .getCell(24);
            cellData.setCellValue(item.getIaH22() == null ? 0 : item.getIaH22());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 25, 25);
            cellData = sheet1.getRow(rowCount)
                .getCell(25);
            cellData.setCellValue(item.getIaH23() == null ? 0 : item.getIaH23());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 26, 26);
            cellData = sheet1.getRow(rowCount)
                .getCell(26);
            cellData.setCellValue(item.getIaH24() == null ? 0 : item.getIaH24());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 27, 27);
            cellData = sheet1.getRow(rowCount)
                .getCell(27);
            cellData.setCellValue(item.getIaH25() == null ? 0 : item.getIaH25());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 28, 28);
            cellData = sheet1.getRow(rowCount)
                .getCell(28);
            cellData.setCellValue(item.getIaH26() == null ? 0 : item.getIaH26());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 29, 29);
            cellData = sheet1.getRow(rowCount)
                .getCell(29);
            cellData.setCellValue(item.getIaH27() == null ? 0 : item.getIaH27());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 30, 30);
            cellData = sheet1.getRow(rowCount)
                .getCell(30);
            cellData.setCellValue(item.getIaH28() == null ? 0 : item.getIaH28());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 31, 31);
            cellData = sheet1.getRow(rowCount)
                .getCell(31);
            cellData.setCellValue(item.getIaH29() == null ? 0 : item.getIaH29());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 32, 32);
            cellData = sheet1.getRow(rowCount)
                .getCell(32);
            cellData.setCellValue(item.getIaH30() == null ? 0 : item.getIaH30());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 33, 33);
            cellData = sheet1.getRow(rowCount)
                .getCell(33);
            cellData.setCellValue(item.getIaH31() == null ? 0 : item.getIaH31());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
        }
        ;
        int rowCountThd = 11;
        for (Device item : listDataF1) {

            final short bgColor;
            if (rowCountThd % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            region = new CellRangeAddress(rowCountThd, rowCountThd, 2, 2);
            cellData = sheet1.getRow(rowCountThd)
                .getCell(2);
            cellData.setCellValue(item.getThdIa() == null ? 0 : item.getThdIa());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");
            rowCountThd += 1;
        }
        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    // date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                    // new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataHExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Độ ẩm(%)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getH() == null ? 0 : item.getH());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataFExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Tần số(Hz)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getF() == null ? 0 : item.getF());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataTExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Nhiệt độ(°C)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getT() == null ? 0 : item.getT());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataEpExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Ep(kWh)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getEp() == null ? 0 : item.getEp());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataCosphiExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Pfa");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 3, 3);
        cell1 = sheet1.getRow(10)
            .getCell(3);
        cell1.setCellValue("Pfb");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 4, 4);
        cell1 = sheet1.getRow(10)
            .getCell(4);
        cell1.setCellValue("Pfc");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getPfa() == null ? 0 : (item.getPfa()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getPfb() == null ? 0 : (item.getPfb()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getPfc() == null ? 0 : (item.getPfc()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataElectricExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Ia(A)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 3, 3);
        cell1 = sheet1.getRow(10)
            .getCell(3);
        cell1.setCellValue("Ib(A)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 4, 4);
        cell1 = sheet1.getRow(10)
            .getCell(4);
        cell1.setCellValue("Ic(A)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 5, 5);
        cell1 = sheet1.getRow(10)
            .getCell(5);
        cell1.setCellValue("I(A)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getIa() == null ? 0 : (item.getIa()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getIb() == null ? 0 : (item.getIb()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getIc() == null ? 0 : (item.getIc()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getI() == null ? 0 : (item.getI()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataWattageExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 8);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Module");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Site");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Device");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Pa(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 3, 3);
        cell1 = sheet1.getRow(10)
            .getCell(3);
        cell1.setCellValue("Pb(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 4, 4);
        cell1 = sheet1.getRow(10)
            .getCell(4);
        cell1.setCellValue("Pc(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 5, 5);
        cell1 = sheet1.getRow(10)
            .getCell(5);
        cell1.setCellValue("Qa(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 6, 6);
        cell1 = sheet1.getRow(10)
            .getCell(6);
        cell1.setCellValue("Qb(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 7, 7);
        cell1 = sheet1.getRow(10)
            .getCell(7);
        cell1.setCellValue("Qc(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 8, 8);
        cell1 = sheet1.getRow(10)
            .getCell(8);
        cell1.setCellValue("Ptotal(kW)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getPa() == null ? 0 : item.getPa());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getPb() == null ? 0 : item.getPb());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getPc() == null ? 0 : item.getPc());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getQa() == null ? 0 : item.getQa());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getQb() == null ? 0 : item.getQb());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getQc() == null ? 0 : item.getQc());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 8, 8);
            cellData = sheet1.getRow(rowCount)
                .getCell(8);
            cellData.setCellValue(item.getPTotal() == null ? 0 : item.getPTotal());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private void createDataVoltageExcel(final List<Device> listDataF1, final List<Device> listDataF2,
        final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
        String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
        final String fileNameExcel, final String deviceName) throws Exception {
        log.info("NewReportController.createEnergyTotalExcel(): START");

        // format dateTime
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet(reportName);
        Row row;
        Cell cell;
        // set font style
        // DataFormat format = wb.createDataFormat();
        CellStyle cs = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
        for (int z = 0; z < 1500; z++) {
            row = sheet1.createRow(z);
            for (int j = 0; j < 200; j++) {
                row.createCell(j, CellType.BLANK)
                    .setCellStyle(cs);
            }
        }

        // set độ rộng của hàng
        Row row1 = sheet1.getRow(1);
        row1.setHeight((short) 1000);
        Row row2 = sheet1.getRow(4);
        row2.setHeight((short) 1000);

        // set độ rộng của cột
        sheet1.setColumnWidth(0, 5000);
        sheet1.setColumnWidth(1, 5000);
        sheet1.setColumnWidth(2, 5000);
        sheet1.setColumnWidth(3, 6000);
        sheet1.setColumnWidth(4, 6000);
        sheet1.setColumnWidth(5, 5000);
        sheet1.setColumnWidth(6, 5000);
        sheet1.setColumnWidth(7, 5000);
        sheet1.setColumnWidth(8, 5000);

        // Hàng màu xanh ses
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(0)
            .getCell(0);
        formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
        // Tên hàng khách hàng
        region = new CellRangeAddress(1, 1, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(1)
            .getCell(0);
        cell.setCellValue(customerName);
        formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Tên hàng địa chỉ
        region = new CellRangeAddress(2, 2, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(2)
            .getCell(0);
        cell.setCellValue(description);
        formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
            HorizontalAlignment.LEFT, 1);
        // Tên báo cáo
        region = new CellRangeAddress(4, 4, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(4)
            .getCell(0);
        if (typeTime == 1) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 2) {
            cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        } else if (typeTime == 3) {
            cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
            formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
        }
        // Cột ngày tạo báo cáo
        region = new CellRangeAddress(5, 5, 0, 5);
        sheet1.addMergedRegion(region);
        cell = sheet1.getRow(5)
            .getCell(0);
        cell.setCellValue("Ngày tạo: " + dateTime);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
        // Cột module
        region = new CellRangeAddress(7, 7, 0, 0);
        cell = sheet1.getRow(7)
            .getCell(0);
        cell.setCellValue("Thành phần");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột SITE
        region = new CellRangeAddress(7, 7, 1, 1);
        cell = sheet1.getRow(7)
            .getCell(1);
        cell.setCellValue("Dự án");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột Device
        region = new CellRangeAddress(7, 7, 2, 2);
        cell = sheet1.getRow(7)
            .getCell(2);
        cell.setCellValue("Điểm đo");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

        // Cột giá trị device
        region = new CellRangeAddress(8, 8, 2, 2);
        cell = sheet1.getRow(8)
            .getCell(2);
        cell.setCellValue(deviceName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột thời gian
        region = new CellRangeAddress(7, 7, 3, 3);
        cell = sheet1.getRow(7)
            .getCell(3);
        cell.setCellValue("Thời gian");
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // Cột giá trị Module
        region = new CellRangeAddress(8, 8, 0, 0);
        cell = sheet1.getRow(8)
            .getCell(0);
        cell.setCellValue(moduleName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // Cột giá trị Site
        region = new CellRangeAddress(8, 8, 1, 1);
        cell = sheet1.getRow(8)
            .getCell(1);
        cell.setCellValue(siteName);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
        // cột giá trị fromDate
        region = new CellRangeAddress(8, 8, 3, 3);
        cell = sheet1.getRow(8)
            .getCell(3);
        cell.setCellValue("Từ: " + fromDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
        // cột giá trị toDate
        region = new CellRangeAddress(8, 8, 4, 4);
        cell = sheet1.getRow(8)
            .getCell(4);
        cell.setCellValue("Đến: " + toDate);
        formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // cột giá trị null
        // region = new CellRangeAddress(12, 12, 0, 0);
        // cell = sheet1.getRow(12).getCell(0);
        // cell.setCellValue("");
        // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

        // format tiền điện
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        Cell cell1;
        // Cột THỜI GIAN
        region = new CellRangeAddress(10, 10, 0, 1);
        sheet1.addMergedRegion(region);
        cell1 = sheet1.getRow(10)
            .getCell(0);
        cell1.setCellValue("THỜI GIAN");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 2, 2);
        cell1 = sheet1.getRow(10)
            .getCell(2);
        cell1.setCellValue("Uab(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 3, 3);
        cell1 = sheet1.getRow(10)
            .getCell(3);
        cell1.setCellValue("Ubc(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 4, 4);
        cell1 = sheet1.getRow(10)
            .getCell(4);
        cell1.setCellValue("Uca(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 5, 5);
        cell1 = sheet1.getRow(10)
            .getCell(5);
        cell1.setCellValue("Uan(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 6, 6);
        cell1 = sheet1.getRow(10)
            .getCell(6);
        cell1.setCellValue("Ubn(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        region = new CellRangeAddress(10, 10, 7, 7);
        cell1 = sheet1.getRow(10)
            .getCell(7);
        cell1.setCellValue("Ucn(V)");
        formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

        int rowCount = 11;
        int count = 1;
        float lowTotal = 0;
        float normalTotal = 0;
        float highTotal = 0;
        float total = 0;
        // Thông số load % tải báo cáo

        DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Device item : listDataF1) {
            // float low_cost = 0, normal_cost = 0, high_cost = 0;
            final short bgColor;
            if (rowCount % 2 != 0) {
                bgColor = IndexedColors.WHITE.getIndex();
            } else {
                bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
            }

            // low_cost = item.getLowEp();
            // normal_cost = item.getNormalEp();
            // high_cost = item.getHighEp();
            Cell cellData;
            // Cột thời gian
            region = new CellRangeAddress(rowCount, rowCount, 0, 1);
            sheet1.addMergedRegion(region);
            cellData = sheet1.getRow(rowCount)
                .getCell(0);
            cellData.setCellValue(viewTime.format(item.getSendDate()));
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

            region = new CellRangeAddress(rowCount, rowCount, 2, 2);
            cellData = sheet1.getRow(rowCount)
                .getCell(2);
            cellData.setCellValue(item.getUab() == null ? 0 : item.getUab());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 3, 3);
            cellData = sheet1.getRow(rowCount)
                .getCell(3);
            cellData.setCellValue(item.getUbc() == null ? 0 : item.getUbc());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 4, 4);
            cellData = sheet1.getRow(rowCount)
                .getCell(4);
            cellData.setCellValue(item.getUca() == null ? 0 : item.getUca());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            cellData = sheet1.getRow(rowCount)
                .getCell(5);
            cellData.setCellValue(item.getUan() == null ? 0 : item.getUan());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 6, 6);
            cellData = sheet1.getRow(rowCount)
                .getCell(6);
            cellData.setCellValue(item.getUbn() == null ? 0 : item.getUbn());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            region = new CellRangeAddress(rowCount, rowCount, 7, 7);
            cellData = sheet1.getRow(rowCount)
                .getCell(7);
            cellData.setCellValue(item.getUcn() == null ? 0 : item.getUcn());
            formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
            // Cột tổng
            // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
            // cellData = sheet1.getRow(rowCount).getCell(5);
            // cellData.setCellValue((low_cost > 0 ? low_cost : 0) + (normal_cost > 0 ? normal_cost : 0)
            // + (high_cost > 0 ? high_cost : 0));
            // formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

            rowCount += 1;
            count += 1;
            // lowTotal += low_cost;
            // normalTotal += normal_cost;
            // highTotal += high_cost;
            // total += low_cost + normal_cost + high_cost;
        }
        ;

        // Cột TỔNG
        // region = new CellRangeAddress(rowCount, rowCount, 0, 1);
        // sheet1.addMergedRegion(region);
        // cell1 = sheet1.getRow(rowCount).getCell(0);
        // cell1.setCellValue("TỔNG");
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.CENTER, 0, "");
        // // Cột tổng giờ thấp điểm
        // region = new CellRangeAddress(rowCount, rowCount, 2, 2);
        // cell1 = sheet1.getRow(rowCount).getCell(2);
        // cell1.setCellValue(lowTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ bình thường
        // region = new CellRangeAddress(rowCount, rowCount, 3, 3);
        // cell1 = sheet1.getRow(rowCount).getCell(3);
        // cell1.setCellValue(normalTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        // // Cột tổng giờ cao điểm
        // region = new CellRangeAddress(rowCount, rowCount, 4, 4);
        // cell1 = sheet1.getRow(rowCount).getCell(4);
        // cell1.setCellValue(highTotal);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");
        //
        // // Cột tổng tiêu thụ
        // region = new CellRangeAddress(rowCount, rowCount, 5, 5);
        // cell1 = sheet1.getRow(rowCount).getCell(5);
        // cell1.setCellValue(total);
        // formatExcelTotal(wb, region, sheet1, cell1, IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex(),
        // HorizontalAlignment.RIGHT, 1, "");

        XDDFDataSource date = null;
        CellType type = CellType.ERROR;
        row = sheet1.getRow(1);
        if (row != null) {
            cell = row.getCell(0);
            if (cell != null) {
                type = cell.getCellType();
                if (type == CellType.STRING) {
                    date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.NUMERIC) {
                    date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                        new CellRangeAddress(11, rowCount - 1, 0, 0));
                } else if (type == CellType.FORMULA) {
                    type = cell.getCachedFormulaResultType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    }
                }
            }
        }

        // set data point colors
        // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
        byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
            new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
            new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

        // export file
        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Tạo file excel trong folder export
        long url = new Date().getTime();
        // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
        String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
        File file = new File(exportFilePath);
        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
        } catch (FileNotFoundException e) {
            log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
            e.printStackTrace();
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                // wb.dispose();
                wb.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        ZipUtil.pack(folder, new File(path + ".zip"));

    }

    private static String convertToCamelCase(String input) {
        // Xóa dấu và chuyển sang chữ thường
        String normalized = removeDiacriticalMarks(input).toLowerCase();

        // Chuyển đổi sang kiểu Camel Case
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : normalized.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(c);
                }

                // Nếu là khoảng trắng, đánh dấu để chuyển ký tự tiếp theo sang chữ in hoa
                if (c == ' ') {
                    capitalizeNext = true;
                }
            }
        }

        return camelCase.toString();
    }

    private static String removeDiacriticalMarks(String input) {
        // Loại bỏ dấu và chuyển về chữ thường
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacriticalMarks = pattern.matcher(normalized)
            .replaceAll("");

        // Loại bỏ dấu '-' và ':'
        return withoutDiacriticalMarks.replaceAll("[-:]", "");
    }

    private static List<DataPower> sumPower(List<DataPowerResult> listDataResults) {
        List<DataPower> result = new ArrayList<>();

        for (DataPowerResult dataResult : listDataResults) {
            List<DataPower> dataList = dataResult.getListDataPower();
            for (DataPower item : dataList) {
                DataPower existingItem = findItemByViewTime(result, item.getViewTime());

                if (existingItem != null) {
                    existingItem.setPower(existingItem.getPower() + item.getPower());
                } else {
                    result.add(new DataPower(item.getPower(), null, null, item.getViewTime()));
                }
            }
        }

        return result;
    }

    private static DataPower findItemByViewTime(List<DataPower> list, String viewTime) {
        for (DataPower item : list) {
            if (item.getViewTime()
                .equals(viewTime)) {
                return item;
            }
        }
        return null;
    }

    private void formatExcelReport(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.ORANGE.getIndex());
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 20);
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTableBody(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent, final String unit) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setIndention((short) indent);
        cs.setDataFormat(format.getFormat("0.00 " + unit));
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTotal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent, final String unit) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cs.setDataFormat(format.getFormat("###,000 " + unit));
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelTable(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setIndention((short) indent);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
    }

    private void formatExcel(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
        // cs.setDataFormat(format.getFormat("##0,##0"));
        // RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatExcelFill(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);

        // RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        // RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatExcelCustomerName(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 20);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    private void formatExcelCustomerDescription(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

        CellStyle cs = wb.createCellStyle();

        DataFormat format = wb.createDataFormat();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontName("Times New Roman");
        cs.setFont(font);
        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        // cs.setDataFormat(format.getFormat("0.000"));
        cell.setCellStyle(cs);
    }

    public double countDayOccurence(int year, int month, int dayToFindCount) {
        Calendar calendar = Calendar.getInstance();
        // Note that month is 0-based in calendar, bizarrely.
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        double count = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month - 1, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == dayToFindCount) {
                count++;
                // Or do whatever you need to with the result.
            }
        }
        return count;
    }

    @GetMapping ("/getDataDeviceGateway")
    public ResponseEntity<Device> getDataInstanceGateway(@RequestParam ("customerId") final Integer customerId,
        @RequestParam (value = "projectId", required = false) final Integer projectId,
        @RequestParam ("deviceId") final Long deviceId) {
        Map<String, Object> condition = new HashMap<>();
        String schema = Schema.getSchemas(customerId);
        condition.put("schema", schema);
        condition.put("deviceId", deviceId);
        if (projectId != null) {
            condition.put("projectId", projectId);
        }

        Device gatewayDeviceResponses = deviceService.getDataInstanceGateway(condition);
        return new ResponseEntity<Device>(gatewayDeviceResponses, HttpStatus.OK);

    }
    
    private void createDataPressureFlowExcel(final List<Device> listDataF1, final List<Device> listDataF2,
            final List<Device> listDataH, String customerName, String description, Integer typeTime, String reportName,
            String moduleName, String siteName, String fromDate, String toDate, final String dateTime, final String path,
            final String fileNameExcel, final String deviceName, final Integer option) throws Exception {
            log.info("NewReportController.createEnergyTotalExcel(): START");

            // format dateTime
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet1 = wb.createSheet(reportName);
            Row row;
            Cell cell;
            // set font style
            // DataFormat format = wb.createDataFormat();
            CellStyle cs = wb.createCellStyle();
            Font font = wb.createFont();
            font.setFontName("Times New Roman");
            cs.setFont(font);
            cs.setAlignment(HorizontalAlignment.CENTER);
            DataFormat format = wb.createDataFormat();
            cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
            for (int z = 0; z < 1500; z++) {
                row = sheet1.createRow(z);
                for (int j = 0; j < 200; j++) {
                    row.createCell(j, CellType.BLANK)
                        .setCellStyle(cs);
                }
            }

            // set độ rộng của hàng
            Row row1 = sheet1.getRow(1);
            row1.setHeight((short) 1000);
            Row row2 = sheet1.getRow(4);
            row2.setHeight((short) 1000);

            // set độ rộng của cột
            sheet1.setColumnWidth(0, 5000);
            sheet1.setColumnWidth(1, 5000);
            sheet1.setColumnWidth(2, 5000);
            sheet1.setColumnWidth(3, 6000);
            sheet1.setColumnWidth(4, 6000);
            sheet1.setColumnWidth(5, 5000);
            sheet1.setColumnWidth(6, 5000);
            sheet1.setColumnWidth(7, 5000);
            sheet1.setColumnWidth(8, 5000);

            // Hàng màu xanh ses
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(0)
                .getCell(0);
            formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
            // Tên hàng khách hàng
            region = new CellRangeAddress(1, 1, 0, 8);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(1)
                .getCell(0);
            cell.setCellValue(customerName);
            formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            // Tên hàng địa chỉ
            region = new CellRangeAddress(2, 2, 0, 8);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(2)
                .getCell(0);
            cell.setCellValue(description);
            formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.LEFT, 1);
            // Tên báo cáo
            region = new CellRangeAddress(4, 4, 0, 5);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(4)
                .getCell(0);
            if (typeTime == 1) {
                cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
                formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            } else if (typeTime == 2) {
                cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
                formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            } else if (typeTime == 3) {
                cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
                formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
            }
            // Cột ngày tạo báo cáo
            region = new CellRangeAddress(5, 5, 0, 5);
            sheet1.addMergedRegion(region);
            cell = sheet1.getRow(5)
                .getCell(0);
            cell.setCellValue("Ngày tạo: " + dateTime);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
            // Cột module
            region = new CellRangeAddress(7, 7, 0, 0);
            cell = sheet1.getRow(7)
                .getCell(0);
            cell.setCellValue("Module");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            // Cột SITE
            region = new CellRangeAddress(7, 7, 1, 1);
            cell = sheet1.getRow(7)
                .getCell(1);
            cell.setCellValue("Site");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            // Cột thời gian
            region = new CellRangeAddress(7, 7, 3, 3);
            cell = sheet1.getRow(7)
                .getCell(3);
            cell.setCellValue("Thời gian");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // Cột giá trị Module
            region = new CellRangeAddress(8, 8, 0, 0);
            cell = sheet1.getRow(8)
                .getCell(0);
            cell.setCellValue(moduleName);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            // Cột giá trị Site
            region = new CellRangeAddress(8, 8, 1, 1);
            cell = sheet1.getRow(8)
                .getCell(1);
            cell.setCellValue(siteName);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

            // Cột Device
            region = new CellRangeAddress(7, 7, 2, 2);
            cell = sheet1.getRow(7)
                .getCell(2);
            cell.setCellValue("Device");
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);

            // Cột giá trị device
            region = new CellRangeAddress(8, 8, 2, 2);
            cell = sheet1.getRow(8)
                .getCell(2);
            cell.setCellValue(deviceName);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
            // cột giá trị fromDate
            region = new CellRangeAddress(8, 8, 3, 3);
            cell = sheet1.getRow(8)
                .getCell(3);
            cell.setCellValue("Từ: " + fromDate);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
            // cột giá trị toDate
            region = new CellRangeAddress(8, 8, 4, 4);
            cell = sheet1.getRow(8)
                .getCell(4);
            cell.setCellValue("Đến: " + toDate);
            formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

            // cột giá trị null
            // region = new CellRangeAddress(12, 12, 0, 0);
            // cell = sheet1.getRow(12).getCell(0);
            // cell.setCellValue("");
            // formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

            // format tiền điện
            Locale localeVN = new Locale("vi", "VN");
            NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

            Cell cell1;
            // Cột THỜI GIAN
            region = new CellRangeAddress(10, 10, 0, 1);
            sheet1.addMergedRegion(region);
            cell1 = sheet1.getRow(10)
                .getCell(0);
            cell1.setCellValue("THỜI GIAN");
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            region = new CellRangeAddress(10, 10, 2, 2);
            cell1 = sheet1.getRow(10)
                .getCell(2);
			if(option == 15) {
				 cell1.setCellValue("Lưu lượng (m³)");
			}else if(option == 16) {
				 cell1.setCellValue("Lưu lượng tích lũy (m³)");
			}else if(option == 17) {
				 cell1.setCellValue("Áp suất (bar)");
			}
           
            formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

            int rowCount = 11;
            int count = 1;
            float lowTotal = 0;
            float normalTotal = 0;
            float highTotal = 0;
            float total = 0;
            // Thông số load % tải báo cáo

            DateFormat viewTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (Device item : listDataF1) {

                final short bgColor;
                if (rowCount % 2 != 0) {
                    bgColor = IndexedColors.WHITE.getIndex();
                } else {
                    bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
                }


                Cell cellData;

                region = new CellRangeAddress(rowCount, rowCount, 0, 1);
                sheet1.addMergedRegion(region);
                cellData = sheet1.getRow(rowCount)
                    .getCell(0);
                cellData.setCellValue(viewTime.format(item.getSendDate()));
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

                region = new CellRangeAddress(rowCount, rowCount, 2, 2);
                cellData = sheet1.getRow(rowCount)
                    .getCell(2);
            	if(option == 15) {
            	cellData.setCellValue(item.getT() == null ? 0 : item.getT());
	   			}else if(option == 16) {
	   			   cellData.setCellValue(item.getTAccumulationDay() == null ? 0 : item.getTAccumulationDay());
	   			}else if(option == 17) {
	   			   cellData.setCellValue(item.getP() == null ? 0 : item.getP());
	   			}
             
                formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");

                rowCount += 1;
                count += 1;
            }
            ;

            XDDFDataSource date = null;
            CellType type = CellType.ERROR;
            row = sheet1.getRow(1);
            if (row != null) {
                cell = row.getCell(0);
                if (cell != null) {
                    type = cell.getCellType();
                    if (type == CellType.STRING) {
                        date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.NUMERIC) {
                        date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                            new CellRangeAddress(11, rowCount - 1, 0, 0));
                    } else if (type == CellType.FORMULA) {
                        type = cell.getCachedFormulaResultType();
                        if (type == CellType.STRING) {
                            date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
                                new CellRangeAddress(11, rowCount - 1, 0, 0));
                        } else if (type == CellType.NUMERIC) {
                            date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
                                new CellRangeAddress(11, rowCount - 1, 0, 0));
                        }
                    }
                }
            }

            // set data point colors
            // // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
            byte[][] colors = new byte[][] {new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
                new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
                new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0}, new byte[] {(byte) 255, 0, 0},
                new byte[] {(byte) 255, (byte) 255, 0}, new byte[] {(byte) 255, (byte) 255, 0},
                new byte[] {(byte) 102, (byte) 205, 0}, new byte[] {(byte) 102, (byte) 205, 0}};

            // export file
            // access folder export excel
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            // Tạo file excel trong folder export
            long url = new Date().getTime();
            // String exportFilePath = path + File.separator + StringUtils.stripAccents(reportName) + ".xlsx";
            String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";
            File file = new File(exportFilePath);
            FileOutputStream outFile = null;

            try {
                outFile = new FileOutputStream(file);
                log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
            } catch (FileNotFoundException e) {
                log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
                e.printStackTrace();
            } finally {
                try {
                    wb.write(outFile);
                    outFile.close();
                    // wb.dispose();
                    wb.close();

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            ZipUtil.pack(folder, new File(path + ".zip"));

        }
}
