package co.mannit.commonservice;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UniqueIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uniqueId = request.getHeader("xxxid");
        if (uniqueId != null) {
            RequestContextHolder.set(uniqueId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
        	RequestContextHolder.clear(); // Safe even if already cleared in interceptor
        }
    }
}

