package ru.bvn13.voidforum.support.localization;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import ru.bvn13.voidforum.models.support.AppLocale;

/**
 * Created by bvn13 on 14.12.2017.
 */
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class SessionStorer {

    @Getter
    @Setter
    private AppLocale locale;

}
