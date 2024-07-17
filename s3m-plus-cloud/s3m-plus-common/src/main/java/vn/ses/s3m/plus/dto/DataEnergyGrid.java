package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataEnergyGrid {
    private Long wh;

    private Long whDay;

    private Long whMonth;

    private Long whYear;

    private Long whPrevDay;

    private Long whPrevMonth;

    private Long whPrevYear;

    private String infor;
}
