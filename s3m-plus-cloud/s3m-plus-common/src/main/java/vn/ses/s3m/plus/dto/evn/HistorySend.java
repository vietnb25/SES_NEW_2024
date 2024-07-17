package vn.ses.s3m.plus.dto.evn;

import lombok.Data;

@Data
public class HistorySend {

    private Integer id;

    private Integer type;

    private Integer superManagerId;

    private Integer managerId;

    private Integer areaId;
    
    private String fromDate;
    
    private String userName;
    
    private Integer targetId;

}
