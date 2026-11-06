package dev.qgp.tasks.task;

/** O que a API devolve. Separado da entidade para não expor o modelo do banco diretamente. */
public record TaskResponse(Long id, String title, String description, boolean done) {

    static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isDone());
    }
}
