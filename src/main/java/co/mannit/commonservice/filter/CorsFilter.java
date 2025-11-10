/*
 * package co.mannit.commonservice.filter;
 * 
 * import java.io.IOException;
 * 
 * import org.apache.logging.log4j.LogManager; import
 * org.apache.logging.log4j.Logger; import
 * org.springframework.stereotype.Component;
 * 
 * import jakarta.servlet.Filter; import jakarta.servlet.FilterChain; import
 * jakarta.servlet.FilterConfig; import jakarta.servlet.ServletException; import
 * jakarta.servlet.ServletRequest; import jakarta.servlet.ServletResponse;
 * import jakarta.servlet.annotation.WebFilter; import
 * jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * @WebFilter("/*")
 * 
 * @Component public class CorsFilter implements Filter {
 * 
 * private static final Logger logger = LogManager.getLogger(CorsFilter.class);
 * 
 * @Override public void doFilter(ServletRequest request, ServletResponse
 * response, FilterChain chain) throws IOException, ServletException { // TODO
 * Auto-generated method stub logger.debug("CORS Origin filter invoked");
 * 
 * 
 * 
 * HttpServletResponse httpResponse = (HttpServletResponse) response;
 * httpResponse.setHeader("Access-Control-Allow-Origin", "*");
 * httpResponse.setHeader("Access-Control-Allow-Methods",
 * "GET, POST, PUT, DELETE,OPTIONS");
 * httpResponse.setHeader("Access-Control-Allow-Headers",
 * "Authorization, Content-Type,xxxid,X-Razorpay-Signature");
 * httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
 * httpResponse.setHeader("Access-Control-Max-Age", "3600");
 * 
 * if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
 * logger.debug("Preflight (OPTIONS) request handled");
 * httpResponse.setStatus(HttpServletResponse.SC_OK); return; }
 * 
 * chain.doFilter(request, response); }
 * 
 * 
 * }
 */