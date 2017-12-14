package ru.bvn13.voidforum.support.localization;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.AppLocale;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.utils.CommonHelper;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Created by bvn13 on 12.12.2017.
 */
@JadeHelper("locale")
@Component
public class LocalizationHelper {

    private static Logger logger = LoggerFactory.getLogger(LocalizationHelper.class);

    private AppLocale defaultLocale = AppLocale.EN;

    private static Map<AppLocale, Map<String, String>> messages = new HashMap<>();

    private static Map<AppLocale, String> resources = new HashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private SessionStorer sessionStorer;


    static {
        resources.put(AppLocale.EN, "i18n/messages.properties");
        resources.put(AppLocale.RU, "i18n/messages_ru.properties");
    }

    @PostConstruct
    private void init() {
        for (AppLocale locale : resources.keySet()) {
            loadLocale(locale, resources.get(locale));
        }
    }

    private static void loadLocale(AppLocale locale, String resourceName) {
        Map<String, String> resourceMessages = new HashMap<>();

        Resource cpr = new ClassPathResource(resourceName);

        try {

            File file = cpr.getFile();
            BufferedReader b = new BufferedReader(new FileReader(file));

            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                parseResourceLine(readLine, resourceMessages);
            }
            messages.put(locale, resourceMessages);

        } catch (IOException e) {
            logger.error("Could not load resource: "+resourceName);
            e.printStackTrace();
            return;
        }
    }

    private static void parseResourceLine(String line, Map<String, String> resourceMessages) throws IllegalArgumentException {
        if (line.trim().isEmpty() || line.trim().substring(0,1).equals("#")) {
            return;
        }
        String[] args = line.split("=");
        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong locale resource: "+line);
        }
        resourceMessages.put(args[0].trim(), args[1].trim());
    }




    public String msg(String msgId) {
        AppLocale locale = getCurrentLocale();
        String message = "";
        if (messages.containsKey(locale) && messages.get(locale).containsKey(msgId)) {
            message = messages.get(locale).get(msgId);
        } else {
            message = messages.get(defaultLocale).get(msgId);
        }
        return message;
    }


    public List<AppLocale> getSupportedLocales() {
        return CommonHelper.asSortedList(messages.keySet());
    }

    public AppLocale getCurrentLocale() {
        User user = userService.currentUser();
        AppLocale locale = null;
        if (user != null) {
            locale = user.getLocale();
        }
        if (locale == null) {
            locale = sessionStorer.getLocale();
        }
        if (locale == null) {
            locale = defaultLocale;
        }
        return locale;
    }
}
