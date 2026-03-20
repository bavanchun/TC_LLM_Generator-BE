package com.group05.TC_LLM_Generator.infrastructure.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet filter that ensures every request carries a correlation ID.
 *
 * <p>Reads {@code X-Request-ID} from the incoming request header. If absent,
 * generates a random UUID. The value is:
 * <ul>
 *   <li>Stored in {@link MDC} as {@code requestId} so every log line in the
 *       request scope includes it — enabling log correlation in Loki.</li>
 *   <li>Written back to the response as {@code X-Request-ID} so clients and
 *       proxies can trace calls end-to-end.</li>
 * </ul>
 *
 * <p>Registered with the highest precedence so it runs before security filters,
 * meaning even authentication failures are tagged with a request ID.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID    = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpReq = (HttpServletRequest)  request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String requestId = httpReq.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_REQUEST_ID, requestId);
        httpRes.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            // Always clear MDC — threads are reused in the servlet container pool
            MDC.remove(MDC_REQUEST_ID);
        }
    }
}
