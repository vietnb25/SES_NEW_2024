package vn.ses.s3m.plus.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ses.s3m.plus.dto.SettingCost;
import vn.ses.s3m.plus.form.SettingCostForm;
import vn.ses.s3m.plus.service.SettingCostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

@RestController
@RequestMapping("common/setting-cost")
public class SettingCostController {
    @Autowired
    private SettingCostService service;

    @GetMapping("/list-by-project")
    public ResponseEntity<List<SettingCost>> getListByProject(
            @RequestParam("project") String project
    ) {
        Map<String, Object> con = new HashMap<>();
        con.put("project", project);
        List<SettingCost> ls = this.service.getListByProject(con);
        return new ResponseEntity<>(ls, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?>  update(@RequestBody SettingCostForm st) {
        Map<String, Object> con = new HashMap<>();
        con.put("project", st.getProjectId());
        List<SettingCost> ls = this.service.getListByProject(con);
        this.service.insertHistoryNew(st);
        for (int i = 0; i < 4 ; i++) {
            if(i == 2) {
                con.put("settingValue", st.getNonPeakHour() );
                con.put("stId", (i+ 1));
                this.service.update(con);
            }
            if(i == 1) {
                con.put("settingValue", st.getNormalHour());
                con.put("stId", (i+ 1));
                this.service.update(con);
            }
            if(i == 0) {
                con.put("settingValue", st.getPeakHour());
                con.put("stId", (i+ 1));
                this.service.update(con);
            }
            if(i == 3) {
                con.put("settingValue", st.getVat());
                con.put("stId", (i+ 1));
                this.service.update(con);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
