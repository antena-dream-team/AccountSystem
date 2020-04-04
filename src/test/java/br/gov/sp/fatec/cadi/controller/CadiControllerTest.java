package br.gov.sp.fatec.cadi.controller;

import br.gov.sp.fatec.cadi.controller.CadiController;
import br.gov.sp.fatec.cadi.domain.Cadi;
import br.gov.sp.fatec.cadi.service.CadiService;
import br.gov.sp.fatec.project.domain.Project;
import br.gov.sp.fatec.project.domain.Status;
import br.gov.sp.fatec.project.service.ProjectService;
import br.gov.sp.fatec.teacher.domain.Teacher;
import org.assertj.core.util.Lists;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static br.gov.sp.fatec.cadi.fixture.CadiFixture.newCadi;
import static br.gov.sp.fatec.project.fixture.ProjectFixture.*;
import static br.gov.sp.fatec.teacher.fixture.TeacherFixture.newTeacher;
import static br.gov.sp.fatec.utils.commons.JSONParser.toJSON;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CadiControllerTest {

    private static final String URL = "/dev/cadi";

    @InjectMocks
    private CadiController controller;

    @Mock
    private CadiService service;

    @Mock
    private ProjectService projectService;

    private MockMvc mockMvc;

    @Before
    public void onInit() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    public void create_shouldSucceed() throws Exception {
        Cadi cadi = newCadi();
        when(service.save(cadi)).thenReturn(cadi);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(cadi))))
                .andExpect(status().isCreated());

        verify(service).save(cadi);
    }

    @Test
    public void findAll_shouldSucceed() throws Exception {
        List<Cadi> cadiList = Lists.newArrayList(newCadi(1L, true),
                newCadi(2L, true),
                newCadi(3L, true));

        when(service.findAll()).thenReturn(cadiList);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());

        verify(service).findAll();
    }

    @Test
    public void findActive_shouldSucceed() throws Exception {
        List<Cadi> cadiList = Lists.newArrayList(newCadi(1L, true),
                newCadi(2L, true),
                newCadi(3L, true),
                newCadi(4L, true),
                newCadi(5L, true));

        when(service.findActive()).thenReturn(cadiList);

        mockMvc.perform(get(URL + "/active"))
                .andExpect(status().isOk());
    }

    @Test
    public void findById_shouldSucceed() throws Exception {
        Cadi cadi = newCadi();
        when(service.findById(cadi.getId())).thenReturn(cadi);

        mockMvc.perform(get(URL + "/" + cadi.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void update_ShouldSucceed() throws Exception {
        Cadi cadi = newCadi();
        Cadi updated = newCadi();
        updated.setEmail("newEmail@test.com");

        mockMvc.perform(put(URL + "/" + cadi.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(cadi))))
                .andExpect(status().isOk());
    }

    @Test
    public void activate_shouldSucceed() throws Exception {
        Cadi cadi = newCadi();
        JSONObject base64 = new JSONObject();
        base64.put("dateTime", new Date());
        base64.put("email", cadi.getEmail());
        String b64 = Base64.getEncoder().encodeToString(base64.toString().getBytes());

        mockMvc.perform(get(URL + "/activate/" + b64))
                .andExpect(status().isOk());
    }

    @Test
    public void setTeacher_shouldSucceed() throws Exception {
        Project project = newProject();
        when(service.setTeacher(1L, project.getId())).thenReturn(project);

        mockMvc.perform(put(URL + "/set-teacher/" + 1L + "/" + project.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void setStatus_shouldSucceed() throws Exception {
        Project project = newProject();
        Status status = newStatus();

        when(service.setProjectStatus(project.getId(), status)).thenReturn(project);

        mockMvc.perform(put(URL + "/set-project-status/" + project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(status))))
                .andExpect(status().isOk());
    }

    @Test
    public void login_shouldSucceed() throws Exception {
        Cadi cadi = newCadi();

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("email", cadi.getEmail());
        loginMap.put("password", cadi.getPassword());

        when(service.login(loginMap)).thenReturn(cadi);

        JSONObject loginObject = new JSONObject();
        loginObject.put("email", cadi.getEmail());
        loginObject.put("password", cadi.getPassword());

        mockMvc.perform(post(URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(loginObject)))
                .andExpect(status().isOk());
    }

    @Test
    public void setPossibleDate_shouldSucceed() throws Exception {
        Project project = newProject();
        List<br.gov.sp.fatec.project.domain.Date> possibleDate = getPossibleDate();

        when(service.setMeetingPossibleDate(possibleDate, project.getId())).thenReturn(project);

        mockMvc.perform(post(URL + "/set-possible-date/" + project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(possibleDate))))
                .andExpect(status().isOk());
    }

    @Test
    public void approve_shouldSucceed() throws Exception {
        Project project = newProject();
        when(projectService.approve(project.getId())).thenReturn(project);
        mockMvc.perform(put(URL + "/approve/" + project.getId()))
                .andExpect(status().isOk());
    }
}
