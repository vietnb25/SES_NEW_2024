package vn.ses.s3m.plus.pv.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataPanel1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOperationPanelResponse {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer Temp_panel;

    private Integer V;

    private Integer JA_SOLAR;

    private Integer EA_SOLAR;

    private Integer LONGI_SOLAR;

    private Float P;

    private Float T;

    private Float I;

    private Float U;

    private String sentDate;

    private Long transactionDate;

    public ChartOperationPanelResponse(final DataPanel1 panel1) {
        this.id = panel1.getId();
        this.deviceId = panel1.getDeviceId();
        this.deviceName = panel1.getDeviceName();
        this.deviceType = panel1.getDeviceType();
        this.deviceCode = panel1.getDeviceCode();
        this.Temp_panel = panel1.getTemp_panel();
        this.V = panel1.getV();
        this.JA_SOLAR = panel1.getJA_SOLAR();
        this.EA_SOLAR = panel1.getEA_SOLAR();
        this.LONGI_SOLAR = panel1.getLONGI_SOLAR();
        this.P = panel1.getP();
        this.T = panel1.getT();
        this.I = panel1.getI();
        this.U = panel1.getU();
        this.sentDate = panel1.getSentDate();
        this.transactionDate = panel1.getTransactionDate();
    }

}
