package com.jphaugla.domain;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@RedisHash("account")


public class Account {
    private @Id String account_no;
    private @Indexed String customer_id;
    private String account_type;
    private String account_origin_system;
    private @Indexed String account_status;
    private Date open_date;
    private  Date   last_updated;
    private  String last_updated_by;
    private  String created_by;
    private Date created_date;
}
