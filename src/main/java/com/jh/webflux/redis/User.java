package com.jh.webflux.redis;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    String vendorCode;
    String name;
}
