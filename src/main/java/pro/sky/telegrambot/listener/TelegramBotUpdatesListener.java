package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Notification_task;
import pro.sky.telegrambot.repository.Notification_task_repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final Notification_task_repository notificationTaskRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      Notification_task_repository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message mess = update.message();
            Long chatId = update.message().chat().id();

            if (mess.text().equals("/start")) {
                SendMessage sendMess = new SendMessage(chatId, "Привет, Чемпион");
                SendResponse response = telegramBot.execute(sendMess);
            }

            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(mess.text());
            if (matcher.matches()) {
                String date = matcher.group(1);
                String item = matcher.group(3);
                LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                Notification_task notificationTask = new Notification_task();
                notificationTask.setChart_id(chatId);
                notificationTask.setMessage(item);
                notificationTask.setDate_time(ldt);

                notificationTaskRepository.save(notificationTask);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void getListNotific() {
        List<Notification_task> listNotif = notificationTaskRepository.findAll();
        listNotif.stream()
                .filter(not -> not.getDate_time().truncatedTo(ChronoUnit.MINUTES)
                        .equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .forEach(notif -> {
                    Long chatId = notif.getChart_id();
                    SendMessage sendMess = new SendMessage(chatId, notif.getMessage());
                    SendResponse response = telegramBot.execute(sendMess);
                });

    }

}
