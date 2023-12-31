package cz.zcu.kiv.mjakubas.piae.sem.webapplication.controller;

import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.EmployeeService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.ProjectService;
import cz.zcu.kiv.mjakubas.piae.sem.core.vo.AllocationCellVO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Controls index view.
 */
@Controller
@RequestMapping("/")
@AllArgsConstructor
public class IndexController {

    private final EmployeeService employeeService;

    private final ProjectService projectService;

    @GetMapping("/")
    public String red() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String viewIndex(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var me = employeeService.getEmployee(auth.getName());
        var myProjects = projectService.getManagerProjects(me.getId());
        var managerProjects = projectService.getWorkplaceManagerProjects(me.getId());

        var employees = employeeService.getEmployees();
        employees.forEach(employee ->
                employee.getSubordinates().addAll(employeeService.getSubordinates(employee.getId())));

        employees.forEach(employeeService::prepareProjectsCells);
        employees.forEach(employeeService::prepareCoursesCells);
        employees.forEach(employeeService::prepareFunctionsCells);
        employees.forEach(employeeService::prepareTotalCells);

        model.addAttribute("employees", employees);

        model.addAttribute("me", me);
        model.addAttribute("myProjects", myProjects);
        model.addAttribute("workplaceProjects", managerProjects);
        return "main";
    }
}
