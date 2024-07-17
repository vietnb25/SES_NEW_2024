package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class SchedulePV {
    private Integer id;

    private String addRess;

    private Double acPower;

    private Double congSuatTietGiam;

    private Double congSuatChoPhep;

    private Double congSuatDinhMuc;

    private String fromTime;

    private String toTime;

    private String status;

    private String status2;

    private Timestamp timeView;

    private String timeSet;

    private Integer stt;

    private Integer typeScrop;

    private Integer deleteFlag;

    private Integer parentId;

    private Timestamp createDate;

    private Integer createId;

    private Timestamp updateDate;

    private Timestamp updateId;
}
