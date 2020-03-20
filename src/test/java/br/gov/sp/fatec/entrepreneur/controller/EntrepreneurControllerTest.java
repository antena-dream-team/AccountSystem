package br.gov.sp.fatec.entrepreneur.controller;

import br.gov.sp.fatec.entrepreneur.domain.Entrepreneur;
import br.gov.sp.fatec.entrepreneur.service.EntrepreneurService;
import org.assertj.core.util.Lists;
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

import java.util.List;
import java.util.Objects;

import static br.gov.sp.fatec.entrepreneur.fixture.EntrepreneurFixture.newEntrepreneur;
import static br.gov.sp.fatec.utils.commons.JSONParser.toJSON;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EntrepreneurControllerTest {
    private static final String URL = "/dev/entrepreneur";

    @InjectMocks
    private EntrepreneurController controller;

    @Mock
    private EntrepreneurService service;

    private MockMvc mockMvc;

    @Before
    public void onInit() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    public void create_shouldSucceed() throws Exception {
        Entrepreneur entrepreneur = newEntrepreneur();
        when(service.save(entrepreneur)).thenReturn(entrepreneur);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(entrepreneur))))
                .andExpect(status().isCreated());

        verify(service).save(entrepreneur);
    }

    @Test
    public void findAll_shouldSucceed() throws Exception {
        List<Entrepreneur> entrepreneurList = Lists.newArrayList(newEntrepreneur(1L, true),
                newEntrepreneur(2L, true),
                newEntrepreneur(3L, true));

        when(service.findAll()).thenReturn(entrepreneurList);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());

        verify(service).findAll();
    }

    @Test
    public void findActive_shouldSucceed() throws Exception {
        List<Entrepreneur> entrepreneurList = Lists.newArrayList(newEntrepreneur(1L, true),
                newEntrepreneur(2L, true),
                newEntrepreneur(3L, true),
                newEntrepreneur(4L, true),
                newEntrepreneur(5L, true));

        when(service.findActive()).thenReturn(entrepreneurList);

        mockMvc.perform(get(URL + "/active"))
                .andExpect(status().isOk());
    }

    @Test
    public void findById_shouldSucceed() throws Exception {
        Entrepreneur entrepreneur = newEntrepreneur();
        when(service.findById(entrepreneur.getId())).thenReturn(entrepreneur);

        mockMvc.perform(get(URL + "/" + entrepreneur.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void update_ShouldSucceed() throws Exception {
        Entrepreneur entrepreneur = newEntrepreneur();
        Entrepreneur updated = newEntrepreneur();
        updated.setEmail("newEmail@test.com");

        mockMvc.perform(put(URL + "/" + entrepreneur.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(toJSON(entrepreneur))))
                .andExpect(status().isOk());
    }

    @Test
    public void deactivate_shouldSucceed() throws Exception {
        Entrepreneur entrepreneur = newEntrepreneur();
        entrepreneur.setActive(false);

        mockMvc.perform(delete(URL + "/" + entrepreneur.getId()))
                .andExpect(status().isOk());
    }
}