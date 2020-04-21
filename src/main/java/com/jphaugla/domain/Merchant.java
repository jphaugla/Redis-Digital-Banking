package com.jphaugla.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Merchant")
public class Merchant {

    private @Id String name;
    private @Indexed String categoryCode;
    private @Indexed String categoryDescription;
    private @Indexed String state;
    private @Indexed String countryCode;
}
