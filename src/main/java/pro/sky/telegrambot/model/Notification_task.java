package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Notification_task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chart_id;
    private String message;
    private LocalDateTime date_time;

    public Notification_task() {
    }

    public Notification_task(Long id, Long chart_id, String message, LocalDateTime date_time) {
        this.id = id;
        this.chart_id = chart_id;
        this.message = message;
        this.date_time = date_time;
    }

    public Long getId() {
        return id;
    }

    public Long getChart_id() {
        return chart_id;
    }

    public void setChart_id(Long chart_id) {
        this.chart_id = chart_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification_task that = (Notification_task) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
