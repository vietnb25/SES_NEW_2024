package vn.ses.s3m.plus.controllers.pv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.Control;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.SystemMap;

@RestController
@RequestMapping ("/pv/control")
public class ControlPVController {

    @Autowired
    private LoadClient pvClient;

    /**
     * @param projectId
     * @return
     */
    @GetMapping ("/getSystem/{projectId}")
    public ResponseEntity<List<SystemMap>> getListSystemMapByProject(@PathVariable final String projectId) {
        return pvClient.getListSystemMapByProject(projectId);
    }

    /**
     *
     */
    @GetMapping ("/{projectId}")
    public ResponseEntity<?> getControl(@PathVariable final String projectId) {
        return pvClient.getControl(projectId);
    }

    /**
     *
     */
    @PostMapping ("/device")
    public ResponseEntity<?> detailControl(@RequestBody final History control) {
        return pvClient.detailControl(control);
    }

    /**
     *
     */
    @PostMapping ("/save")
    public ResponseEntity<?> saveControl(@RequestBody final List<Control> controls) {
        return pvClient.saveControl(controls);
    }

    /**
     *
     */
    @GetMapping ("/system/{systemMapId}")
    public ResponseEntity<?> getControlSystem(@PathVariable final String systemMapId) {
        return pvClient.getControlSystem(systemMapId);
    }
}
