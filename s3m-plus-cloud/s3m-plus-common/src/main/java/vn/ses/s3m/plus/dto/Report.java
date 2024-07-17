package vn.ses.s3m.plus.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class Report {

    private Integer id;

    private Integer userId;

    private Date dateFrom;

    private Date dateTo;

    private Integer reportId;

    private String deviceId;

    private Timestamp reportDate;

    private Integer status;

    private String url;

    private Integer deleted;

    private Integer managerId;

    private Integer percent;

    private Timestamp updated;

    private String deviceName;

    private String systemType;

    private String dateType;

    private Integer projectId;
}
