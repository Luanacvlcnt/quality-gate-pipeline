package dev.qgp.tasks.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo aceito no POST e no PUT. A validação roda antes de chegar no service. */
public record TaskRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 1000) String description,
        Boolean done) {

    /** "done" é opcional no JSON; se vier ausente, a tarefa começa como não concluída. */
    public TaskRequest {
        done = Boolean.TRUE.equals(done);
    }
}
