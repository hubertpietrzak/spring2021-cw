package pl.javastart.todo;

import org.springframework.stereotype.Service;
import pl.javastart.todo.dto.NewTaskDto;
import pl.javastart.todo.dto.TaskDurationDto;
import pl.javastart.todo.exception.TaskAlreadyCompletedException;
import pl.javastart.todo.exception.TaskAlreadyStartedException;
import pl.javastart.todo.exception.TaskNotFoundException;
import pl.javastart.todo.exception.TaskNotStartedException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
class TaskService {
    private static long nextId = 1;
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Long saveTask(NewTaskDto task) {
        Task taskToSave = new Task(task.getTitle(), task.getDescription(), task.getPriority());
        taskToSave.setId(nextId);
        Task savedTask = taskRepository.save(taskToSave);
        nextId++;
        return savedTask.getId();
    }

    public Optional<String> getTaskInfo(Long taskId) {
        return taskRepository.findById(taskId).map(Task::toString);
    }

    @Transactional
    public LocalDateTime startTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        if (task.getStartTime() != null) {
            throw new TaskAlreadyStartedException();
        }
        task.setStartTime(LocalDateTime.now());
        return task.getStartTime();
    }

    @Transactional
    public TaskDurationDto completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        if (task.getStartTime() == null) {
            throw new TaskNotStartedException();
        } else if (task.getCompletionTime() != null) {
            throw new TaskAlreadyCompletedException();
        }
        task.setCompletionTime(LocalDateTime.now());
        return new TaskDurationDto(task.getStartTime(), task.getCompletionTime());
    }
    List<String> getAllNotStartedTasksInfo() {
        return taskRepository.findAllByStartTimeIsNullOrderByPriorityDesc()
                .stream()
                .map(Task::toString)
                .toList();
    }

    List<String> getAllCompletedTasksInfo() {
        return taskRepository.findAllByCompletionTimeNotNullOrderByCompletionTimeDesc()
                .stream()
                .map(Task::toString)
                .toList();
    }

}