package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Area {

    private Integer areaId;

    private String areaName;

    private Integer managerId;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;

    private Double latitude;

    private Double longitude;

    private Integer projectNumber;

    private Integer pvNumber;

    private Long cspvTotal;

    private Integer loadNumber;

    private Long loadEnnergy;

    private Integer stt;

    private String managerName;

    private Integer superManagerId;

    private Integer customerId;

    private Long totalPv;
}
