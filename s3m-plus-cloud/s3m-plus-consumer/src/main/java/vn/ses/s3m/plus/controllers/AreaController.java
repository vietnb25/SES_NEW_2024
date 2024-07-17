package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.form.AreaForm;
import vn.ses.s3m.plus.response.AreaResponse;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.UserService;

/**
 * Xử lý về danh sách quận huyện.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@Validated
@RestController
@RequestMapping ("/common/area")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    /** Logging */
    private final Log log = LogFactory.getLog(AreaController.class);

    /**
     * Lấy ra danh sách quận huyện theo tên người dùng .
     *
     * @param username Tên đăng nhập.
     * @return Danh sách quận huyện và tỉnh thành, 200 (Lấy danh sách thành công).
     */
    @SuppressWarnings ("rawtypes")
    @GetMapping ("/list/{usernameLogin}")
    public ResponseEntity<Map<String, List>> getListArea(@Valid @PathVariable ("usernameLogin") final String username) {

        log.info("AreaController.getListArea: start!");
        User user = userService.getUserByUsername(username);
        List<Area> areas = areaService.getListArea();
        Map<String, String> condition = new HashMap<String, String>();
        if (user.getSuperManagerId() != null) {
            condition.put("superManagerId", String.valueOf(user.getSuperManagerId()));
        }
        List<Manager> managers = managerService.getListManager();
        Map<String, List> data = new HashMap<>();
        data.put("areas", areas);
        data.put("managers", managers);
        log.info("AreaController.getListArea: end!");
        return new ResponseEntity<Map<String, List>>(data, HttpStatus.OK);
    }

    /**
     * Lấy ra danh sách quận huyện theo mã tỉnh thành.
     *
     * @param user Thông tin người dùng
     * @param managerId Mã của tỉnh thành.
     * @return Danh sách quận huyện, 200 (Lấy danh sách quận huyện thành công).
     */

    @GetMapping ("/list/{usernameLogin}/{managerId}")
    public ResponseEntity<List<Area>> getAreasByManagerId(@Valid @PathVariable ("managerId") final String managerId) {

        log.info("AreaController.getAreasByManagerId: start!");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("managerId", String.valueOf(managerId));

        List<Area> areas = areaService.getAreas(condition);

        log.info("AreaController.getAreasByManagerId: end!");
        return new ResponseEntity<List<Area>>(areas, HttpStatus.OK);
    }

    /**
     * Thêm mới một quận huyện.
     *
     * @param areaForm Thông tin quận huyện.
     * @return Thông tin quận huyện, 200(Thêm mới quận huyện thành công).
     */
    @PostMapping ("/add")
    public ResponseEntity<AreaResponse> addArea(@Valid @RequestBody final AreaForm areaForm) {

        log.info("AreaController.addArea: start!");
        AreaResponse a = new AreaResponse();
        a.setAreaName(areaForm.getAreaName());
        a.setLongitude(areaForm.getLongitude());
        a.setLatitude(areaForm.getLatitude());
        a.setDescription(areaForm.getDescription());
        a.setManagerId(areaForm.getManagerId());

        areaService.addArea(a);

        log.info("AreaController.addArea: end!");
        return new ResponseEntity<AreaResponse>(a, HttpStatus.OK);
    }

    /**
     * Chỉnh sửa quận huyện.
     *
     * @param areaForm Thông tin quận huyện.
     * @param id Mã quận huyện
     * @return Thông tin quận huyện, 200(Cập nhật quận huyện thành công).
     */

    @PutMapping ("/update/{areaId}")
    public ResponseEntity<AreaResponse> editArea(@Valid @RequestBody final AreaForm areaForm,
        @PathVariable ("areaId") final int id) {

        log.info("AreaController.editArea: start!");
        AreaResponse a = new AreaResponse();
        a.setAreaName(areaForm.getAreaName());
        a.setManagerId(areaForm.getManagerId());
        a.setLongitude(areaForm.getLongitude());
        a.setLatitude(areaForm.getLatitude());
        a.setDescription(areaForm.getDescription());
        a.setAreaId(id);

        areaService.editArea(a);

        log.info("AreaController.editArea: end!");
        return new ResponseEntity<AreaResponse>(a, HttpStatus.OK);
    }

    /**
     * Tìm kiếm area theo từ khóa của người dùng.
     *
     * @param keyword Từ khóa tìm kiếm theo tên quận huyện, tỉnh thành và mô tả.
     * @return Trả về những quận huyện có từ khóa phù hợp, 200(Lấy thông tin quận huyện thành công).
     */

    @GetMapping ("/search")
    public ResponseEntity<List<AreaResponse>> searchArea(@RequestParam final String keyword) {

        log.info("AreaController.searchArea: start!");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("keyword", keyword);

        List<AreaResponse> area = areaService.searchArea(condition);

        log.info("AreaController.searchArea: end!");
        return new ResponseEntity<List<AreaResponse>>(area, HttpStatus.OK);
    }

    /**
     * Lấy thông tin quận huyện theo mã quận huyện.
     *
     * @param areaId Mã quận huyện.
     * @return Danh sách quận huyện, 200(Lấy thông tin quận huyện thành công).
     */
    @GetMapping ("/{areaId}")
    public ResponseEntity<Area> getAreaById(@PathVariable final String areaId) {

        log.info("AreaController.getAreaById: start!");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("areaId", areaId);

        Area area = areaService.getArea(condition);

        log.info("AreaController.getAreaById: end!");
        return new ResponseEntity<Area>(area, HttpStatus.OK);
    }

    /**
     * Xóa thông tin quận huyện theo mã quận huyện.
     *
     * @param areaId Mã quận huyện.
     * @return 400(Xóa thông tin quận huyện thất bại), 200(Xóa thông tin quận huyện thành công).
     */
    @DeleteMapping ("/delete/{areaId}")
    public ResponseEntity<Void> deleteArea(@PathVariable final int areaId) {

        log.info("AreaController.deleteArea: start!");
        List<Area> checkDependentUser = areaService.checkDependentAreaByProject(areaId);
        List<Area> checkDependentProject = areaService.checkDependentAreaByProject(areaId);
        // check dependent delete area
        if (checkDependentUser.size() > 0 || checkDependentProject.size() > 0) {
            log.info("AreaController.deleteArea: delete error !");
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else {
            areaService.deleteArea(areaId);
            log.info("AreaController.deleteArea: end!");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }

    }

    /**
     * Lấy thông tin quận huyện theo mã khách hàng.
     *
     * @param customerId Mã khách hàng.
     * @return Danh sách quận huyện, 200(lấy thông tin quận huyện thành công).
     */
    @GetMapping ("/getAreaByCustomerId/{customerId}")
    public ResponseEntity<List<AreaResponse>> getAreaByCustomerId(@PathVariable final int customerId) {

        log.info("AreaController.getAreaByCustomerId: start!");
        List<AreaResponse> area = areaService.getAreaByCustomerId(customerId);

        log.info("AreaController.getAreaByCustomerId: end!");
        return new ResponseEntity<List<AreaResponse>>(area, HttpStatus.OK);
    }

    /**
     * Lấy thông tin quận huyện theo khách hàng va tinh thanh.
     *
     * @param customerId Mã khách hàng.
     * @return Danh sách quận huyện, 200(lấy thông tin quận huyện thành công).
     */
    @GetMapping ("/getAreaByCustomerIdAndManagerId/{customerId}/{managerId}")
    public ResponseEntity<List<Area>> getAreaByCustomerIdAndManagerId(@PathVariable final int customerId,
        @PathVariable final int managerId) {

        log.info("AreaController.getAreaByCustomerId: start!");

        Map<String, Object> map = new HashMap<>();
        map.put("customerId", customerId);
        map.put("managerId", managerId);
        List<Area> area = areaService.getAreaByCustomerIdAndManagerId(map);

        log.info("AreaController.getAreaByCustomerId: end!");
        return new ResponseEntity<List<Area>>(area, HttpStatus.OK);
    }

    /**
     * Lấy thông tin quận huyện theo mã dự án.
     *
     * @param projectId Mã dự án.
     * @return Danh sách quận huyện, 200(lấy thông tin quận huyện thành công).
     */
    @GetMapping ("/getAreaByProjectId/{projectId}")
    public ResponseEntity<List<Area>> getAreaByProjectId(@PathVariable final int projectId) {

        log.info("AreaController.getAreaByProjectId: start!");
        List<Area> area = areaService.getAreaByProjectId(projectId);

        log.info("AreaController.getAreaByProjectId: end!");
        return new ResponseEntity<List<Area>>(area, HttpStatus.OK);
    }
}
