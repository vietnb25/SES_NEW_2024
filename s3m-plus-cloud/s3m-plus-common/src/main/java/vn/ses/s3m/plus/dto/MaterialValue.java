package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MaterialValue {
    private Integer id;
    private Integer typeTime;
    private String typeTimeName;
    private Integer projectId;
    private Integer materialId;
    private String materialName;
    private Double materialPrice;
    private Timestamp createDate;
    private Timestamp updateDate;
}
