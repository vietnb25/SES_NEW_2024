package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusWarningRespone {

    private String date;

    private String fromTime;

    private String toTime;

    private int size;

    private int index;

    private boolean icon1;

    private boolean icon2;

    private boolean icon3;

    private boolean icon4;

    private boolean icon5;

    private boolean icon6;

    private boolean icon7;

    private boolean icon8;

    private boolean icon9;

    private boolean icon10;

    private boolean icon11;

    private boolean icon12;
}
