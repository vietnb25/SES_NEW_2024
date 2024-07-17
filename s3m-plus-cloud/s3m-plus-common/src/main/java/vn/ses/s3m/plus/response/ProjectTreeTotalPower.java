package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class ProjectTreeTotalPower {

    private String time;

    private Long totalPower;

    private Long w;

    private Long totalEp;

    private Long wh;

    private Long powerGrid;

    private Long epGrid;

    private String totalLoad;
}
