package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.FloatType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.form.UpdateSystemMapForm;
import vn.ses.s3m.plus.response.DeviceResponse;
import vn.ses.s3m.plus.response.DeviceTimerResponse;
import vn.ses.s3m.plus.response.SystemMapResponse;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.SystemMapService;

/**
 * Controller xử lý thông tin sơ đồ 1 sợi.
 *
 * @author Arius Vietnam JSC
 * @since 2022-12-05
 */
@RestController
@RequestMapping ("/common/systemMap")
public class SystemMapController {

    @Autowired
    private SystemMapService systemMapService;

    @Autowired
    private DeviceService deviceService;

    @Value ("${time-active-module}")
    private Long timeActiveModule;

    @GetMapping ("/all")
    public ResponseEntity<List<SystemMapResponse>> getAll() {
        List<SystemMap> systemMaps = systemMapService.getAllSystemMap();
        List<SystemMapResponse> systemMapResponses = new ArrayList<>();
        if (systemMaps.size() > 0) {
            for (SystemMap systemMap : systemMaps) {
                SystemMapResponse systemMapResponse = new SystemMapResponse(systemMap);
                systemMapResponses.add(systemMapResponse);
            }
        }
        return new ResponseEntity<List<SystemMapResponse>>(systemMapResponses, HttpStatus.OK);
    }

    /**
     * Lấy danh sách sơ đồ 1 sợi theo id dự án và id kiểu thiết bị.
     *
     * @param projectId String id dự án
     * @param systemTypeId String id kiểu thiết bị
     * @return ResponseEntity<List<SystemMapResponse>> Danh sách sơ đồ 1 sợi
     */
    @GetMapping ("/list/{projectId}/{systemTypeId}")
    public ResponseEntity<List<SystemMapResponse>> getListSystemMapByProjectIdAndSystemTypeId(
        @PathVariable ("projectId") String projectId, @PathVariable ("systemTypeId") String systemTypeId) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);

        List<SystemMap> systemMaps = systemMapService.getListSystemMapByProjectIdAndSystemTypeId(condition);
        List<SystemMapResponse> systemMapResponses = new ArrayList<>();
        if (systemMaps.size() > 0) {
            for (SystemMap systemMap : systemMaps) {
                SystemMapResponse systemMapResponse = new SystemMapResponse(systemMap);
                systemMapResponses.add(systemMapResponse);
            }
        }
        return new ResponseEntity<List<SystemMapResponse>>(systemMapResponses, HttpStatus.OK);
    }

    /**
     * Thêm mới sơ đồ 1 sợi.
     *
     * @param systemMap SystemMap sơ đồ 1 sợi
     */
    @PostMapping ("/add")
    public ResponseEntity<?> addSystemMap(@RequestBody SystemMap systemMap) {
        systemMapService.addSystemMap(systemMap);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }
    
    /**
     * Cập nhật sơ đồ 1 sợi.
     *
     * @param systemMap SystemMap sơ đồ 1 sợi
     */
    @PostMapping ("/updateSystemDevice")
    public ResponseEntity<?> updateSystemMapDevice(@RequestBody UpdateSystemMapForm updateForm) {
    	SystemMap systemMap = updateForm.getSystemMap();
    	systemMapService.updateSystemMap(systemMap);
    	return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * Cập nhật sơ đồ 1 sợi.
     *
     * @param systemMap SystemMap sơ đồ 1 sợi
     */
    @PostMapping ("/update")
    public ResponseEntity<?> updateSystemMap(@RequestBody UpdateSystemMapForm updateForm) {

        SystemMap systemMap = updateForm.getSystemMap();

        List<Integer> idsNew = new ArrayList<>();

        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId", String.valueOf(systemMap.getProjectId()));
        condition.put("systemTypeId", String.valueOf(systemMap.getSystemTypeId()));
        List<Device> devices = deviceService.getDevicesEmpty(condition);

        List<Integer> ids = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        String deviceUpdateList = updateForm.getListDeviceUpdate();
        
        if (deviceUpdateList != null) {
            List<String> participantJsonDeviceList;
            try {
                participantJsonDeviceList = mapper.readValue(deviceUpdateList, new TypeReference<List<String>>() {
                });
                for (int i = 0; i < participantJsonDeviceList.size(); i++) {
                    int id = Integer.parseInt(participantJsonDeviceList.get(i));
                    idsNew.add(id);
                }
            } catch (Exception e) {

            }
        }

        for (int i = 0; i < devices.size(); i++) {
            int id = devices.get(i)
                .getDeviceId()
                .intValue();
            ids.add(id);
        }
        ;

        List<Integer> idsAdd = idsNew.stream()
            .filter(element -> !ids.contains(element))
            .collect(Collectors.toList());

        List<Integer> idsRemove = ids.stream()
            .filter(element -> !idsNew.contains(element))
            .collect(Collectors.toList());

        for (int i = 0; i < idsAdd.size(); i++) {
            Device device = new Device();
            device.setDeviceId(Long.valueOf(idsAdd.get(i)));
            device.setSystemMapId(null);
            deviceService.updateDeviceTool(device);
        }
        ;

        for (int i = 0; i < idsRemove.size(); i++) {
            Device device = new Device();
            device.setDeviceId(Long.valueOf(idsRemove.get(i)));
            device.setSystemMapId(systemMap.getId());
            deviceService.updateDeviceTool(device);
        }
        ;

        systemMapService.updateSystemMap(systemMap);

        if (updateForm.getSystemMap()
            .getLayer() == 1) {
            if (updateForm.getDeviceJsonList() == null || updateForm.getDeviceJsonList()
                .equals("")) {
                deviceService.removeDeviceCaculator(null, updateForm.getSystemMap()
                    .getId());
            } else {
                String[] deviceIds = updateForm.getDeviceJsonList()
                    .trim()
                    .split(",");
                deviceService.setDeviceCaculator(deviceIds);
                deviceService.removeDeviceCaculator(deviceIds, updateForm.getSystemMap()
                    .getId());
            }
            deviceService.removeDeviceCaculatorEmpty();
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * Xóa sơ đồ 1 sợi.
     */
    @GetMapping ("/delete/{systemMapId}")
    public ResponseEntity<?> deleteSystemMap(@PathVariable ("systemMapId") String systemMapId) {
        deviceService.setDeviceEmpty(Integer.parseInt(systemMapId));
        systemMapService.deleteSystemMap(systemMapId);
        deviceService.removeDeviceCaculatorEmpty();
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị chưa được gán vào sơ đồ 1 sợi.
     *
     * @param projectId String id dự án
     * @param systemTypeId String id kiểu thiết bị
     * @return ResponseEntity<List<DeviceResponse>> Danh sách thiết bị
     */
    @GetMapping ("/deviceEmpty/{projectId}/{systemTypeId}")
    public ResponseEntity<List<DeviceResponse>> getDevicesEmpty(@PathVariable ("projectId") String projectId,
        @PathVariable ("systemTypeId") String systemTypeId) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);
        List<Device> devicesEmpty = deviceService.getDevicesEmpty(condition);
        List<DeviceResponse> devicesEmptyResponse = new ArrayList<>();
        if (devicesEmpty.size() > 0) {
            for (Device device : devicesEmpty) {
                DeviceResponse deviceResponse = new DeviceResponse(device);
                devicesEmptyResponse.add(deviceResponse);
            }
        }
        return new ResponseEntity<List<DeviceResponse>>(devicesEmptyResponse, HttpStatus.OK);
    }

    @GetMapping ("/{systemMapId}")
    public ResponseEntity<SystemMapResponse> getSystemMap(@PathVariable ("systemMapId") String systemMapId) {
        SystemMap systemMap = systemMapService.getSystemMapById(Integer.valueOf(systemMapId));
        SystemMapResponse systemMapResponse = new SystemMapResponse(systemMap);

        return new ResponseEntity<SystemMapResponse>(systemMapResponse, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị đã được gán vào sơ đồ 1 sợi.
     *
     * @param projectId String id dự án
     * @param systemTypeId String id kiểu thiết bị
     * @return ResponseEntity<List<DeviceResponse>> Danh sách thiết bị
     */
    @GetMapping ("/deviceAlReady/{projectId}/{systemTypeId}")
    public ResponseEntity<List<DeviceResponse>> getDevicesAlReady(@PathVariable ("projectId") String projectId,
        @PathVariable ("systemTypeId") String systemTypeId) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("projectId", projectId);
        condition.put("systemTypeId", systemTypeId);
        List<Device> devicesEmpty = deviceService.getDevicesAlReady(condition);
        List<DeviceResponse> devicesEmptyResponse = new ArrayList<>();
        if (devicesEmpty.size() > 0) {
            for (Device device : devicesEmpty) {
                DeviceResponse deviceResponse = new DeviceResponse(device);
                devicesEmptyResponse.add(deviceResponse);
            }
        }
        return new ResponseEntity<List<DeviceResponse>>(devicesEmptyResponse, HttpStatus.OK);
    }

    /**
     * Lấy thông số tức thời của thiết bị trên sơ đồ 1 sợi.
     *
     * @param idList String danh sách id thiết bị
     * @return ResponseEntity<List<DeviceTimerResponse>> Danh sách thống số tức thời thiết bị
     */
    @GetMapping ("/getDataJson")
    public ResponseEntity<List<DeviceTimerResponse>> getDataJson(@RequestParam String idList,
        @RequestParam String customerId) {
        List<DeviceTimerResponse> response = new ArrayList<>();
        String[] deviceIds = idList.trim()
            .split(",");
        String schema = Schema.getSchemas(Integer.valueOf(customerId));
       
        List<Device> deviceList = deviceService.getIds(deviceIds, schema);
       
        String meterData1 = "";
        String meterData2 = "";
        String meterData3 = "";
        for (int i = 0; i < deviceList.size(); i++) {
        	
            Device device = deviceList.get(i);
            HashMap<String, String> condition = new HashMap<String, String>();
            condition.put("deviceId", device.getDeviceId().toString());
            condition.put("customerId", customerId);

            Device deviceIn = deviceService.getInfoDevice(condition);
            Float loadType = null;
            if(deviceIn.getIn() != null) {
            	loadType = 
            	((device.getIa() != null ? device.getIa() : 0)
            	+ (device.getIb() != null ? device.getIb() : 0)
            	+ (device.getIc() != null ? device.getIc() : 0))
            			/ (deviceIn.getIn()* 3) * 100;
            	loadType = (float) Math.round(loadType * 100) / 100;
            }
            
            String deviceStatus = String.valueOf(device.getStatus());

            if (Calendar.getInstance()
                .get(Calendar.MILLISECOND) - timeActiveModule > device.getSendDate()
                    .getTime()) {
                deviceStatus = "-1";
            }

            meterData1 = meterData1 + String.valueOf(device.getDeviceId()) + ":" + String.valueOf(device.getUan()) + "*"
                + String.valueOf(device.getIa()) + "*" + String.valueOf(device.getPa()) + "*"
                + String.valueOf(device.getEp()) + "*" + String.valueOf(device.getT1()) + "*" + "1" + "*" + deviceStatus
                + "*" + String.valueOf(device.getDeviceType()) + "*" + String.valueOf(device.getPpvPhA()) + "*"
                + String.valueOf(device.getAPhaA()) + "*" + String.valueOf(device.getW()) + "*"
                + String.valueOf(device.getT()) + "*" + String.valueOf(device.getH()) + "*"
                + String.valueOf(device.getIndicator()) + "*" + String.valueOf(device.getVa()) + "*"
                + String.valueOf(device.getIaI()) + "*" + String.valueOf(device.getVdcCombiner()) + "*"
                + String.valueOf(device.getIdcCombiner()) + "*" + String.valueOf(device.getVdcStr()) + "*"
                + String.valueOf(device.getIdcStr()) + "*" + String.valueOf(device.getU()) + "*"
                + String.valueOf(device.getI()) + "*" + String.valueOf(device.getTEMP()) + "*"
                + String.valueOf(device.getH()) + "*" + String.valueOf(device.getRad()) + "*"
                + String.valueOf(device.getPdcCombiner()) + "*" + String.valueOf(device.getInDCPR()) + "*"
                + String.valueOf(device.getP()) + "*" + String.valueOf(device.getFs()) + "*" + String.valueOf(loadType);

            if (i < (deviceList.size() - 1)) {
                meterData1 = meterData1 + " ";
            }

            meterData2 = meterData2 + String.valueOf(device.getDeviceId()) + ":" + String.valueOf(device.getUbn()) + "*"
                + String.valueOf(device.getIb()) + "*" + String.valueOf(device.getPb()) + "*"
                + String.valueOf(device.getEp()) + "*" + String.valueOf(device.getT2()) + "*" + "1" + "*" + deviceStatus
                + "*" + String.valueOf(device.getDeviceType()) + "*" + String.valueOf(device.getPpvPhB()) + "*"
                + String.valueOf(device.getAPhaB()) + "*" + String.valueOf(device.getW()) + "*"
                + String.valueOf(device.getT()) + "*" + String.valueOf(device.getH()) + "*"
                + String.valueOf(device.getIndicator()) + "*" + String.valueOf(device.getVb()) + "*"
                + String.valueOf(device.getIbI()) + "*" + String.valueOf(device.getVdcCombiner()) + "*"
                + String.valueOf(device.getIdcCombiner()) + "*" + String.valueOf(device.getVdcStr()) + "*"
                + String.valueOf(device.getIdcStr()) + "*" + String.valueOf(device.getU()) + "*"
                + String.valueOf(device.getI()) + "*" + String.valueOf(device.getTEMP()) + "*"
                + String.valueOf(device.getH()) + "*"
                + String.valueOf(device.getRad() + "*" + String.valueOf(device.getPdcCombiner())) + "*"
                + String.valueOf(device.getInDCPR()) + "*"
                + String.valueOf(device.getP()) + "*" + String.valueOf(device.getFs()) + "*" + String.valueOf(loadType);;

            if (i < (deviceList.size() - 1)) {
                meterData2 = meterData2 + " ";
            }

            meterData3 = meterData3 + String.valueOf(device.getDeviceId()) + ":" + String.valueOf(device.getUcn()) + "*"
                + String.valueOf(device.getIc()) + "*" + String.valueOf(device.getPc()) + "*"
                + String.valueOf(device.getEp()) + "*" + String.valueOf(device.getT3()) + "*" + "1" + "*" + deviceStatus
                + "*" + String.valueOf(device.getDeviceType()) + "*" + String.valueOf(device.getPpvPhC()) + "*"
                + String.valueOf(device.getAPhaC()) + "*" + String.valueOf(device.getW()) + "*"
                + String.valueOf(device.getT()) + "*" + String.valueOf(device.getH()) + "*"
                + String.valueOf(device.getIndicator()) + "*" + String.valueOf(device.getVc()) + "*"
                + String.valueOf(device.getIcI()) + "*" + String.valueOf(device.getVdcCombiner()) + "*"
                + String.valueOf(device.getIdcCombiner()) + "*" + String.valueOf(device.getVdcStr()) + "*"
                + String.valueOf(device.getIdcStr()) + "*" + String.valueOf(device.getU()) + "*"
                + String.valueOf(device.getI()) + "*" + String.valueOf(device.getTEMP()) + "*"
                + String.valueOf(device.getH()) + "*" + String.valueOf(device.getRad()) + "*"
                + String.valueOf(device.getPdcCombiner()) + "*" + String.valueOf(device.getInDCPR()) + "*"
                + String.valueOf(device.getP()) + "*" + String.valueOf(device.getFs()) + "*" + String.valueOf(loadType);;

            if (i < (deviceList.size() - 1)) {
                meterData3 = meterData3 + " ";
            }
        }

        DeviceTimerResponse item1 = new DeviceTimerResponse();
        item1.setType(1);
        item1.setData(meterData1);
        response.add(item1);

        DeviceTimerResponse item2 = new DeviceTimerResponse();
        item2.setType(2);
        item2.setData(meterData2);
        response.add(item2);

        DeviceTimerResponse item3 = new DeviceTimerResponse();
        item3.setType(3);
        item3.setData(meterData3);
        response.add(item3);

        return new ResponseEntity<List<DeviceTimerResponse>>(response, HttpStatus.OK);
    };

    /**
     * Lấy thông tin thời gian trên sơ đồ 1 sợi.
     *
     * @param idList String danh sách id thiết bị
     * @return ResponseEntity<String> Thông tin thời gian
     */
    @GetMapping ("/getSystemInfoTime")
    public ResponseEntity<String> getSystemInfoTime(@RequestParam String idList, @RequestParam String customerId) {
        String[] deviceIds = idList.trim()
            .split(",");

        String deviceTime = "";

        String schema = Schema.getSchemas(Integer.valueOf(customerId));

        if (deviceService.getIds(deviceIds, schema)
            .size() > 0) {
            deviceTime = deviceService.getIds(deviceIds, schema)
                .get(0)
                .getSendDate()
                .toString();
        }

        return new ResponseEntity<String>(deviceTime, HttpStatus.OK);
    }

    @GetMapping ("/getSystemMapByProjectId")
    public ResponseEntity<List<SystemMapResponse>> getSystemMapByProjectId() {
        List<SystemMap> systemMaps = systemMapService.getSystemMapByProjectId();
        List<SystemMapResponse> systemMapResponses = new ArrayList<>();
        if (systemMaps.size() > 0) {
            for (SystemMap systemMap : systemMaps) {
                SystemMapResponse systemMapResponse = new SystemMapResponse(systemMap);
                systemMapResponses.add(systemMapResponse);
            }
        }
        return new ResponseEntity<List<SystemMapResponse>>(systemMapResponses, HttpStatus.OK);
    }
}
