package com.intouristing.intouristing.i18n;

import com.intouristing.intouristing.Application;
import com.intouristing.intouristing.controller.handler.ApiValidatorExceptionHandler;
import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.exceptions.TokenNotFoundException;
import com.intouristing.intouristing.exceptions.TokenParseException;
import com.intouristing.intouristing.model.dto.ErrorDTO;
import com.intouristing.intouristing.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Marcelo Lacroix on 11/08/2019.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class InternationalizationTest {

    private final String ptBr = "pt-BR",
            enUs = "en-US";

    @Autowired
    ApiValidatorExceptionHandler exceptionHandler;
    @Autowired
    MessageSource messageSource;
    @Mock
    WebRequest webRequest;

    private Boolean messageMatches(String expected, String actual) {
        expected = expected.replaceAll("\\[\\{.*?\\}\\]", "{param}");
        actual = actual.replaceAll("\\[.*?\\]", "{param}");
        return expected.equals(actual);
    }

    private Map<String, Locale> getLocales() {
        Locale localePtBr = new Locale("pt", "BR"),
                localeEnUs = new Locale("en", "US");

        Map<String, Locale> map = new HashMap<>();
        map.put(this.ptBr, localePtBr);
        map.put(this.enUs, localeEnUs);
        return map;
    }

    /**
     * Construct object with a parametrized constructor if {@param constructorClasses} and {@param constructParams} are not null
     * otherwise construct with no args constructor
     */
    private <T> T constructException(Class<T> exceptionClass, Class<?>[] constructorClasses, Serializable[] constructorParams) throws Exception {
        if (nonNull(constructorClasses) && nonNull(constructorParams)) {
            return exceptionClass.getDeclaredConstructor(constructorClasses).newInstance((Object[]) constructorParams);
        }

        return exceptionClass.getDeclaredConstructor().newInstance();
    }

    private <T> void createExceptionTest(Class<T> exceptionClass, String messageCode, Class<?>[] constructorClasses, Serializable[] constructorParams) throws Exception {
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(messageCode);
        Map<String, Locale> locales = getLocales();
        String ptBrExpectedMessage = messageSource.getMessage(messageSourceResolvable, locales.get(this.ptBr)),
                enUsExpectedMessage = messageSource.getMessage(messageSourceResolvable, locales.get(this.enUs));

        Exception exception = (Exception) this.constructException(exceptionClass, constructorClasses, constructorParams);

        LocaleContextHolder.setLocale(locales.get(this.ptBr));
        ErrorDTO ptBrErrorDTO = (ErrorDTO) exceptionHandler.handleAllExceptions(exception, webRequest).getBody();
        LocaleContextHolder.setLocale(locales.get(this.enUs));
        ErrorDTO enUsErrorDTO = (ErrorDTO) exceptionHandler.handleAllExceptions(exception, webRequest).getBody();

        assertNotNull(ptBrErrorDTO);
        assertTrue(messageMatches(ptBrExpectedMessage, ptBrErrorDTO.getMessage()));
        assertNotNull(enUsErrorDTO);
        assertTrue(messageMatches(enUsExpectedMessage, enUsErrorDTO.getMessage()));
    }

    @Test
    public void shouldReturnNotFoundExceptionWithInternatinalizedMessage() throws Exception {
        Class<?>[] constructorClasses = ArrayUtils.toArray(Class.class, Long.class);
        Serializable[] serializables = ArrayUtils.toArray(User.class, 9992222L);
        createExceptionTest(NotFoundException.class, "entity.of.type.not.found", constructorClasses, serializables);
    }

    @Test
    public void shouldReturnTokenNotFoundExceptionWithInternatinalizedMessage() throws Exception {
        createExceptionTest(TokenNotFoundException.class, "token.is.mandatory", null, null);
    }

    @Test
    public void shouldReturnTokenParseExceptionWithInternatinalizedMessage() throws Exception {
        createExceptionTest(TokenParseException.class, "error.on.parse.token", null, null);
    }
}
