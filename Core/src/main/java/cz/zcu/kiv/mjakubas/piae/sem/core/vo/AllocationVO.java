package cz.zcu.kiv.mjakubas.piae.sem.core.vo;

import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Allocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

/**
 * Value object for representing {@link Allocation}.
 *
 * @see Allocation
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class AllocationVO {

    private long employeeId;
    private long projectId;
    private long courseId;
    private long functionId;
    private float allocationScope;  // in x/FTE
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateUntil;
    private String description;
    private String role;

    private Boolean isActive = true;
}
