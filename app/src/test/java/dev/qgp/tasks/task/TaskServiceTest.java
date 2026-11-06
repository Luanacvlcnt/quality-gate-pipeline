package dev.qgp.tasks.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Teste unitário puro: sem Spring e sem banco, o repository é um mock. */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void atualizaCamposDaTarefa() {
        Task task = new Task("Antigo", null, false);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = service.update(1L, new TaskRequest("Novo", "desc", true));

        assertThat(response.title()).isEqualTo("Novo");
        assertThat(response.description()).isEqualTo("desc");
        assertThat(response.done()).isTrue();
    }

    @Test
    void lancaExcecaoQuandoTarefaNaoExiste() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(42L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("42");
    }
}
