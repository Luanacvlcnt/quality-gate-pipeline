package dev.qgp.tasks.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Testa só a camada HTTP (rotas, status, JSON, validação), com o service simulado. */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskService service;

    @Test
    void listaTarefas() throws Exception {
        when(service.list()).thenReturn(List.of(new TaskResponse(1L, "Estudar", null, false)));

        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Estudar"));
    }

    @Test
    void retorna404QuandoTarefaNaoExiste() throws Exception {
        when(service.get(99L)).thenThrow(new TaskNotFoundException(99L));

        mvc.perform(get("/tasks/99")).andExpect(status().isNotFound());
    }

    @Test
    void criaTarefaERetorna201ComLocation() throws Exception {
        when(service.create(any())).thenReturn(new TaskResponse(1L, "Estudar", null, false));

        mvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Estudar\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/tasks/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void rejeitaTarefaSemTitulo() throws Exception {
        mvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void removeTarefaERetorna204() throws Exception {
        mvc.perform(delete("/tasks/1")).andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
