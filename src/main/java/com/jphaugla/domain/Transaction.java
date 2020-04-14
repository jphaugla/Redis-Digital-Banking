package com.jphaugla.domain;

import lombok.*;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@RedisHash("transaction")

public class Transaction {
    private @Id String account_tranid;
    private @Indexed String account_no;
    private String account_type;
    private String amount_type;
    private Double amount;
    private @Indexed String cardNum;
    private @Indexed String merchantCtygCd;
    private @Indexed String merchantCtgyDesc;
    private String merchantName;
    private Double origTranAmt;
    private String referenceKeyType;
    private String referenceKeyValue;
    private Double tranAmt;
    private @Indexed String tranCd ;
    private String tranDescription;
    private Date tranExpDt;
    private Integer tranId;
    private Date tranInitDt;
    private Date timestamp;
    private String tranStat   ;
    private String tranType   ;
    private String transRsnCd;
    private String transRsnDesc;
    private String transRsnType;
    private String transRespCd ;
    private String transRespDesc;
    private String transRespType ;
    private String location;
}
