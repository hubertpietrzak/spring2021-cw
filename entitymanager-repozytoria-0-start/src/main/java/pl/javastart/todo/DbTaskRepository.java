package pl.javastart.todo;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
class DbTaskRepository implements TaskRepository {
    private final EntityManager entityManager;
    private static long nextId = 1;

    public DbTaskRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Task save(Task task) {
        task.setId(nextId);
        entityManager.persist(task);
        nextId++;
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Task.class, id));
    }
}