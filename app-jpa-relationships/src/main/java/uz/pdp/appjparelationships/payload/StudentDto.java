package uz.pdp.appjparelationships.payload;

import lombok.Data;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Subject;

import javax.persistence.*;
import java.util.List;
@Data
public class StudentDto {
    private String firstName;
    private String lastName;

    private String city;//Toshkent

    private String district;//Mirobod

    private String street;//U.Nosir ko'chasi

    private Integer group_id;

    private List<Integer> subjects_ids;
}
