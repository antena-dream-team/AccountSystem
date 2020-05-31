package br.gov.sp.fatec.teacher.controller;

import br.gov.sp.fatec.project.domain.Project;
import br.gov.sp.fatec.project.view.ProjectView;
import br.gov.sp.fatec.student.domain.Student;
import br.gov.sp.fatec.teacher.domain.Teacher;
import br.gov.sp.fatec.teacher.exception.TeacherException.*;
import br.gov.sp.fatec.teacher.service.TeacherService;
import br.gov.sp.fatec.teacher.view.TeacherView;
import br.gov.sp.fatec.utils.exception.NotFoundException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@CrossOrigin
@RequestMapping("dev/teacher")
public class TeacherController {

    @Autowired
    private TeacherService service;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    @JsonView(TeacherView.Teacher.class)
    public Teacher create (@RequestBody Teacher teacher) {
        return service.save(teacher);
    }

    @GetMapping(produces =  APPLICATION_JSON_VALUE)
    @JsonView(TeacherView.Teacher.class)
    public List<Teacher> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/active")
    @JsonView(TeacherView.Teacher.class)
    public List<Teacher> findActive() {
        return service.findActive();
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @JsonView(TeacherView.Teacher.class)
    public Teacher findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PutMapping(value = "/{id}")
    @JsonView(TeacherView.Teacher.class)
    public Teacher update(@PathVariable("id") Long id,
                   @RequestBody Teacher teacher) {
        return  service.save(teacher);
    }
    @GetMapping(value = "/activate/{b64}")
    public void activate(@PathVariable("b64") String b64) {
        service.activate(b64);
    }
}

