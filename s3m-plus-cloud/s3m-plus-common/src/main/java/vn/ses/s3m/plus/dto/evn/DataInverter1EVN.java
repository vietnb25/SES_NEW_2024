package vn.ses.s3m.plus.dto.evn;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DataInverter1EVN implements Comparable<DataInverter1EVN> {

    private Integer id;

    private String deviceId;

    private String deviceName;

    private String projectName;

    private Integer deviceType;

    private Integer w;

    private Integer wh;

    private Integer whDay;

    private Long whTotal;

    private Integer dcw;

    private Integer congSuatChoPhep;

    private Timestamp sentDate;

    private Long transactionDate;
    
    private Integer superManagerId;

    @Override
    public int compareTo(DataInverter1EVN o) {
        return this.sentDate.compareTo(o.sentDate);
    }

}