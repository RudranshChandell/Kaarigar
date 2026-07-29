package kaarigar.backend.dto;

import kaarigar.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatContactDTO {
    private Long id;
    private String name;
    private Role role;
}
