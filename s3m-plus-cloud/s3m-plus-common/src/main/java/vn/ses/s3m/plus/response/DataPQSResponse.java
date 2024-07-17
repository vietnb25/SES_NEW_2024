package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPQSResponse {

    private String time;

    private Integer high;

    private Integer low;

    private Integer normal;

    private Integer total;

    // Tham số hiện tổng điện năng trên chart
    private Integer param;

    public DataPQSResponse(final DataLoadFrame1 frame1) {
        this.time = frame1.getSentDate();
        this.high = frame1.getHigh();
        this.low = frame1.getLow();
        this.normal = frame1.getNormal();
        this.total = frame1.getTotal();
        this.param = 0;
    }

}
