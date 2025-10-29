package co.mannit.commonservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldCount {
    private String field;
    private String value;
    private long count;
}
