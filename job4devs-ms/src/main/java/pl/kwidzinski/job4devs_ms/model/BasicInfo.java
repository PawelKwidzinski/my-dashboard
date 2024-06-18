package pl.kwidzinski.job4devs_ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicInfo {
    private String position;
    private String employer;
    private String workplace;
}
