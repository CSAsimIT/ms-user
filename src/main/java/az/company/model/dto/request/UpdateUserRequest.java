package az.company.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String surname;

    @Email
    private String email;

    @Size(max = 20)
    private String phone;
}
