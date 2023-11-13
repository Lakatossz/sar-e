package cz.zcu.kiv.mjakubas.piae.sem.core.service;

import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Employee;
import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Project;
import cz.zcu.kiv.mjakubas.piae.sem.core.repository.IUserRepository;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.EmployeeService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.ProjectService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.WorkplaceService;
import cz.zcu.kiv.mjakubas.piae.sem.core.vo.EmployeeVO;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Represents service for handling security tasks -> such as creating new user or changing his password.
 * Also provides method for securing individual mvc end-points.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class SecurityService {

    private IUserRepository userRepository;
    private EmployeeService employeeService;
    private ProjectService projectService;
    private WorkplaceService workplaceService;

    private PasswordEncoder passwordEncoder;

    /**
     * Creates new user. Proper {@link Employee} must already exist as this only creates wrapper for spring boot.
     *
     * @param orionLogin existing {@link Employee} orion login
     * @param password   plaintext password
     */
    @Transactional
    public void createUser(@NonNull String orionLogin, @NonNull String password) {
        var employee = employeeService.getEmployee(orionLogin);
        this.createUserUser(employee.getId(), password);
        this.createUserRole(employee.getOrionLogin());
    }

    /**
     * Creates new user. As creating user consist of 2 task -> firstly create new user -> secondly create new user role.
     * This method does first task.
     *
     * @param id       user id
     * @param password plaintext password
     */
    @Transactional
    public void createUserUser(@NonNull long id, @NonNull String password) {
        var pw = passwordEncoder.encode(password);
        if (!userRepository.createNewUser(id, pw)) {
            throw new ServiceException();
        }
    }

    /**
     * Creates new user. As creating user consist of 2 task -> firstly create new user -> secondly create new user role.
     * This method does second task.
     *
     * @param orionLogin employee orion login
     */
    @Transactional
    public void createUserRole(@NonNull String orionLogin) {
        if (!userRepository.addUserRole(orionLogin)) {
            throw new ServiceException();
        }
    }

    /**
     * Updates user password -> this also makes new user password not temporary
     * -> thus allowing user to use the application.
     *
     * @param orionLogin user orion login
     * @param password   new plaintext password
     */
    @Transactional
    public void updateUserPassword(@NonNull String orionLogin, @NonNull String password) {
        var employee = employeeService.getEmployee(orionLogin);

        var pw = passwordEncoder.encode(password);
        if (!userRepository.updatePassword(employee.getId(), pw)) {
            throw new ServiceException();
        }
    }

    /**
     * Checks if logged user has temporary password.
     *
     * @param orionLogin logged user orion login
     * @return true if temporary, false otherwise
     */
    public boolean isTemporary(@NonNull String orionLogin) {
        return userRepository.isTemporary(orionLogin);
    }

    /**
     * Checks if parameter id is current logged user id.
     *
     * @param id possible user id
     * @return true if yes, no otherwise
     */
    public boolean isUserView(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var employee = employeeService.getEmployee(id);


        return auth.getName().equals(employee.getOrionLogin());
    }

    /**
     * Checks if parameter id is current logged user id.
     *
     * @param id possible user id
     * @return true if yes, no otherwise
     */
    public boolean isSuperiorView(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var employee = employeeService.getEmployee(id);

        return auth.getName().equals(employee.getOrionLogin());
    }

    /**
     * Checks if current logged user is project manager of parameter id of a project.
     *
     * @param projectId project id
     * @return true if yes, otherwise no
     */
    public boolean isProjectManager(Long projectId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var employee = employeeService.getEmployee(auth.getName());
        var project = projectService.getProject(projectId);

        return project.getProjectManager().getId() == employee.getId();
    }

    /**
     * Checks if current logged user is at least project manager.
     *
     * @return true if yes, otherwise false
     */
    public boolean isAtLeastProjectManager() {
        var projects = projectService.getProjects();
        for (Project p : projects) {
            if (isProjectManager(p.getId()))
                return true;
        }

        return false;
    }

    /**
     * Check if current logged user is workplace manager of project.
     *
     * @param projectId project id
     * @return true if yes, otherwise false
     */
    public boolean isWorkplaceManager(int projectId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var employee = employeeService.getEmployee(auth.getName());
        var project = projectService.getProject(projectId);
        var workplace = workplaceService.getWorkplace(projectId);

        return workplace.getManager().getId() == employee.getId();
    }

    /**
     * Creates new user account
     *
     * @param employeeVO employee VO
     */
    @Transactional
    public void createUserAccount(@NonNull EmployeeVO employeeVO) {
        employeeService.createEmployee(employeeVO);
        this.createUser(employeeVO.getOrionLogin(), employeeVO.getPassword());
    }
}
