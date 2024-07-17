package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.Warning;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningOperationResponse {

    private Integer warningType;

    private Integer warningNo;

    private Long warningDuration;

    private String description;

    private String sentDate;

    private String time;

    public WarningOperationResponse(final Warning warning) {
        this.warningType = warning.getWarningType();
        this.warningNo = warning.getWarningNo();
        this.warningDuration = warning.getWarningDuration();
        this.description = warning.getDescription();
        this.sentDate = warning.getSentDate();
    }
}
