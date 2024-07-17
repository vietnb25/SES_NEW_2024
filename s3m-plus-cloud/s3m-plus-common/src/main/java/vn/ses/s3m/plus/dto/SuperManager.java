package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class SuperManager {

    private Integer superManagerId;

    private String superManagerName;

    private String superManagerCode;

    private String description;

    private Timestamp updateDate;

    private Double latitude;

    private Double longitude;

    private Integer projectNumber;

    private Integer pvNumber;

    private Long cspvTotal;

    private Integer loadNumber;

    private Long loadEnnergy;

    private String superManagerDesc;

    private Long totalPv;
}
