package co.mannit.commonservice.filter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import co.mannit.commonservice.RequestContextHolder;
import co.mannit.commonservice.common.util.JwtUtil;
import co.mannit.commonservice.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
	/*
	 * private static final Set<String> ALLOWED_IPS = Set.of( "0:0:0:0:0:0:0:1",
	 * "::1", "127.0.0.1", "182.72.131.90" );
	 */

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
    	RequestContextHolder.set(request.getHeader("xxxid"));
    	String clientIp = extractClientIp(request);
    //    System.out.println("Client IP seen by AuthTokenFilter: " + clientIp);

        // Allow whitelisted IPs to bypass auth (development/devops only)
		/*
		 * if (ALLOWED_IPS.contains(clientIp)) { UsernamePasswordAuthenticationToken
		 * anonymousAuth = new UsernamePasswordAuthenticationToken( "trustedClient", //
		 * Principal null, List.of(new SimpleGrantedAuthority("ROLE_TRUSTED_CLIENT")) //
		 * Optional role );
		 * 
		 * anonymousAuth.setDetails(new
		 * WebAuthenticationDetailsSource().buildDetails(request));
		 * SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
		 * 
		 * System.out.println("Anonymous access granted for IP: " + clientIp);
		 * filterChain.doFilter(request, response); return; }
		 */
    	
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("Cannot set user authentication: " + e);
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

