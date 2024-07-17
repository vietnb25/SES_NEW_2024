package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataPanel1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationPanelResponse {

    private Long id;

    private Long deviceId;

    private Integer tempPanel;

    private Integer v;

    private Integer jaSolar;

    private Integer eaSolar;

    private Integer longiSolar;

    private Integer p;

    private String sentDate;

    private Long transactionDate;

    public OperationPanelResponse(final DataPanel1 panel1) {
        this.id = panel1.getId();
        this.deviceId = panel1.getDeviceId();
        this.tempPanel = panel1.getTempPanel();
        this.v = panel1.getV();
        this.jaSolar = panel1.getJaSolar();
        this.eaSolar = panel1.getEaSolar();
        this.longiSolar = panel1.getLongiSolar();
        this.p = panel1.getP();
        this.sentDate = panel1.getSentDate();
        this.transactionDate = panel1.getTransactionDate();
    }
}
