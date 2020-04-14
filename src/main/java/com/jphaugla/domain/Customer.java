package com.jphaugla.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@RedisHash("Customer")

public class Customer {
    private  @Id String customer_id;
    private  String address_line1;
    private  String address_line2;
    private  String address_type;
    private  String bill_pay_enrolled;
    private  @Indexed String city;
    private  String country_code;
    private  String created_by;
    private Date created_datetime;
    private  String customer_origin_system;
    private  String customer_status;
    private  String customer_type;
    private  Date date_of_birth;
    private  @Indexed String first_name;
    private  @Indexed String full_name;
    private  String gender;
    private  String government_id;
    private  String government_id_type;
    private  String last_name;
    private  Date   last_updated;
    private  String last_updated_by;
    private  String middle_name;
    private  String prefix;
    private  String query_helper_column;
    private  @Indexed String state_abbreviation;
    private  String zipcode;
    private  String zipcode4;
    private  Email home_email;
    private  Email work_email;
    private  Email custom_email_1;
    private  Email custom_email_2;
    private  PhoneNumber home_phone;
    private  PhoneNumber work_phone;
    private  PhoneNumber cell_phone;
    private  PhoneNumber custom_phone;

}
