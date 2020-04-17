package com.jphaugla.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@RedisHash("Transaction")

public class Transaction {
    private @Id String tranId;
    private @Indexed String account_no;
    private String account_type;
    private String amount_type;
    private Double amount;
    private String cardNum;
    private String merchantCtygCd;
    private String merchantCtgyDesc;
    private String merchantName;
    private Double origTranAmt;
    private String referenceKeyType;
    private String referenceKeyValue;
    private Double tranAmt;
    private String tranCd ;
    private String tranDescription;
    private Date tranExpDt;
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
