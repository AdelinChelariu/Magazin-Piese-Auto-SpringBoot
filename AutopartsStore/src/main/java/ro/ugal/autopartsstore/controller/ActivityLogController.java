package ro.ugal.autopartsstore.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.activity.ActivityLogResponse;
import ro.ugal.autopartsstore.repository.ActivityLogRepository;
import ro.ugal.autopartsstore.spec.ActivityLogFilter;
import ro.ugal.autopartsstore.spec.ActivityLogSpecifications;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activity-log")
public class ActivityLogController {

    private final ActivityLogRepository repo;

    public ActivityLogController(ActivityLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<ActivityLogResponse> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        ActivityLogFilter f = new ActivityLogFilter();
        f.username = username;
        f.action = action;
        f.entity = entity;
        f.success = success;
        f.from = from;
        f.to = to;

        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        var spec = ActivityLogSpecifications.byFilter(f);

        return repo.findAll(spec, pageable).map(l -> {
            ActivityLogResponse r = new ActivityLogResponse();
            r.id = l.getId();
            r.username = (l.getUser() != null) ? l.getUser().getUsername() : null;
            r.roleSnapshot = l.getRoleSnapshot();

            r.action = l.getAction();
            r.entity = l.getEntity();
            r.entityId = l.getEntityId();

            r.success = l.isSuccess();
            r.errorMessage = l.getErrorMessage();

            r.ipAddress = l.getIpAddress();
            r.createdAt = l.getCreatedAt();
            return r;
        });
    }
}
