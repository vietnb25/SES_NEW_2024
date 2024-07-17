package vn.ses.s3m.plus.response;

import lombok.Data;
import vn.ses.s3m.plus.dto.UserDto;

@Data
public class UserResponse {
    private Integer id;

    private String staffName;

    private String customerName;

    private String superManagerName;

    private String managerName;

    private String areaName;

    private String projectName;

    private String username;

    private Integer lockFlag;

    private Integer userType;

    private String targetManager;

    private Integer targetId;

    private String updateDate;

    public UserResponse(final UserDto userDto) {
        this.id = userDto.getId();
        this.staffName = userDto.getStaffName();
        this.customerName = userDto.getCustomerName();
        this.superManagerName = userDto.getSuperManagerName();
        this.managerName = userDto.getManagerName();
        this.areaName = userDto.getAreaName();
        this.projectName = userDto.getProjectName();
        this.username = userDto.getUsername();
        this.lockFlag = userDto.getLockFlag();
        this.userType = userDto.getUserType();
        this.targetManager = userDto.getTargetManager();
        this.updateDate = userDto.getUpdateDate();
    }
}
