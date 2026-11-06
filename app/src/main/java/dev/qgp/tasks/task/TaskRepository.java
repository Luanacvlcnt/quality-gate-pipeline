package dev.qgp.tasks.task;

import org.springframework.data.jpa.repository.JpaRepository;

/** O Spring Data gera a implementação (findAll, findById, save, delete...) em tempo de execução. */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
