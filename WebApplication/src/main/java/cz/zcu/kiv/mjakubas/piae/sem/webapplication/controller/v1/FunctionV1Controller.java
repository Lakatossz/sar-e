package cz.zcu.kiv.mjakubas.piae.sem.webapplication.controller.v1;

import cz.zcu.kiv.mjakubas.piae.sem.core.domain.Function;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.AllocationService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.EmployeeService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.FunctionService;
import cz.zcu.kiv.mjakubas.piae.sem.core.service.v1.WorkplaceService;
import cz.zcu.kiv.mjakubas.piae.sem.core.vo.EmployeeVO;
import cz.zcu.kiv.mjakubas.piae.sem.core.vo.FunctionVO;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contains all sites for working with functions.
 */
@Controller
@RequestMapping("/f")
@AllArgsConstructor
@PreAuthorize("hasAuthority(T(cz.zcu.kiv.mjakubas.piae.sem.webapplication.security.SecurityAuthority).SECRETARIAT)")
public class FunctionV1Controller {

    private final FunctionService functionService;
    private final EmployeeService employeeService;
    private final WorkplaceService workplaceService;

    private final AllocationService allocationService;

    @GetMapping()
    public String getFunctions(Model model) {
        var functions = functionService.getFunctions();

        model.addAttribute("functions", functions);
        return "views/functions";
    }

    @GetMapping("/create")
    public String createFunction(Model model) {
        model.addAttribute("functionVO", new FunctionVO());

        var functions = functionService.getFunctions();
        var employees = employeeService.getEmployees();
        var workplaces = workplaceService.getWorkplaces();

        model.addAttribute("employees", employees);
        model.addAttribute("workplaces", workplaces);
        model.addAttribute("restrictions", functions);

        return "forms/function/create_function_form";
    }

    @PostMapping("/create")
    public String createFunction(@ModelAttribute FunctionVO functionVO, BindingResult bindingResult, Model model) {

        functionService.createFunction(functionVO);

        return "redirect:/f?create=success";
    }

    @PreAuthorize("hasAuthority(T(cz.zcu.kiv.mjakubas.piae.sem.webapplication.security.SecurityAuthority).SECRETARIAT)" +
            " or @securityService.isFunctionManager(#id) or @securityService.isWorkplaceManager(#id)")
    @GetMapping("/{id}/edit")
    public String editFunction(Model model, @PathVariable long id,
                              @RequestParam(required = false) boolean manage) {
        Function data = functionService.getFunction(id);

        model.addAttribute("functionVO",
                new FunctionVO(
                        data.getId(),
                        data.getName(),
                        data.getDateFrom(),
                        data.getDateUntil(),
                        data.getProbability(),
                        data.getDefaultTime(),
                        data.getFunctionManager().getId(),
                        data.getFunctionWorkplace().getId()));

        model.addAttribute("employees", employeeService.getEmployees());
        model.addAttribute("workplaces", workplaceService.getWorkplaces());

        model.addAttribute("restrictions", functionService.getFunctions());

        model.addAttribute("manage", manage);

        return "forms/function/edit_function_form";
    }

    @PreAuthorize("hasAuthority(T(cz.zcu.kiv.mjakubas.piae.sem.webapplication.security.SecurityAuthority).SECRETARIAT)" +
            " or @securityService.isFunctionManager(#id) or @securityService.isWorkplaceManager(#id)")
    @PostMapping("/{id}/edit")
    public String editFunction(Model model, @PathVariable long id, @ModelAttribute FunctionVO functionVO,
                              BindingResult errors, @RequestParam(required = false) boolean manage) {
        Function data = functionService.getFunction(id);

        functionService.editFunction(functionVO, id);

        if (manage)
            return String.format("redirect:/f/%s/manage?edit=success", id);

        return "redirect:/f?edit=success";
    }

    @GetMapping("/{id}/delete")
    public String editFunction(Model model, @PathVariable long id) {
        return "redirect:/f?delete=success";
    }

    @GetMapping("/{id}/employee/add")
    public String addSubordinate(Model model, @PathVariable long id, @ModelAttribute EmployeeVO userVO) {
        model.addAttribute("userVO", new EmployeeVO());

        var function = functionService.getFunction(id);

        var restrictions = functionService.getFunctionEmployees(id);

        model.addAttribute("restrictions", restrictions);
        model.addAttribute("employees", employeeService.getEmployees());

        return "forms/function/create_function_employee_form";
    }

    @PostMapping("/{id}/employee/add")
    public String addSubordinate(Model model, @PathVariable long id, @ModelAttribute EmployeeVO userVO,
                                 BindingResult errors) {
        model.addAttribute("userVO", userVO);
        var function = functionService.getFunction(id);

        functionService.assignEmployee(userVO, function.getId());

        return "redirect:/f?add_employee=success";
    }

    @PreAuthorize("@securityService.isFunctionManager(#id) or @securityService.isWorkplaceManager(#id)")
    @GetMapping("/{id}/manage")
    public String manageFunction(Model model, @PathVariable long id) {
        var payload = allocationService.getFunctionAllocations(id);
        model.addAttribute("allocations", payload.getAllocations());
        model.addAttribute("function", payload.getAssignmentsFunctions());
        model.addAttribute("employees", payload.getAllocationsEmployees());
        return "views/function_management";
    }
}