package vn.ses.s3m.plus.batch.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table
public class DataCombiner1ViewCache {
    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long deviceId;

    private Integer viewType;

    private Float EpCombiner;

}
