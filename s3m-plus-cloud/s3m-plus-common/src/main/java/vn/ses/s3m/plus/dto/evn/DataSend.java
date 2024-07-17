package vn.ses.s3m.plus.dto.evn;

import java.util.List;

import lombok.Data;

@Data
public class DataSend {

    private Integer type;

    private String timeTo;

    private String timeFrom;

    private String fromDate;

    private String toDate;

    private Double sumCSCP;

    private Double sumCSDM;

    private Double sumCSTG;

    private List<Schedule> data;

    private String textStatus;

    private List<HistoryEVN> dataCheck;

    private Integer idSchedule;

}
