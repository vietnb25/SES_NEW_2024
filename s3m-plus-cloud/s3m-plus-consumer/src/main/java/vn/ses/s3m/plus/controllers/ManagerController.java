package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.dto.SystemUser;
import vn.ses.s3m.plus.form.ManagerForm;
import vn.ses.s3m.plus.response.ManagerResponse;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.SuperManagerService;
import vn.ses.s3m.plus.service.UserService;

/**
 * Controller xử lý tỉnh thành.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@RestController
@RequestMapping ("/common/manager")
@Validated
@Slf4j
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private SuperManagerService superManagerService;

    @Autowired
    private UserService userSerivce;

    /**
     * Lấy danh sách tỉnh thành khi người dùng đăng nhập.
     *
     * @param username Tên khi người dùng đăng nhập.
     * @return Danh sách tỉnh thành được lấy ra.
     */
    @GetMapping ("/list/{usernameLogin}")
    public ResponseEntity<List<Manager>> getListManager(@PathVariable ("usernameLogin") final String username) {

        log.info("ManagerController.getListManager START");

        SystemUser user = userSerivce.getUser(username);
        Map<String, String> condition = new HashMap<>();
        Integer superManagerId = user.getManagerId();
        if (superManagerId != null) {
            condition.put("superManagerId", String.valueOf(user.getManagerId()));
        }
        List<Manager> managers = managerService.getManagers(condition);

        log.info("END");

        return new ResponseEntity<List<Manager>>(managers, HttpStatus.OK);
    }

    /**
     * Lấy ra danh sách tỉnh thành theo khu vực/miền.
     *
     * @param superManagerId Mã khu vực.
     * @return Danh sách tỉnh thành được lấy ra.
     */
    @GetMapping ("/list/{usernameLogin}/{superManagerId}")
    public ResponseEntity<List<Manager>> getManagerBySuperManager(
        @PathVariable ("superManagerId") final Integer superManagerId) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.getManagerBySuperManager START");

        Map<String, String> condition = new HashMap<>();
        condition.put("superManagerId", String.valueOf(superManagerId));
        List<Manager> managers = managerService.getManagers(condition);

        log.info("END");

        return new ResponseEntity<List<Manager>>(managers, HttpStatus.OK);
    }

    /**
     * Lấy ra danh sách tỉnh thành.
     *
     * @return Danh sách tỉnh thành.
     */
    @GetMapping ("/list")
    public ResponseEntity<List<ManagerResponse>> getListManager() {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.getListManager START");
        List<Manager> list = managerService.getListManager();

        for (Manager manager : list) {
            if (manager.getSuperManagerId() != null) {
                SuperManager sm = superManagerService.getSuperManagerById((long) manager.getSuperManagerId());
                manager.setSuperManagerName(sm.getSuperManagerName());
            }
        }

        List<ManagerResponse> managerResponses = new ArrayList<>();
        for (Manager manager : list) {
            ManagerResponse managers = new ManagerResponse(manager);

            managerResponses.add(managers);
        }

        log.info("END");

        return new ResponseEntity<List<ManagerResponse>>(managerResponses, HttpStatus.OK);
    }

    /**
     * Thêm mới thông tin tỉnh thành.
     *
     * @param manager Đối tượng tỉnh thành nhập vào.
     * @return Đối tượng manager được thêm vào.
     */
    @PostMapping ("/add")
    public ResponseEntity<?> addManager(@Valid @RequestBody final ManagerForm managerForm) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.addManager START");
        List<String> errors = new ArrayList<>();
        Manager managerByManagerName = managerService.getManagerByManagerName(managerForm.getManagerName());
        if (managerByManagerName != null) {
            errors.add(Constants.ManagerValidation.MANAGER_NAME_EXIST);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        Manager manager = new Manager();
        BeanUtils.copyProperties(managerForm, manager);
        managerService.addManager(manager);

        log.info("END");

        return new ResponseEntity<Manager>(manager, HttpStatus.OK);
    }

    /**
     * Lấy ra thông tin tỉnh thành theo id.
     *
     * @param managerId Mã tỉnh thành.
     * @return Đối tượng manager được lấy ra theo id.
     */
    @GetMapping ("/{managerId}")
    public ResponseEntity<Manager> getManagerById(@PathVariable ("managerId") final int managerId) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.getManagerById START");

        Manager mg = managerService.getManagerById(managerId);

        log.info("END");
        return new ResponseEntity<Manager>(mg, HttpStatus.OK);
    }

    /**
     * Chỉnh sửa thông tin tỉnh thành.
     *
     * @param managerId Mã tỉnh thành.
     * @param manager Đối tượng cáp chỉnh sửa.
     * @return đối tượng tỉnh thành được chỉnh sửa.
     */
    @PutMapping ("/edit/{managerId}")
    public ResponseEntity<Manager> editManager(@Valid @PathVariable ("managerId") final int managerId,
        @RequestBody final ManagerForm managerForm) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.editManager START");

        Manager manager = new Manager();
        BeanUtils.copyProperties(managerForm, manager);

        Manager mag = managerService.getManagerById(managerId);

        mag.setManagerId(manager.getManagerId());
        mag.setManagerCode(manager.getManagerCode());
        mag.setManagerName(manager.getManagerName());
        mag.setSuperManagerId(manager.getSuperManagerId());
        mag.setLatitude(manager.getLatitude());
        mag.setLongitude(manager.getLongitude());
        mag.setDescription(manager.getDescription());

        managerService.updateManager(mag);

        log.info("END");

        return new ResponseEntity<Manager>(manager, HttpStatus.OK);
    }

    /**
     * Xóa thông tin tỉnh thành.
     *
     * @param managerId Mã tỉnh thành.
     * @return Trả về mã trạng thái (200: thành công), (400: thất bại).
     */
    @DeleteMapping ("/delete/{managerId}")
    public ResponseEntity<?> deleteManager(@Valid @PathVariable ("managerId") final int managerId) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.deleteManager START");
        List<String> errors = new ArrayList<>();
        Manager managerByManagerId = managerService.getManagerById(managerId);
        if (managerByManagerId.getSuperManagerId() != null) {
            errors.add(Constants.ManagerValidation.MANAGER_NAME_DEPENDENT);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        managerService.deleteManager(managerId);

        log.info("END");

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Tìm kiếm thông tin tỉnh thành.
     *
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách tỉnh thành được tìm kiếm.
     */
    @GetMapping ("/search")
    public ResponseEntity<List<ManagerResponse>> searchManager(@RequestParam ("keyword") final String keyword) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.searchManager START");

        List<Manager> list = managerService.getListManager();
        for (Manager manager : list) {
            if (manager.getSuperManagerId() != null) {
                SuperManager sm = superManagerService.getSuperManagerById((long) manager.getSuperManagerId());
                manager.setSuperManagerName(sm.getSuperManagerName());
            }
        }

        List<Manager> data = new ArrayList<>();
        for (Manager mn : list) {
            if (mn.getManagerName() != null && mn.getManagerName()
                .contains(keyword)) {
                data.add(mn);
            } else if (mn.getSuperManagerName() != null && mn.getSuperManagerName()
                .contains(keyword)) {
                data.add(mn);
            } else if (mn.getDescription() != null && mn.getDescription()
                .contains(keyword)) {
                data.add(mn);
            }
        }

        List<ManagerResponse> managerResponses = new ArrayList<>();
        for (Manager manager : data) {
            ManagerResponse managers = new ManagerResponse(manager);
            managerResponses.add(managers);
        }

        log.info("END");

        return new ResponseEntity<List<ManagerResponse>>(managerResponses, HttpStatus.OK);
    }

    /**
     * Lấy danh sách tỉnh theo theo khách hàng.
     *
     * @param customerId Mã khách hàng.
     * @return Danh sách tỉnh thành được lấy ra theo khách hàng.
     */
    @GetMapping ("listManager/{customerId}")
    public ResponseEntity<List<Manager>> getManagerByCustomerId(@PathVariable ("customerId") final int customerId) {

        log.info("vn.ses.s3m.plus.controllers.ManagerController.getManagerByCustomerId START");

        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        List<Manager> lisManagers = managerService.getManagerByCustomerId(condition);

        log.info("END");

        return new ResponseEntity<List<Manager>>(lisManagers, HttpStatus.OK);
    }

}
