package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.Notification_task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.*;

public interface Notification_task_repository extends JpaRepository<Notification_task, Long> {

}
