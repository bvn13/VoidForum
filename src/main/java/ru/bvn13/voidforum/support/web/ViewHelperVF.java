package ru.bvn13.voidforum.support.web;


import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.support.WebError;
import ru.bvn13.voidforum.services.AppSetting;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author bvn13 <mail4bvn@gmail.com>
 */
@Service
@JadeHelper("viewHelper")
public class ViewHelperVF {

    private static Logger logger = LoggerFactory.getLogger(ViewHelperVF.class);

    private static DateFormatSymbols ruDateFormatSymbolsFull = new DateFormatSymbols(){
        @Override
        public String[] getMonths() {
            return new String[]{"январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};
        }
    };
    private static DateFormatSymbols ruDateFormatSymbolsShort = new DateFormatSymbols(){
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy", ViewHelperVF.ruDateFormatSymbolsFull);
    private static final SimpleDateFormat DATE_FORMAT_MONTH_DAY = new SimpleDateFormat("MMM dd", ViewHelperVF.ruDateFormatSymbolsShort);

    private AppSetting appSetting;

    private String applicationEnv;

    @Autowired
    public ViewHelperVF(AppSetting appSetting){
        this.appSetting = appSetting;
    }

    private long startTime;

    public long getResponseTime(){
        return System.currentTimeMillis() - startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getFormattedDate(Date date){
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    public String getMonthAndDay(Date date){
        return date == null ? "" : DATE_FORMAT_MONTH_DAY.format(date);
    }

    public String metaTitle(String title){
        return title + " · " + appSetting.getSiteName();
    }

    public String getApplicationEnv() {
        return applicationEnv;
    }

    public Boolean isProductionMode() {
        return this.applicationEnv.equalsIgnoreCase("production");
    }

    public void setApplicationEnv(String applicationEnv) {
        this.applicationEnv = applicationEnv;
    }

    public String formatNumberByThousands(Long number) {
        if (number == null)
            return "0";

        double thousands = number / 1000;
        double millions = thousands / 1000;
        if (millions > 0d) {
            return String.format("%.3f", millions);
        } else if (thousands > 0d) {
            return String.format("%.3fK", thousands);
        } else {
            return String.format("%d", number);
        }
    }

    public String formatNumberByThousands(Integer number) {
        return this.formatNumberByThousands((long) number);
    }

    public String errorFormat(Map<String, WebError> errors, String field) {
        if (errors != null && errors.containsKey(field)) {
            return errors.get(field).getErrorMessage();
        } else {
            return "";
        }
    }

    public String getPostUrl(Post post) {
        return String.format("%s/posts/%s",
                this.appSetting.getMainUri().endsWith("/") ? this.appSetting.getMainUri().substring(0, this.appSetting.getMainUri().length()-1) : this.appSetting.getMainUri(),
                post.getPermalink().isEmpty() ? post.getId() : post.getPermalink()
        );
    }

    public String getAbsoluteUrl(String url) {
        if (url.isEmpty()) {
            return "";
        }
        return String.format("%s/%s",
                this.appSetting.getMainUri().endsWith("/") ? this.appSetting.getMainUri().substring(0, this.appSetting.getMainUri().length()-1) : this.appSetting.getMainUri(),
                url.startsWith("/") ? url.substring(1) : url
        );
    }
}
