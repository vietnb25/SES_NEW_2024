package vn.ses.s3m.plus.batch.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataRmuDrawer1View implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long deviceId;

    private Integer viewType;

    private String viewTime;

    private Long pTotal;

    private Long ep;

    private String sentDate;
}
