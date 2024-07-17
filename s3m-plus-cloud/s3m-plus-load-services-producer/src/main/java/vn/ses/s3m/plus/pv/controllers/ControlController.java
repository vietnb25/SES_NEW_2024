package vn.ses.s3m.plus.pv.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.dto.Control;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SchedulePV;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.pv.service.ControlPVService;

@RestController
@Slf4j
@RequestMapping ("/pv/control")
public class ControlController {

    static String topicName = "MQTT_S3M_2_0_POWER_DATA";

    @Value ("${mqtt.server}")
    private String topicUrl;

    @Value ("${mqtt.user.name}")
    private String userName;

    @Value ("${mqtt.password}")
    private String password;

    @Autowired
    private ControlPVService controlService;

    @GetMapping ("/getSystem/{projectId}")
    public ResponseEntity<List<SystemMap>> getListSystemMapByProject(@PathVariable final String projectId) {

        log.info("getListSystemMapByProject START");
        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", projectId);
        List<SystemMap> systemMaps = controlService.getSystemMapPVByProject(condition);
        log.info("getListSystemMapByProject END");

        return new ResponseEntity<List<SystemMap>>(systemMaps, HttpStatus.OK);
    }

    @GetMapping ("/{projectId}")
    public ResponseEntity<?> getControl(@PathVariable final String projectId) {

        log.info("getControl START");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        List<History> control = new ArrayList<>();
        List<History> controlAll = new ArrayList<>();
        Map<String, String> condition = new HashMap<>();
        condition.put("stt", projectId);
        condition.put("typeScrop", "3");
        List<History> historiesAll = controlService.getHistories(condition);
        controlAll = generateListHistories(historiesAll, projectId);

        condition.put("timeview", date);
        condition.put("deleteFlag", "0");
        List<History> histories = controlService.getHistories(condition);
        control = generateListHistories(histories, projectId);

        Map<String, List<History>> resultControl = new HashMap<>();
        resultControl.put("controls", control);
        resultControl.put("controlsAll", controlAll);
        log.info("getControl END");

        return new ResponseEntity<Map<String, List<History>>>(resultControl, HttpStatus.OK);
    }

    @GetMapping ("/system/{systemMapId}")
    public ResponseEntity<?> getControlSystem(@PathVariable final String systemMapId) {

        log.info("getControlSystem START");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        String toTime = DateUtils.toString(new Date(), "HH:mm");
        List<SchedulePV> control = new ArrayList<>();
        List<SchedulePV> controlAll = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("systemMapId", systemMapId);
        List<SchedulePV> schedulesAll = controlService.getSchedules(condition);
        controlAll = generateSystemMapListHistories(schedulesAll, systemMapId);

        condition.put("typeScrop", 4);
        condition.put("timeview", date);
        condition.put("toTime", toTime);
        condition.put("deleteFlag", 0);
        List<SchedulePV> schedules = controlService.getSchedules(condition);
        control = generateSystemMapListHistories(schedules, systemMapId);

        Map<String, Object> result = new HashMap<>();
        result.put("controls", control);
        result.put("controlsAll", controlAll);
        log.info("getControlSystem END");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

    }

    @PostMapping ("/device")
    public ResponseEntity<?> detailControl(@RequestBody final History control) {

        log.info("detailControl START");
        Map<String, Object> result = new HashMap<>();
        Integer parentId = control.getHistoryId();
        Integer projectId = control.getStt();
        String timeFrame = control.getTimeFrame();
        String time[] = timeFrame.split(" ~ ");
        Double power = control.getCongSuatTietGiam() * 1000;
        String timeFrom = control.getFromDate();
        String timeTo = control.getToDate();

        Map<String, Object> condition = new HashMap<>();
        Double congSuat = 0.0;
        condition.put("projectId", projectId);
        congSuat = controlService.getACPowerByProjectId(condition);
        congSuat = congSuat == null ? 0.0 : congSuat;

        condition.put("systemType", 2);
        condition.put("deviceType", 1);
        List<Device> deviceIds = controlService.getDevices(condition);

        result.put("status", false);

        List<Control> deviceList = new ArrayList<>();
        for (Device device : deviceIds) {
            condition = new HashMap<String, Object>();
            condition.put("stt", device.getProjectId());
            condition.put("typeScrop", 4);
            condition.put("timeview", timeFrom);
            condition.put("parentId", parentId);
            condition.put("deleteFlag", 0);
            History history = controlService.getHistory(condition);
            Double csDinhMuc = device.getAcPower() != null ? (double) device.getAcPower() : 0.0;

            if (history != null) {
                Control deviceControl = new Control();
                deviceControl.setHistoryId(history.getHistoryId()
                    .toString());
                deviceControl.setDeviceId(history.getStt()
                    .toString());
                deviceControl.setDeviceName(device.getDeviceName());
                deviceControl.setCsdm(csDinhMuc / 1000);
                deviceControl.setCscp(history.getCongSuatChoPhep() / 1000);
                deviceControl.setCstg(history.getCongSuatTietGiam() / 1000);
                deviceControl.setCongSuat( (csDinhMuc - history.getCongSuatChoPhep()) / 1000);
                deviceControl.setParentId(Integer.toString(parentId));
                deviceControl.setTimeViewFrom(time[0]);
                deviceControl.setTimeViewTo(time[1]);
                deviceControl.setFromTime(timeFrom);
                deviceControl.setToTime(timeTo);

                deviceList.add(deviceControl);
                result.put("status", true);
            } else {
                Control deviceControl = new Control();
                deviceControl.setDeviceId(device.getDeviceId()
                    .toString());
                deviceControl.setDeviceName(device.getDeviceName());
                deviceControl.setCsdm(csDinhMuc / 1000);
                deviceControl.setCscp(0.0);
                double cstg = (csDinhMuc / congSuat) * power;
                deviceControl.setCstg((double) Math.round(cstg * 1000) / 1000);
                if (csDinhMuc == 0) {
                    deviceControl.setCstg(0.0);
                }
                if ( (csDinhMuc / congSuat) * power > csDinhMuc) {
                    deviceControl.setCstg(csDinhMuc / 1000);
                }
                deviceControl.setParentId(parentId.toString());
                deviceControl.setTimeViewFrom(time[0]);
                deviceControl.setTimeViewTo(time[1]);
                deviceControl.setFromTime(timeFrom);
                deviceControl.setToTime(timeTo);
                deviceControl.setCongSuat((double) 0);
                deviceList.add(deviceControl);
            }
        }
        result.put("devices", deviceList);
        log.info("detailControl END");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping ("/save")
    public ResponseEntity<?> saveControl(@RequestBody final List<Control> controls) throws Exception {

        log.info("saveControl START");
        for (Control control : controls) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("deviceId", control.getDeviceId());
            Device device = controlService.getDeviceById(condition);

            Integer congSuat = device.getAcPower() != null ? device.getAcPower() : 0;
            Long historyId = null;
            if (congSuat > control.getCscp()) {
                if (control.getHistoryId() == null || control.getHistoryId() == "") {
                    Map<String, Object> cond = new HashMap<>();
                    cond.put("fromDate", control.getTimeViewFrom());
                    cond.put("toDate", control.getTimeViewTo());
                    cond.put("timeFrame", control.getFromTime() + " ~ " + control.getToTime());
                    cond.put("congSuatChoPhep", String.valueOf((int) (control.getCscp() * 1000)));
                    cond.put("congSuatDinhMuc", String.valueOf(congSuat));
                    cond.put("congSuatTietGiam", String.valueOf((int) (congSuat - control.getCscp() * 1000)));
                    cond.put("deleteFlag", "0");
                    cond.put("typeScrop", "4");
                    cond.put("stt", control.getDeviceId());
                    cond.put("status", "0");
                    cond.put("parentId", control.getParentId());
                    controlService.addControl(cond);
                    historyId = (Long) cond.get("id");
                } else {
                    condition = new HashMap<>();
                    condition.put("congSuatChoPhep", String.valueOf((int) (control.getCscp() * 1000)));
                    condition.put("congSuatTietGiam", String.valueOf((int) (congSuat - control.getCscp() * 1000)));
                    condition.put("historyId", control.getHistoryId());
                    controlService.updateControl(condition);
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                Date fromDate = format.parse(control.getTimeViewFrom());
                Date toDate = format.parse(control.getTimeViewTo());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fromDate);

                long diff = TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS);
                if (control.getHistoryId() == null || control.getHistoryId() == "") {
                    for (int i = 0; i <= diff; i++) {
                        String date = format.format(calendar.getTime());
                        condition = new HashMap<String, Object>();
                        condition.put("stt", control.getDeviceId());
                        condition.put("addRess", device.getDeviceName());
                        condition.put("scrop", "4");
                        condition.put("congSuatChoPhep", String.valueOf(control.getCscp() * 1000));
                        condition.put("congSuatTietGiam", String.valueOf( (congSuat - control.getCscp() * 1000)));
                        condition.put("fromTime", control.getFromTime());
                        condition.put("toTime", control.getToTime());
                        condition.put("timeView", date);
                        condition.put("historyId", historyId.toString());
                        condition.put("status", "0");
                        condition.put("deleteFlag", "0");
                        condition.put("parentId", null);
                        controlService.addSchedule(condition);
                        calendar.add(Calendar.DATE, 1);
                    }
                } else {
                    condition = new HashMap<String, Object>();
                    condition.put("congSuatChoPhep", String.valueOf(control.getCscp() * 1000));
                    condition.put("congSuatTietGiam", String.valueOf( (congSuat - control.getCscp() * 1000)));
                    condition.put("historyIdAfter", control.getHistoryId());
                    condition.put("typeScrop", "4");
                    condition.put("stt", control.getDeviceId());
                    controlService.updateSchedule(condition);
                }
            }
        }

        if (controls != null && controls.size() > 0) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("historyId", controls.get(0)
                .getParentId());
            History history = controlService.getHistory(condition);
            if (history.getStatus() == 0) {
                condition = new HashMap<>();
                condition.put("id", history.getParentId()
                    .toString());
                condition.put("status", "1");
                controlService.updateSendStatusById(condition);
            }
            condition = new HashMap<>();
            condition.put("id", controls.get(0)
                .getParentId());
            condition.put("status", 1);
            controlService.updateStatus(condition);
        }

        callToPVControl();
        log.info("saveControl END");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<History> generateListHistories(List<History> histories, String projectId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        Project project = controlService.getProjectById(condition);

        condition.put("systemType", "2");
        List<Device> deviceIds = controlService.getDevicesByProjectAndSystem(condition);
        Double csDinhMuc = 0.0;
        for (Device device : deviceIds) {
            if (device.getAcPower() != null) {
                csDinhMuc += (double) device.getAcPower();
            }
        }

        for (History history : histories) {
            history.setCongSuatDinhMuc(csDinhMuc / 1000);
            history.setCongSuatTietGiam(history.getCongSuatTietGiam() / 1000);
            history.setCongSuatChoPhep( (csDinhMuc - history.getCongSuatTietGiam()) / 1000);
            history.setViTri(project.getProjectName());
            condition.put("historyId", history.getHistoryId()
                .toString());
            History schedules = controlService.getHistoryLastestById(condition);
            if (schedules != null) {
                history.setCreateDate(schedules.getTimeInsert());
            }
            String toTime = history.getTimeFrame()
                .split(" ~ ")[1];
            Date time = DateUtils.toDate(history.getToDate() + " " + toTime, "yyyy-MM-dd HH:mm");
            if (history.getStatus() == 0 && (new Date().getTime()) > time.getTime()) {
                history.setStatus(2);
            }
        }
        return histories;
    }

    public List<SchedulePV> generateSystemMapListHistories(List<SchedulePV> schedules, String systemMapId) {

        Map<String, Object> condition = new HashMap<>();

        for (SchedulePV schedule : schedules) {
            condition = new HashMap<>();
            condition.put("deviceId", schedule.getStt()
                .toString());
            Device device = controlService.getDeviceById(condition);
            if (device.getAcPower() != null) {
                schedule.setCongSuatDinhMuc((double) device.getAcPower() / 1000);
            }
            schedule.setCongSuatTietGiam(schedule.getCongSuatTietGiam() / 1000);
            schedule.setAddRess(device.getDeviceName());

            if (schedule.getId() != null) {
                condition.put("scheduleId", schedule.getId());
            }
            if (StringUtils.equals(schedule.getStatus(), "0")) {
                schedule.setStatus(null);
            }
            List<History> histories = controlService.getDeviceControl(condition);
            if (histories != null && histories.size() != 0) {
                schedule.setStatus(null);
                schedule.setStatus2(null);
            }
            for (History history : histories) {
                if (history.getStt() != null) {
                    if (history.getStt() == 1) {
                        schedule.setStatus(history.getStatus()
                            .toString());
                        schedule.setStatus2(history.getStatus()
                            .toString());
                    }
                    if (history.getStt() == 2) {
                        schedule.setStatus2(history.getStatus()
                            .toString());
                    }
                }
                schedule.setUpdateDate(history.getCreateDate());
            }
        }
        return schedules;

    }

    public void callToPVControl() {
        String content = "test receiver";
        // String content = "abc";
        // String topicUrl = "tcp://192.168.1.30:1883";
        String clientId = "C" + 1;
        // String topicUrl = "tcp://127.0.0.1:1883";
        // String topicUrl = "tcp://192.168.1.45:1883";
        // String userName = "mqtt-test";
        // String password = "mqtt-test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(topicUrl, clientId, persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());

            log.info("Connecting to broker: " + topicUrl);

            client.connect(connOpts);

            log.info("Connected");

            log.info("Publishing message: " + content);

            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);

            client.publish(topicName, message);
            log.info("Message published");

            client.disconnect();
            log.info("Disconnected");

        } catch (MqttException me) {
            log.error("reason " + me.getReasonCode());
            log.error("msg " + me.getMessage());
            log.error("loc " + me.getLocalizedMessage());
            log.error("cause " + me.getCause());
            log.error("excep " + me);
            me.printStackTrace();
        }
    }
}
