package io.github.ppdzm.utils.universal.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author stuartalex
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Person {
    private String name;
    private Integer age;
}
