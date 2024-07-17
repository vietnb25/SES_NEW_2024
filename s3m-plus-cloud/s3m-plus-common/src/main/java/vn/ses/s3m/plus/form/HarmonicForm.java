package vn.ses.s3m.plus.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarmonicForm {
    private String chartViewPoint;

    private String[] chartChanelU;

    private String[] chartChanelI;
}
