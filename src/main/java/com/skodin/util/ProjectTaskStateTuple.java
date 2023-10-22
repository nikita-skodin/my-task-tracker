package com.skodin.util;

import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Не дженерик для наглядности,
 * не record для того чтобы не подтягивал ненужную дату
 * для toString и тд
 */
@Getter
@AllArgsConstructor
public class ProjectTaskStateTuple {
    private final ProjectEntity project;
    private final TaskStateEntity taskState;
}

