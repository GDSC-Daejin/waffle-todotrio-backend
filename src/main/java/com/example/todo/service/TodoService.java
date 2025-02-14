    package com.example.todo.service;

    import com.example.todo.dto.TodoDto;
    import com.example.todo.dto.TodoRequestDto;
    import com.example.todo.dto.TodoSearchRequestDto;
    import com.example.todo.dto.TodoStatisticsDto;
    import com.example.todo.entity.Todo;
    import com.example.todo.entity.TodoHistory;
    import com.example.todo.entity.User;
    import com.example.todo.enums.ActionType;
    import com.example.todo.enums.TodoStatus;
    import com.example.todo.repository.TodoHistoryRepository;
    import com.example.todo.repository.TodoRepository;
    import com.example.todo.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class TodoService {
        private final TodoRepository todoRepository;
        private final TodoHistoryRepository historyRepository;

        private final MLService mlService; // 머신러닝 관련 서비스 추가

        /**
         * 새로운 Todo 생성
         * @param //todoDto Todo 생성 정보
         * @param user 생성자
         * @return 생성된 Todo
         */
        @Transactional
        public Todo createTodo(TodoRequestDto requestDto, User user) {
            Todo todo = new Todo();

            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            LocalDateTime now = LocalDateTime.now(seoulZone);

            todo.setTitle(requestDto.getTitle());
            todo.setContent(requestDto.getContent());
            todo.setPriority(requestDto.getPriority());
            todo.setStartDate(requestDto.getStartDate().atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(seoulZone).toLocalDateTime());
            todo.setDeadline(requestDto.getDeadline().atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(seoulZone).toLocalDateTime());
            todo.setStatus(TodoStatus.IN_PROGRESS);
            todo.setCreatedDate(now);
            todo.setOwner(user);


            // ML 서버에서 카테고리 예측
            String predictedCategory = mlService.predictCategory(
                    requestDto.getContent(),
                    requestDto.getTitle()
            );
            todo.setCategory(predictedCategory);

            return todoRepository.save(todo);
        }

        /**
         * Todo 수정
         * @param todoId 수정할 Todo ID
         * @param //todoDto 수정할 내용
         * @param user 수정자
         * @return 수정된 Todo
         */
        @Transactional
        public Todo updateTodo(Long todoId, TodoRequestDto requestDto, User user) {

            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            todo.setTitle(requestDto.getTitle());
            todo.setContent(requestDto.getContent());
            todo.setPriority(requestDto.getPriority());
            todo.setStartDate(requestDto.getStartDate().atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(seoulZone).toLocalDateTime());
            todo.setDeadline(requestDto.getDeadline().atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(seoulZone).toLocalDateTime());

            String predictedCategory = mlService.predictCategory(
                    requestDto.getContent(),
                    requestDto.getTitle()
            );
            todo.setCategory(predictedCategory);
            return todo;
        }

        /**
         * Todo 삭제
         * @param todoId 삭제할 Todo ID
         * @param user 삭제자
         */
        @Transactional
        public void deleteTodo(Long todoId, User user) {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            // 현재 사용자와 Todo 소유자 일치 여부 확인
            if (!todo.getOwner().getId().equals(user.getId())) {
                throw new RuntimeException("삭제 권한이 없습니다.");
            }

            // TodoHistory 먼저 삭제
            historyRepository.deleteByTodo(todo);

            // Todo 삭제
            todoRepository.delete(todo);
        }

        /**
         * Todo 완료 처리
         * @param todoId 완료할 Todo ID
         * @param user 완료 처리자
         * @return 완료된 Todo
         */
        @Transactional
        public Todo completeTodo(Long todoId, User user) {
            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            todo.setStatus(TodoStatus.COMPLETED);
            todo.setCompletedDate(LocalDateTime.now(seoulZone));

            mlService.trainModel(todo);

            recordHistory(todo, user, ActionType.COMPLETE);
            return todo;
        }

        /**
         * Todo 지연 처리
         * @param todoId 지연 처리할 Todo ID
         * @param user 처리자
         * @return 지연 처리된 Todo
         */
        @Transactional
        public Todo delayTodo(Long todoId, User user) {
            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            // 이미 완료된 할일인 경우 예외 처리
            if (todo.getStatus() == TodoStatus.COMPLETED) {
                throw new IllegalStateException("이미 완료된 할일은 지연 처리할 수 없습니다.");
            }

            todo.setStatus(TodoStatus.DELAYED);

            // 마감기한 연장 (1주일)
            if (todo.getDeadline() != null) {
                todo.setDeadline(todo.getDeadline().plusWeeks(1));
            }
            // 지연 처리 시 ML 서버에 학습 데이터 전송
            mlService.trainModel(todo);
            // 이력 기록
            recordHistory(todo, user, ActionType.DELAYED);
            return todo;
        }
        // ml 확률 통신
        @Transactional
        public Double getPredictedSuccess(Long todoId) {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            return mlService.predictSuccess(todo);
        }

        /**
         * Todo 이력 기록
         * @param todo 대상 Todo
         * @param user 이력 생성자
         * @param actionType 이력 유형
         */
        private void recordHistory(Todo todo, User user, ActionType actionType) {
            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            TodoHistory history = new TodoHistory();
            history.setTodo(todo);
            history.setUser(user);
            history.setActionType(actionType);
            history.setCreatedDate(LocalDateTime.now(seoulZone));
            historyRepository.save(history);
        }
        @Transactional
        public List<Todo> getTodosByUser(User user) {
            return todoRepository.findByOwner(user);
        }

        public TodoStatisticsDto getTodoStatistics(User user) {
            List<Todo> todos = todoRepository.findByOwner(user);

            TodoStatisticsDto stats = new TodoStatisticsDto();

            // 완료율 계산
            long completedCount = todos.stream()
                    .filter(todo -> todo.getStatus() == TodoStatus.COMPLETED)
                    .count();
            stats.setCompletionRate(todos.isEmpty() ? 0 : (double) completedCount / todos.size() * 100);

            // 우선순위 분포
            stats.setPriorityDistribution(
                    todos.stream().collect(Collectors.groupingBy(
                            Todo::getPriority,
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ))
            );

            // 상태 분포
            stats.setStatusDistribution(
                    todos.stream().collect(Collectors.groupingBy(
                            Todo::getStatus,
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ))
            );

            return stats;
        }

        public List<TodoDto> searchTodos(TodoSearchRequestDto searchDto, User user) {
            List<Todo> todos = todoRepository.findByOwner(user);

            // 검색 조건에 따라 필터링
            return todos.stream()
                    .filter(todo -> searchDto.getKeyword() == null ||
                            todo.getTitle().contains(searchDto.getKeyword()) ||
                            (todo.getContent() != null && todo.getContent().contains(searchDto.getKeyword())))
                    .filter(todo -> searchDto.getStatus() == null ||
                            todo.getStatus() == searchDto.getStatus())
                    .filter(todo -> searchDto.getPriority() == null ||
                            todo.getPriority() == searchDto.getPriority())
                    .filter(todo -> searchDto.getStartDate() == null ||
                            todo.getDeadline().isAfter(searchDto.getStartDate()))
                    .filter(todo -> searchDto.getEndDate() == null ||
                            todo.getDeadline().isBefore(searchDto.getEndDate()))
                    .map(TodoDto::from)
                    .collect(Collectors.toList());
        }

        // 어드민 관련 코드
        public List<TodoDto> getAllTodos() {
            return todoRepository.findAll().stream()
                    .map(TodoDto::from)
                    .collect(Collectors.toList());
        }

        public Todo updateAnyTodo(Long todoId, TodoRequestDto requestDto) {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            todo.setTitle(requestDto.getTitle());
            todo.setContent(requestDto.getContent());
            todo.setPriority(requestDto.getPriority());
            todo.setDeadline(requestDto.getDeadline());

            String predictedCategory = mlService.predictCategory(
                    requestDto.getContent(),
                    requestDto.getTitle()
            );
            todo.setCategory(predictedCategory);

            return todoRepository.save(todo);
        }


    }