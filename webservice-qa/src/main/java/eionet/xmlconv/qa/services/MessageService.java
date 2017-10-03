package eionet.xmlconv.qa.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 *
 */
@Service
public class MessageService {

    private MessageSource messageSource;

    @Autowired
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key) {
        String value = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        return StringUtils.defaultIfEmpty(value, key);
    }

    public String getMessage(String key, Object... strings) {
        String value = messageSource.getMessage(key, strings, LocaleContextHolder.getLocale());
        return StringUtils.defaultIfEmpty(value, key);
    }
}
