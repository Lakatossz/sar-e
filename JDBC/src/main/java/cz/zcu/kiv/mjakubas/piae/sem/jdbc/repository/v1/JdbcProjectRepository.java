package cz.zcu.kiv.mjakubas.piae.sem.jdbc.repository.v1;

import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Employee;
import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Project;
import cz.zcu.kiv.mjakubas.piae.sem.core.repository.IAllocationRepository;
import cz.zcu.kiv.mjakubas.piae.sem.core.repository.IProjectRepository;
import cz.zcu.kiv.mjakubas.piae.sem.jdbc.mapper.EmployeeMapper;
import cz.zcu.kiv.mjakubas.piae.sem.jdbc.mapper.ProjectMapper;
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
public class JdbcProjectRepository implements IProjectRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final ProjectMapper PROJECT_MAPPER = new ProjectMapper();
    private static final EmployeeMapper EMPLOYEE_MAPPER = new EmployeeMapper();

    private final IAllocationRepository allocationRepository;

    @Override
    public Project fetchProject(long projectId) {
        var sql = """
                SELECT p.*, w.wrk_abbrevation, w.workplace_id, e.* FROM project p
                INNER JOIN workplace w ON w.workplace_id=p.pro_workplace_id
                INNER JOIN employee e ON e.employee_id=p.pro_manager_id
                WHERE p.pro_enabled=:isEnabled AND p.project_id=:id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("id", projectId);
        params.addValue("isEnabled", true);

        return jdbcTemplate.query(sql, params, PROJECT_MAPPER).get(0);
    }

    @Override
    public Project fetchProject(String projectName) {
        var sql = """
                SELECT p.*, w.wrk_abbrevation, e.* FROM project p
                INNER JOIN workplace w ON w.workplace_id=p.pro_workplace_id
                INNER JOIN employee e ON e.employee_id=p.pro_manager_id
                WHERE p.pro_enabled=:isEnabled AND p.pro_name=:projectName
                """;

        var params = new MapSqlParameterSource();
        params.addValue("projectName", projectName);
        params.addValue("isEnabled", true);

        return jdbcTemplate.query(sql, params, PROJECT_MAPPER).get(0);
    }

    @Override
    public List<Project> fetchProjects() {
        var sql = """
                SELECT p.*, w.wrk_abbrevation, e.* FROM project p
                INNER JOIN workplace w ON w.workplace_id=p.pro_workplace_id
                INNER JOIN employee e ON e.employee_id=p.pro_manager_id
                WHERE p.pro_enabled=:isEnabled
                """;

        var params = new MapSqlParameterSource();
        params.addValue("isEnabled", true);

        return jdbcTemplate.query(sql, params, PROJECT_MAPPER);
    }

    @Override
    public List<Employee> fetchProjectEmployees(long projectId) {
        var sql = """
                SELECT e.*, w.wrk_abbrevation FROM project_employee pe
                INNER JOIN employee e ON e.employee_id=pe.pre_employee_id
                INNER JOIN workplace w ON w.workplace_id=e.emp_workplace_id
                WHERE pe.pre_enabled=:isEnabled AND pe.pre_project_id=:project_id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("isEnabled", true);
        params.addValue("project_id", projectId);

        return jdbcTemplate.query(sql, params, EMPLOYEE_MAPPER);
    }

    @Override
    public boolean createProject(@NonNull Project project) {
        var sql = """
                INSERT INTO project
                (pro_enabled, pro_name, pro_manager_id, pro_workplace_id,\s
                pro_active_from, pro_active_until, pro_description)
                VALUES
                (:pro_enabled, :pro_name, :pro_manager_id, :pro_workplace_id,\s
                :pro_active_from, :pro_active_until, :pro_description);
                """;

        var params = new MapSqlParameterSource();
        params.addValue("pro_enabled", 1);
        params.addValue("pro_name", project.getProjectName());
        params.addValue("pro_manager_id", project.getProjectManager().getId());
        params.addValue("pro_workplace_id", project.getProjectWorkplace().getId());
        params.addValue("pro_active_from", project.getValidFrom());
        params.addValue("pro_active_until", project.getValidUntil());
        params.addValue("pro_description", project.getDescription());


        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean updateProject(@NonNull Project project, long projectId) {
        var sql = """
                UPDATE project
                SET pro_enabled = :pro_enabled, pro_name = :pro_name, pro_manager_id = :pro_manager_id,
                pro_workplace_id = :pro_workplace_id, pro_active_from = :pro_active_from, pro_description = :pro_description
                WHERE project_id = :project_id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("pro_enabled", 1);
        params.addValue("pro_name", project.getProjectName());
        params.addValue("pro_manager_id", project.getProjectManager().getId());
        params.addValue("pro_workplace_id", project.getProjectWorkplace().getId());
        params.addValue("pro_active_from", project.getValidFrom());
        params.addValue("pro_active_until", project.getValidUntil());
        params.addValue("pro_description", project.getDescription());
        params.addValue("project_id", projectId);

        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean removeProject(long projectId) {
        var sql = """
                UPDATE project
                SET pro_enabled = :pro_enabled
                WHERE pro_enabled =:isEnabled AND project_id = :project_id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("pro_enabled", true);
        params.addValue("project_id", projectId);
        params.addValue("isEnabled", true);

        allocationRepository.fetchProjectAllocations(projectId)
                .forEach(allocation -> allocationRepository.removeAllocation(allocation.getId()));

        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean addEmployee(long employeeId, long projectId) {
        var sql = """
                INSERT INTO project_employee
                (pre_enabled, pre_project_id, pre_employee_id)
                VALUES
                (:pre_enabled, :pre_project_id, :pre_employee_id)
                """;

        var params = new MapSqlParameterSource();
        params.addValue("pre_enabled", true);
        params.addValue("pre_project_id", projectId);
        params.addValue("pre_employee_id", employeeId);


        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean removeEmployee(long employeeId, long projectId) {
        var sql = """
                UPDATE project_employee
                SET pre_enabled = :pro_enabled
                WHERE pro_enabled =:isEnabled AND pre.project_id = :project_id AND pre.employee_id=:employee_id
                """;

        var params = new MapSqlParameterSource();
        params.addValue("pre_enabled", false);
        params.addValue("project_id", projectId);
        params.addValue("isEnabled", true);
        params.addValue("employee_id", employeeId);

        allocationRepository.fetchProjectAllocations(projectId)
                .forEach(allocation -> {
                    if (allocation.getWorker().getId() == employeeId)
                        allocationRepository.removeAllocation(allocation.getId());
                });

        return jdbcTemplate.update(sql, params) == 1;
    }
}