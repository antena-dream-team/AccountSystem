package br.gov.sp.fatec.project.controller;

import br.gov.sp.fatec.project.domain.Project;
import br.gov.sp.fatec.project.service.ProjectService;
import br.gov.sp.fatec.project.view.ProjectView;
import br.gov.sp.fatec.utils.exception.NotFoundException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("dev/project")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(ProjectView.Project.class)
    public br.gov.sp.fatec.project.domain.Project create (@RequestBody br.gov.sp.fatec.project.domain.Project project) throws NotFoundException {

        return service.save(project);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    @JsonView(ProjectView.Project.class)
    public List<Project> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @JsonView(ProjectView.Project.class)
    public Project findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }
}
