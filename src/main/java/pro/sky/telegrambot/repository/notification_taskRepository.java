package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.Notification_task;

public interface notification_taskRepository extends JpaRepository<Notification_task, Long> {
}
