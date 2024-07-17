package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Manager {

    private Integer managerId;

    private String managerName;

    private String managerCode;

    private Integer superManagerId;

    private String superManagerName;

    private String description;

    private Timestamp updateDate;

    private Integer customerId;

    private Double latitude;

    private Double longitude;

    private Integer areaNumber;

    private Integer pvNumber;

    private Long cspvTotal;

    private Integer loadNumber;

    private Long totalPv;
}
