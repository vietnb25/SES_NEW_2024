package vn.ses.s3m.plus.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.service.ControlService;

@RestController
@RequestMapping ("/pv/control")
public class ControlController {

    @Autowired
    private ControlService controlService;

    @GetMapping ("/getSystem/{projectId}")
    public ResponseEntity<List<SystemMap>> getListSystemMapByProject(@PathVariable final String projectId) {

        Map<String, String> condition = new HashMap<>();
        condition.put("projectId", projectId);
        List<SystemMap> systemMaps = controlService.getSystemMapPVByProject(condition);

        return new ResponseEntity<List<SystemMap>>(systemMaps, HttpStatus.OK);
    }

    @GetMapping ("/{projectId}")
    public ResponseEntity<?> getControl(@PathVariable final String projectId) {

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

        return new ResponseEntity<Map<String, List<History>>>(resultControl, HttpStatus.OK);
    }

    @PostMapping ("/device")
    public ResponseEntity<?> detailControl(@RequestBody final History control) {

        Integer parentId = control.getHistoryId();
        Integer projectId = control.getStt();
        String timeFrame = control.getTimeFrame();
        String time[] = timeFrame.split(" ~ ");
        Double power = control.getCongSuatTietGiam();
        String timeFrom = control.getFromDate();
        String timeTo = control.getToDate();

        Map<String, Object> conditon = new HashMap<>();
        Double congSuat = 0.0;
        conditon.put("projectId", projectId);
        congSuat = controlService.getACPowerByProjectId(conditon);
        congSuat = congSuat == null ? 0.0 : congSuat;

        conditon.put("systemType", 2);
        conditon.put("deviceType", 1);
        List<Device> deviceIds = controlService.getDevices(conditon);

        return null;
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
}
