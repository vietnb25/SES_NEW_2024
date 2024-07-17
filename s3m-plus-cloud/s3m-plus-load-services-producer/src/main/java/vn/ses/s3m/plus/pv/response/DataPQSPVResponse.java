package vn.ses.s3m.plus.pv.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataInverter1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPQSPVResponse {

    private Double high;

    private Double low;

    private Double normal;

    private Double total;

    // Tham số hiện tổng điện năng trên chart
    private Double param;

    private String sentDate;

    public DataPQSPVResponse(final DataInverter1 inverter1) {
        this.high = inverter1.getHigh();
        this.low = inverter1.getLow();
        this.normal = inverter1.getNormal();
        this.total = inverter1.getTotal();
        this.param = (double) 0;
        this.sentDate = inverter1.getSentDate();
    }

}
