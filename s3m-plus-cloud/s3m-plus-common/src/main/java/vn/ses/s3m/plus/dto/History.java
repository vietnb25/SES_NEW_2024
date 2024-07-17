package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class History {

    private Integer historyId;

    private Integer deviceId;

    private String fromDate;

    private String toDate;

    private String timeFrame;

    private Timestamp timeInsert;

    private Double congSuatChoPhep;

    private Double congSuatDinhMuc;

    private Double congSuatTietGiam;

    private Integer deleteFlag;

    private Integer status;

    private Integer typeScrop;

    private Integer stt;

    private Integer parentId;

    private Integer updateFlag;

    private Timestamp createDate;

    private Timestamp deleteDate;

    private String viTri;
}
