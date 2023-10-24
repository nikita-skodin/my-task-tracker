package com.skodin.services;

import com.skodin.exceptions.BagRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.repositories.TaskStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskStateService {

    private final TaskStateRepository taskStateRepository;

    public List<TaskStateEntity> findAll() {
        return taskStateRepository.findAll();
    }

    public TaskStateEntity findById(Long aLong) {
        return taskStateRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Task State with id " + aLong + " did not found"));
    }

    @Transactional
    public <S extends TaskStateEntity> S saveAndFlush(S entity) {
        return taskStateRepository.saveAndFlush(entity);
    }

    @Transactional
    public <S extends TaskStateEntity> S save(S entity) {
        return taskStateRepository.save(entity);
    }

    @Transactional
    public TaskStateEntity update(Long id, TaskStateEntity stateFromURl) {

        TaskStateEntity stateFromDB = findById(id);

        TaskStateEntity previousStateFromDB = stateFromDB.getPreviousTaskState().orElse(null);
        TaskStateEntity previousStateFromURL = stateFromURl.getPreviousTaskState().orElse(null);

        if (!Objects.equals(previousStateFromDB, previousStateFromURL)) {

            if (previousStateFromURL != null && !Objects.equals(previousStateFromURL.getProject(), stateFromDB.getProject())) {
                throw new BagRequestException("Projects should not be different");
            }

            TaskStateEntity previousFromUrl = stateFromURl.getPreviousTaskState().orElse(null);

            if (previousFromUrl == null) {
                //логика добавления как самый первый элемент
                System.err.println("Добавляем в начало");
                Optional<TaskStateEntity> firstState = taskStateRepository
                        .findTaskStateEntityByPreviousTaskStateNullAndProject(stateFromDB.getProject());

                if (firstState.isPresent()) {

                    snatchElement(stateFromDB);

                    stateFromDB.setPreviousTaskState(null);
                    stateFromDB.setNextTaskState(firstState.get());
                    firstState.get().setPreviousTaskState(stateFromDB);

                    saveAndFlush(firstState.get());
                } else {
                    // логика если он первый и единственный
                    // по идее этот кейс никогда не сработает
                    throw new BagRequestException("There is no other elements");
                }
            } else {
                TaskStateEntity workElement = previousFromUrl.getNextTaskState().orElse(null);

                if (workElement == null) {
                    //логика добавления как последний элемент
                    System.err.println("Добавляем в конец");

                    Optional<TaskStateEntity> lastState = taskStateRepository
                            .findTaskStateEntityByNextTaskStateNullAndProject(stateFromDB.getProject());

                    if (lastState.isPresent()) {

                        snatchElement(stateFromDB);

                        stateFromDB.setNextTaskState(null);
                        lastState.get().setNextTaskState(stateFromDB);
                        stateFromDB.setPreviousTaskState(lastState.get());

                        saveAndFlush(lastState.get());
                    } else {
                        // логика если он первый и единственный
                        throw new BagRequestException("There is no other elements");
                    }

                } else {
                    // логика если меняем два обычных элемента
                    System.err.println("Просто обновляем");

                    snatchElement(stateFromDB);

                    stateFromDB.setNextTaskState(workElement);
                    workElement.setPreviousTaskState(stateFromDB);
                    stateFromDB.setPreviousTaskState(previousFromUrl);
                    previousFromUrl.setNextTaskState(stateFromDB);

                    saveAndFlush(previousFromUrl);
                    saveAndFlush(workElement);
                }
            }
        }

        stateFromDB.setName(stateFromURl.getName());
        stateFromDB.setCreatedAt(stateFromURl.getCreatedAt());

        return saveAndFlush(stateFromDB);
    }

    @Transactional
    public void deleteById(Long aLong) {
        findById(aLong);    // just check does object exist
        taskStateRepository.deleteById(aLong);
    }

    public Optional<TaskStateEntity> findTaskStateEntityByNameAndProject(String name, ProjectEntity project) {
        return taskStateRepository.findTaskStateEntityByNameAndProject(name, project);
    }

    private void snatchElement(TaskStateEntity stateFromDB) {
        stateFromDB.getNextTaskState().ifPresent(s -> s.setPreviousTaskState(stateFromDB
                .getPreviousTaskState().orElse(null)));
        stateFromDB.getPreviousTaskState().ifPresent(s -> s.setNextTaskState(stateFromDB.getNextTaskState().orElse(null)));
    }
}
