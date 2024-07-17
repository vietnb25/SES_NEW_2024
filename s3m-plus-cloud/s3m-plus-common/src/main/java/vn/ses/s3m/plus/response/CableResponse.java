package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.Cable;

@Data
public class CableResponse {

    private Integer no;

    private Integer cableId;

    private String cableName;

    private Integer current;

    private String description;

    private Timestamp updateDate;

    public CableResponse(final Cable cable) {
        this.cableId = cable.getCableId();
        this.cableName = cable.getCableName();
        this.current = cable.getCurrent();
        this.description = cable.getDescription();
        this.updateDate = cable.getUpdateDate();
    }
}
