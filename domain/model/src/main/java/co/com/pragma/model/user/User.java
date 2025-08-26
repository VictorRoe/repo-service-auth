package co.com.pragma.model.user;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;
    private String name;
    private String lastname;
    private String birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;

}
