package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.PasswordEncode;
import vn.ses.s3m.plus.dao.UserRoleMapper;
import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.UserDto;
import vn.ses.s3m.plus.dto.UserRole;
import vn.ses.s3m.plus.form.UserForm;
import vn.ses.s3m.plus.form.UserUpdateForm;
import vn.ses.s3m.plus.response.UserResponse;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.PermissionService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.SuperManagerService;
import vn.ses.s3m.plus.service.UserService;

/**
 * Controller Xử lý tài khoản user
 *
 * @author Arius Vietnam JSC
 * @since 2022-10-28
 */
@RestController
@RequestMapping ("/common/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SuperManagerService superManagerService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ProjectService projectService;

    /**
     * Lấy danh sách tài khoản.
     *
     * @return Danh sách tài khoản.
     */
    @GetMapping ("/list")
    public ResponseEntity<List<UserResponse>> listUser() {
        List<UserDto> users = userService.findAllUsers();

        List<Customer> customers = customerService.getListCustomer();

        List<SuperManager> superManagers = superManagerService.getListSuperManager();
        List<Manager> managers = managerService.getManagers(null);
        List<Area> areas = areaService.getListArea();

        for (UserDto user : users) {
            String customerName = "";
            String target = "";

            if (user.getCustomerIds() != null) {
                String[] customerIds = user.getCustomerIds()
                    .split(",");
                for (String id : customerIds) {
                    for (Customer customer : customers) {
                        if (Integer.parseInt(id) == customer.getCustomerId()) {
                            customerName += customer.getCustomerName() + ", ";
                        }
                    }
                }
                user.setCustomerName(customerName);
            }

            if (user.getTargetId() != null && user.getUserType() != null) {
                if (user.getUserType() != 1 || user.getUserType() != 2) {
                    if (user.getUserType() == 4) {
                        int superManager = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            if (sm.getSuperManagerId() == superManager) {
                                target = sm.getSuperManagerName();
                            }
                        }
                    } else if (user.getUserType() == 5) {
                        int managerId = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            for (Manager manager : managers) {
                                if (sm.getSuperManagerId() == manager.getSuperManagerId()) {
                                    if (manager.getManagerId() == managerId) {
                                        target = sm.getSuperManagerName() + " > " + manager.getManagerName();
                                    }
                                }
                            }
                        }
                    } else if (user.getUserType() == 6) {
                        int areaId = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            for (Manager manager : managers) {
                                for (Area area : areas) {
                                    if (sm.getSuperManagerId() == manager.getSuperManagerId()
                                        && manager.getManagerId() == area.getManagerId()) {
                                        if (area.getAreaId() == areaId) {
                                            target = sm.getSuperManagerName() + " > " + manager.getManagerName() + " > "
                                                + area.getAreaName();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            user.setTargetManager(target);
        }

        List<UserResponse> usersResponse = new ArrayList<>();
        for (UserDto user : users) {
            UserResponse u = new UserResponse(user);
            usersResponse.add(u);
        }

        return new ResponseEntity<>(usersResponse, HttpStatus.OK);
    }

    @GetMapping ("/usersByCustomerIds")
    public ResponseEntity<?> listUserByCustomerIds(@RequestParam String[] customerIds, @RequestParam Integer userId) {
        List<UserDto> users = userService.findAllUsers();

        Set<UserDto> _users = new HashSet<>();

        for (int i = 0; i < customerIds.length; i++) {
            String cId = customerIds[i];
            users.forEach(u -> {
                String[] _customerIds = u.getCustomerIds() != null
                    ? u.getCustomerIds()
                        .split(",")
                    : null;
                if ( (u.getCreateId() != null && u.getCreateId() == userId)
                    || (_customerIds != null && Arrays.asList(_customerIds)
                        .contains(String.valueOf(cId)))) {
                    _users.add(u);
                }
            });
        }

        List<Customer> customers = customerService.getListCustomer();

        List<SuperManager> superManagers = superManagerService.getListSuperManager();
        List<Manager> managers = managerService.getManagers(null);
        List<Area> areas = areaService.getListArea();

        for (UserDto user : _users) {
            String customerName = "";
            String target = "";

            if (user.getCustomerIds() != null) {
                String[] _customerIds = user.getCustomerIds()
                    .split(",");
                for (String id : _customerIds) {
                    for (Customer customer : customers) {
                        if (Integer.parseInt(id) == customer.getCustomerId()) {
                            customerName += customer.getCustomerName() + ", ";
                        }
                    }
                }
                user.setCustomerName(customerName);
            }

            if (user.getTargetId() != null && user.getUserType() != null) {
                if (user.getUserType() != 1 || user.getUserType() != 2) {
                    if (user.getUserType() == 4) {
                        int superManager = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            if (sm.getSuperManagerId() == superManager) {
                                target = sm.getSuperManagerName();
                            }
                        }
                    } else if (user.getUserType() == 5) {
                        int managerId = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            for (Manager manager : managers) {
                                if (sm.getSuperManagerId() == manager.getSuperManagerId()) {
                                    if (manager.getManagerId() == managerId) {
                                        target = sm.getSuperManagerName() + " > " + manager.getManagerName();
                                    }
                                }
                            }
                        }
                    } else if (user.getUserType() == 6) {
                        int areaId = user.getTargetId();
                        for (SuperManager sm : superManagers) {
                            for (Manager manager : managers) {
                                for (Area area : areas) {
                                    if (sm.getSuperManagerId() == manager.getSuperManagerId()
                                        && manager.getManagerId() == area.getManagerId()) {
                                        if (area.getAreaId() == areaId) {
                                            target = sm.getSuperManagerName() + " > " + manager.getManagerName() + " > "
                                                + area.getAreaName();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            user.setTargetManager(target);
        }

        List<UserResponse> usersResponse = new ArrayList<>();
        for (UserDto user : _users) {
            UserResponse u = new UserResponse(user);
            usersResponse.add(u);
        }

        return new ResponseEntity<>(usersResponse, HttpStatus.OK);
    }

    @GetMapping ("/{userName}")
    public ResponseEntity<User> getUser(@PathVariable ("userName") final String userName) {
        User userByUsername = userService.getUserByUsername(userName);
        return new ResponseEntity<>(userByUsername, HttpStatus.OK);
    }

    /**
     * Tìm kiếm tài khoản.
     *
     * @param keyword Dùng để tìm kiếm tài khoản theo keyword.
     * @return Danh sách tài khoản được tìm theo keyword.
     */
    @GetMapping ("/search")
    public ResponseEntity<?> searchUser(@RequestParam String keyword,
        @RequestParam (name = "customerIds", required = false) String[] customerIds,
        @RequestParam (name = "userId", required = false) Integer userId) {

        UserDto userDto = userService.getUserById(userId);

        List<Customer> customers = customerService.getListCustomer();
        List<SuperManager> superManagers = superManagerService.getListSuperManager();
        List<Manager> managers = managerService.getManagers(null);
        List<Area> areas = areaService.getListArea();

        if (userDto.getUserType() != null && userDto.getUserType() == 1) {
            List<UserDto> users = userService.searchUser("%" + keyword + "%");

            for (UserDto user : users) {
                String customerName = "";
                String target = "";

                if (user.getCustomerIds() != null) {
                    String[] _customerIds = user.getCustomerIds()
                        .split(",");
                    for (String id : _customerIds) {
                        for (Customer customer : customers) {
                            if (Integer.parseInt(id) == customer.getCustomerId()) {
                                customerName += customer.getCustomerName() + ", ";
                            }
                        }
                    }
                    user.setCustomerName(customerName);
                }

                if (user.getTargetId() != null && user.getUserType() != null) {
                    if (user.getUserType() != 1 || user.getUserType() != 2) {
                        if (user.getUserType() == 4) {
                            int superManager = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                if (sm.getSuperManagerId() == superManager) {
                                    target = sm.getSuperManagerName();
                                }
                            }
                        } else if (user.getUserType() == 5) {
                            int managerId = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                for (Manager manager : managers) {
                                    if (sm.getSuperManagerId() == manager.getSuperManagerId()) {
                                        if (manager.getManagerId() == managerId) {
                                            target = sm.getSuperManagerName() + " > " + manager.getManagerName();
                                        }
                                    }
                                }
                            }
                        } else if (user.getUserType() == 6) {
                            int areaId = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                for (Manager manager : managers) {
                                    for (Area area : areas) {
                                        if (sm.getSuperManagerId() == manager.getSuperManagerId()
                                            && manager.getManagerId() == area.getManagerId()) {
                                            if (area.getAreaId() == areaId) {
                                                target = sm.getSuperManagerName() + " > " + manager.getManagerName()
                                                    + " > " + area.getAreaName();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                user.setTargetManager(target);
            }

            List<UserResponse> usersResponse = new ArrayList<>();
            for (UserDto user : users) {
                UserResponse u = new UserResponse(user);
                usersResponse.add(u);
            }

            return new ResponseEntity<>(usersResponse, HttpStatus.OK);
        } else {
            List<UserDto> users = userService.searchUser("%" + keyword + "%");

            Set<UserDto> _users = new HashSet<>();

            for (int i = 0; i < customerIds.length; i++) {
                String cId = customerIds[i];
                users.forEach(u -> {
                    String[] _customerIds = u.getCustomerIds() != null
                        ? u.getCustomerIds()
                            .split(",")
                        : null;
                    if ( (u.getCreateId() != null && u.getCreateId() == userId)
                        || (_customerIds != null && Arrays.asList(_customerIds)
                            .contains(String.valueOf(cId)))) {
                        _users.add(u);
                    }
                });
            }

            for (UserDto user : _users) {
                String customerName = "";
                String target = "";

                if (user.getCustomerIds() != null) {
                    String[] _customerIds = user.getCustomerIds()
                        .split(",");
                    for (String id : _customerIds) {
                        for (Customer customer : customers) {
                            if (Integer.parseInt(id) == customer.getCustomerId()) {
                                customerName += customer.getCustomerName() + ", ";
                            }
                        }
                    }
                    user.setCustomerName(customerName);
                }

                if (user.getTargetId() != null && user.getUserType() != null) {
                    if (user.getUserType() != 1 || user.getUserType() != 2) {
                        if (user.getUserType() == 4) {
                            int superManager = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                if (sm.getSuperManagerId() == superManager) {
                                    target = sm.getSuperManagerName();
                                }
                            }
                        } else if (user.getUserType() == 5) {
                            int managerId = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                for (Manager manager : managers) {
                                    if (sm.getSuperManagerId() == manager.getSuperManagerId()) {
                                        if (manager.getManagerId() == managerId) {
                                            target = sm.getSuperManagerName() + " > " + manager.getManagerName();
                                        }
                                    }
                                }
                            }
                        } else if (user.getUserType() == 6) {
                            int areaId = user.getTargetId();
                            for (SuperManager sm : superManagers) {
                                for (Manager manager : managers) {
                                    for (Area area : areas) {
                                        if (sm.getSuperManagerId() == manager.getSuperManagerId()
                                            && manager.getManagerId() == area.getManagerId()) {
                                            if (area.getAreaId() == areaId) {
                                                target = sm.getSuperManagerName() + " > " + manager.getManagerName()
                                                    + " > " + area.getAreaName();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                user.setTargetManager(target);
            }

            List<UserResponse> usersResponse = new ArrayList<>();
            for (UserDto user : _users) {
                UserResponse u = new UserResponse(user);
                usersResponse.add(u);
            }

            return new ResponseEntity<>(usersResponse, HttpStatus.OK);
        }

    }

    /**
     * Xóa tài khoản.
     *
     * @param userId Id của user cần xóa.
     * @return Kết quả xóa tài khoản.
     */
    @DeleteMapping ("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("userId") final Integer userId) {
        try {
            // delete user
            userService.deleteUser(userId);
            permissionService.deleteTreeData(userId);
            permissionService.deleteMapData(userId);
            permissionService.deleteCategoryData(userId);

            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Lấy dữ liệu để thêm mới tài khoản.
     *
     * @return Dữ liệu bao gồm danh sách super managers, customers, user roles.
     */
    @GetMapping ("/add")
    public ResponseEntity<Map<String, Object>> getDataForAddUser() {
        List<SuperManager> superManagers = superManagerService.getListSuperManager();
        List<UserRole> roles = userService.getRole();
        List<Customer> customers = customerService.getListCustomer();
        List<UserRole> userRoles = new ArrayList<UserRole>();
        for (UserRole newrole : roles) {
            if (newrole.getRoleCode()
                .equalsIgnoreCase("ROLE_MOD")
                || newrole.getRoleCode()
                    // CHECKSTYLE:OFF
                    .equalsIgnoreCase("ROLE_USER")) {
                // CHECKSTYLE:ON
                userRoles.add(newrole);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("superManagers", superManagers);
        map.put("userRoles", userRoles);
        map.put("customers", customers);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    /**
     * Thêm mới tài khoản.
     *
     * @param userForm Dữ liệu của User.
     * @return Kết quả thêm mới user.
     */
    @PostMapping ("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody final UserForm userForm) {
        // validate unique email and username
        List<String> errors = new ArrayList<>();
        User userByUsername = userService.getUserByUsername(userForm.getUsername());
        if (userByUsername != null) {
            errors.add(Constants.UserValidation.USERNAME_IS_EXIST);
        }
        User userByEmail = userService.getUserByEmail(userForm.getEmail());
        if (userByEmail != null) {
            errors.add(Constants.UserValidation.EMAIL_IS_EXIST);
        }

        // return
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        /*
         * if data valid
         */
        // CHECKSTYLE:OFF
        if (userForm.getUserType() == 2) {
            userForm.setRoleId(2);
        } else if (userForm.getUserType() == 3) {
            userForm.setRoleId(3);
        } else if (userForm.getUserType() == 4 || userForm.getUserType() == 5 || userForm.getUserType() == 6
            || userForm.getUserType() == 7) {
            userForm.setRoleId(4);
        }
        // CHECKSTYLE:ON

        // encode password
        userForm.setPassword(PasswordEncode.encodePassword(userForm.getPassword()));
        if (userForm.getProjectIds() == "") {
            String projectIds = "";
            HashMap<String, String> condition = new HashMap<String, String>();
            condition.put("customerIds", userForm.getCustomerIds());
            List<Project> list = projectService.getListPro(condition);
            for (Project project : list) {
                projectIds += project.getProjectId() + ",";
            }
            if (projectIds.length() > 0) {
                // Xóa phần tử cuối cùng bằng cách lấy substring từ đầu đến phần tử cuối cùng - 1
                projectIds = projectIds.substring(0, projectIds.length() - 1);
            }
            userForm.setProjectIds(projectIds);
        }

        // user insert to db
        User user = new User();
        BeanUtils.copyProperties(userForm, user);
        userService.insertUser(user);

        // last user id
        int userId = userService.getLastUserId();

        // insert role user
        Map<String, String> condition = new HashMap<>();
        condition.put("userId", String.valueOf(userId));
        condition.put("roleId", String.valueOf(userForm.getRoleId()));
        userRoleMapper.insertUserRole(condition);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Lấy thông tin để cập nhật user.
     *
     * @param userId Id của user cần cập nhật.
     * @return Danh sách super managers, customers, managers, areas, projects, user roles và thông tin user cần cập
     *     nhật.
     */
    @GetMapping ("/update/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable ("userId") final Integer userId) {
        UserDto user = userService.getUserById(userId);

        String customerName = "";
        String target = "";

        List<Customer> customers = customerService.getListCustomer();
        List<SuperManager> superManagers = superManagerService.getListSuperManager();
        List<Manager> managers = managerService.getManagers(null);
        List<Area> areas = areaService.getListArea();

        if (user.getTargetId() != null && user.getUserType() != null) {
            if (user.getUserType() != 1 || user.getUserType() != 2) {
                if (user.getUserType() == 4) {
                    int superManager = user.getTargetId();
                    for (SuperManager sm : superManagers) {
                        if (sm.getSuperManagerId() == superManager) {
                            target = sm.getSuperManagerName();
                        }
                    }
                } else if (user.getUserType() == 5) {
                    int managerId = user.getTargetId();
                    for (SuperManager sm : superManagers) {
                        for (Manager manager : managers) {
                            if (sm.getSuperManagerId() == manager.getSuperManagerId()) {
                                if (manager.getManagerId() == managerId) {
                                    target = sm.getSuperManagerName() + " > " + manager.getManagerName();
                                }
                            }
                        }
                    }
                } else if (user.getUserType() == 6) {
                    int areaId = user.getTargetId();
                    for (SuperManager sm : superManagers) {
                        for (Manager manager : managers) {
                            for (Area area : areas) {
                                if (sm.getSuperManagerId() == manager.getSuperManagerId()
                                    && manager.getManagerId() == area.getManagerId()) {
                                    if (area.getAreaId() == areaId) {
                                        target = sm.getSuperManagerName() + " > " + manager.getManagerName() + " > "
                                            + area.getAreaName();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            user.setTargetManager(target);
        } else {
            if (user.getCustomerIds() != null) {
                String[] _customerIds = user.getCustomerIds()
                    .split(",");
                for (String id : _customerIds) {
                    for (Customer customer : customers) {
                        if (Integer.parseInt(id) == customer.getCustomerId()) {
                            customerName += customer.getCustomerName() + ", ";
                        }
                    }
                }
                user.setCustomerName(customerName);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    /**
     * Cập nhật thông tin user.
     *
     * @param userForm Dữ liệu được gửi từ client.
     * @param userId Id của User cần cập nhật thông tin.
     * @return Kết quả cập nhật.
     */
    @PutMapping ("/update/{userId}")
    public ResponseEntity<?> editUser(@Valid @RequestBody final UserUpdateForm userForm,
        @PathVariable ("userId") final Integer userId) {
        // list errors
        List<String> errors = new ArrayList<>();

        // kiểm tra email đã có trong db chưa
        // User userByEmail = userService.getUserByEmail(userForm.getEmail());
        // if (userByEmail != null && userByEmail.getId() != userId) {
        // errors.add(Constants.UserValidation.EMAIL_IS_EXIST);
        // }

        // return nếu có lỗi
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
        if (userForm.getUserType() == 2) {
            String projectIds = "";
            HashMap<String, String> conditionUpdateIdsPro = new HashMap<String, String>();
            conditionUpdateIdsPro.put("customerIds", userForm.getCustomerIds());
            List<Project> list = projectService.getListPro(conditionUpdateIdsPro);
            for (Project project : list) {
                projectIds += project.getProjectId() + ",";
            }
            if (projectIds.length() > 0) {
                projectIds = projectIds.substring(0, projectIds.length() - 1);
            }
            userForm.setProjectIds(projectIds);
        }

        // nếu mật khẩu được thay đổi
        if (userForm.getPassword() != null && !userForm.getPassword()
            .isEmpty()) {
            userForm.setPassword(PasswordEncode.encodePassword(userForm.getPassword()));
        }

        // user update to db
        User user = new User();
        BeanUtils.copyProperties(userForm, user);
        if (user.getUserType() == 1) {
            user.setCustomerIds(null);
            user.setProjectIds(null);
        }
        userService.updateUser(user);

        // update lại số lần login fail và trạng thái
        Map<String, String> condition = new HashMap<>();
        condition.put("userId", String.valueOf(userId));
        condition.put("lockFlag", String.valueOf(userForm.getLockFlag()));
        userService.updateLockedUser(condition);

        if (userForm.getLockFlag() == 0) {
            condition.put("failedAttempt", String.valueOf(0));
            userService.updateFailedAttempts(condition);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Mở khóa tài khỏa user.
     *
     * @param userId Id của User cần mở khóa tài khoản.
     * @return Kết quả mở khóa.
     */
    @GetMapping ("/unlock/{userId}")
    public ResponseEntity<Void> unlockUser(@PathVariable ("userId") final Integer userId) {
        try {
            Map<String, String> condition = new HashMap<>();
            condition.put("userId", String.valueOf(userId));
            condition.put("failedAttempt", String.valueOf(0));
            condition.put("lockFlag", String.valueOf(0));
            userService.updateLockedUser(condition);
            userService.updateFailedAttempts(condition);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Khóa tài khóa user.
     *
     * @param userId Id của user cần khóa tài khoản.
     * @return Kết quả khóa tài khoản.
     */
    @GetMapping ("/lock/{userId}")
    public ResponseEntity<?> lockUser(@PathVariable ("userId") final Integer userId) {
        try {
            Map<String, String> condition = new HashMap<>();
            condition.put("userId", String.valueOf(userId));
            condition.put("lockFlag", String.valueOf(1));
            userService.updateLockedUser(condition);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping ("/updatePriorityIngredients")
    public ResponseEntity<?> updatePriorityIngredients(@Valid @RequestBody final UserUpdateForm userForm) {
        // list errors
        List<String> errors = new ArrayList<>();

        // return nếu có lỗi
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        // user update to db
        User user = new User();
        BeanUtils.copyProperties(userForm, user);
        userService.updatePriorityIngredients(user);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
