package co.mannit.commonservice.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiCountResponse {
    private List<FieldCount> counts;
}

