package cz.zcu.kiv.mjakubas.piae.sem.jdbc.repository.v1;

import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Employee;
import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Workplace;
import cz.zcu.kiv.mjakubas.piae.sem.core.repository.IWorkplaceRepository;
import cz.zcu.kiv.mjakubas.piae.sem.jdbc.mapper.EmployeeMapper;
import cz.zcu.kiv.mjakubas.piae.sem.jdbc.mapper.WorkplaceMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
@AllArgsConstructor
public class JdbcWorkplaceRepository implements IWorkplaceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final WorkplaceMapper WORKPLACE_MAPPER = new WorkplaceMapper();

    private static final EmployeeMapper EMPLOYEE_MAPPER = new EmployeeMapper();

    @Override
    public Workplace fetchWorkplace(long workplaceId) {
        var sql = """
                SELECT w.*, e.* FROM workplace w
                LEFT JOIN employee e ON e.employee_id=w.wrk_manager_id
                WHERE wrk_enabled=:isEnabled AND workplace_id=:wrk_id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("wrk_id", workplaceId);
        params.addValue("isEnabled", true);

        return jdbcTemplate.query(sql, params, WORKPLACE_MAPPER).get(0);
    }

    @Override
    public Workplace fetchWorkplace(String abbrevation) {
        var sql = """
                SELECT w.*, e.* FROM workplace w
                LEFT JOIN employee e ON e.employee_id=w.wrk_manager_id
                WHERE wrk_enabled=:isEnabled AND wrk_abbrevation=:wrk_abbrevation
                """;

        var params = new MapSqlParameterSource();
        params.addValue("wrk_abbrevation", abbrevation);
        params.addValue("isEnabled", true);

        return jdbcTemplate.query(sql, params, WORKPLACE_MAPPER).get(0);
    }

    @Override
    public List<Workplace> fetchWorkplaces() {
        var sql = """
                SELECT w.*, e.* FROM workplace w
                LEFT JOIN employee e ON e.employee_id=w.wrk_manager_id
                WHERE w.wrk_enabled=1
                """;

        return jdbcTemplate.query(sql, WORKPLACE_MAPPER);
    }

    @Override
    public List<Employee> fetchWorkplaceEmployees(long workplaceId) {
        var sql = """
                SELECT e.*, w.wrk_abbrevation
                FROM employee e
                INNER JOIN workplace w ON w.workplace_id=e.emp_workplace_id
                WHERE emp_enabled=:isEnabled AND w.workplace_id=:id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("isEnabled", true);
        params.addValue("id", workplaceId);

        return jdbcTemplate.query(sql, EMPLOYEE_MAPPER);
    }

    @Override
    public boolean createWorkplace(@NonNull Workplace workplace) {
        var sql = """
                INSERT INTO workplace
                (wrk_enabled, wrk_abbrevation, wrk_name)
                VALUES
                (:wrk_enabled, :wrk_abbreviation, :wrk_name)
                """;

        var params = new MapSqlParameterSource();
        params.addValue("wrk_enabled", true);
        params.addValue("wrk_abbreviation", workplace.getAbbreviation());
        params.addValue("wrk_name", workplace.getFullName());

        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean updateWorkplace(@NonNull Workplace workplace, long workplaceId) {
        var sql = """
                UPDATE workplace
                SET wrk_enabled = :wrk_enabled, wrk_abbrevation = :wrk_abbrevation,
                 wrk_name = :wrk_name, wrk_manager_id = :wrk_manager_id
                WHERE workplace_id = :workplace_id;
                """;

        var params = new MapSqlParameterSource();
        params.addValue("wrk_enabled", 1);
        params.addValue("wrk_abbrevation", workplace.getAbbreviation());
        params.addValue("wrk_name", workplace.getFullName());
        if (workplace.getManager() != null) params.addValue("wrk_manager_id", workplace.getManager().getId());
        else params.addValue("wrk_manager_id", null);
        params.addValue("workplace_id", workplace.getId());

        var rowsUpdated = jdbcTemplate.update(sql, params);

        return rowsUpdated == 1;
    }

    @Override
    public boolean removeWorkplace(long workplaceId) {
        return false;
    }
}