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

    // dependency injection with constructor
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
            Long chatId = update.message().chat().id(); // save chart id in variable

            // if the user sent the /start command
            if (mess.text().equals("/start")) {
                SendMessage sendMess = new SendMessage(chatId, "Привет, Чемпион");
                SendResponse response = telegramBot.execute(sendMess);
            }
            // pattern for checking messages sent by a user in a chat
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(mess.text());
            if (matcher.matches()) {
                String date = matcher.group(1); // date and time
                String item = matcher.group(3); // text
                //parsing date and time from a string
                LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                Notification_task notificationTask = new Notification_task(); // create new entity
                notificationTask.setChart_id(chatId);
                notificationTask.setMessage(item);
                notificationTask.setDate_time(ldt);

                notificationTaskRepository.save(notificationTask); // save entity to database
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /*
    the method runs every minute and checks if there are records with the correct date and time.
    If yes, then oct notification
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void getListNotific() {
        // get all notifications
        List<Notification_task> listNotif = notificationTaskRepository.findAll();

        listNotif.stream() // create stream
                .filter(not -> not.getDate_time().truncatedTo(ChronoUnit.MINUTES)
                        .equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) //filter by DateTime
                .forEach(notif -> {
                    Long chatId = notif.getChart_id(); // get id chart
                    SendMessage sendMess = new SendMessage(chatId, notif.getMessage()); // get message for sending
                    SendResponse response = telegramBot.execute(sendMess); // send message
                });

    }

}
