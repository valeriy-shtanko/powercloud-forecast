package com.powerclowd.forecast.components;

import com.powerclowd.forecast.exception.InternalErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClient {

    private final RestTemplate restTemplate;


    public <T> T getObject(final String url, final Class<T> responseType) {

        try {
            var result = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    responseType);

            return result.getBody();
        }
        catch (HttpClientErrorException ex) {
            log.error("HttpClientErrorException", ex);
            throw new RestClientException(getErrorText(ex), ex);
        }
        catch (RestClientException ex) {
            log.error("RestClientException", ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error("Unexpected error occurred", ex);
            throw new InternalErrorException(getErrorText(ex), ex);
        }
    }


    private static String getErrorText(Throwable throwable) {
        return isBlank(throwable.getMessage())
                ? "Error message not defined"
                : throwable.getMessage();
    }
}
