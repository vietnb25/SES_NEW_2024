package vn.ses.s3m.plus.controllers.grid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.Receiver;

@RestController
@RequestMapping ("/grid")
public class ReceiverGridController {

    @Autowired
    private LoadClient loadClient;

    /**
     * @author HoangLH
     * @since Apr 27, 2023
     * @param projectId
     * @param systemType
     * @return
     */
    @GetMapping ("/receivers")
    public ResponseEntity<?> getListReceiverGrid(@RequestParam (value = "projectId") final String projectId,
        @RequestParam (value = "systemType") final String systemType) {

        return loadClient.getListReceiverGrid(projectId, systemType);
    }

    /**
     *
     */
    @PostMapping ("/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    ResponseEntity<?> saveWarningToSentGrid(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId) {
        return loadClient.saveWarningToSentGrid(data, systemType, customerId, projectId, deviceId, receiverId);
    }

    /**
     *
     */
    @GetMapping ("/receiver/getWarning/{receiverId}/{deviceId}")
    public ResponseEntity<?> getWarningInforGrid(@PathVariable (value = "receiverId") final String receiverId,
        @PathVariable (value = "deviceId") final String deviceId) {
        return loadClient.getWarningInforGrid(receiverId, deviceId);
    }

    /**
     *
     */
    @PostMapping ("/receiver/add/{projectId}/{systemType}")
    public ResponseEntity<?> addNewReceiverGrid(@RequestBody final Receiver receiver,
        @PathVariable (value = "projectId") final String projectId,
        @PathVariable (value = "systemType") final String systemType) {

        return loadClient.addNewReceiverGrid(receiver, projectId, systemType);
    }

    /**
     *
     */
    @PostMapping ("/receiver/update")
    public ResponseEntity<?> updateReceiverGrid(@RequestBody final Receiver receiver) {

        return loadClient.updateReceiverGrid(receiver);
    }

    /**
     *
     */
    @GetMapping ("/receiver/delete")
    public ResponseEntity<?> deleteReceiverGrid(@RequestParam (value = "receiverId") final String receiverId) {

        return loadClient.deleteReceiverGrid(receiverId);
    }

}
