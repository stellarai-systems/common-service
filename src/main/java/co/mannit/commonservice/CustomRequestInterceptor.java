/*
 * package co.mannit.commonservice;
 * 
 * import org.apache.logging.log4j.LogManager; import
 * org.apache.logging.log4j.Logger; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.servlet.HandlerInterceptor;
 * 
 * import jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse;
 * 
 * @Component public class CustomRequestInterceptor implements
 * HandlerInterceptor{
 * 
 * private static final Logger logger =
 * LogManager.getLogger(CustomRequestInterceptor.class);
 * 
 * private static final ThreadLocal<String> tl = new ThreadLocal<>();
 * 
 * @Override public boolean preHandle(HttpServletRequest request,
 * HttpServletResponse response, Object handler) throws Exception { if
 * ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
 * 
 * return true; } logger.debug("<preHandle>"); String url =
 * request.getRequestURI(); logger.debug("<preHandle>url{}",url); String uiqueId
 * = request.getHeader("xxxid"); // String uiqueId =
 * request.getParameter("xxxid");
 * 
 * 
 * if(uiqueId != null) { tl.set(uiqueId); }else { if
 * (url.contains("/swagger-ui") || url.contains("/v3/api-docs") ||
 * url.contains("/swagger-resources") || url.contains("/webjars/")) { return
 * true; } if(url.endsWith("web")||url.startsWith("W")) { return true; }
 * 
 * if(!url.endsWith("setup")) { throw new
 * ServiceCommonException("Required Parameter Missing"); } }
 * logger.debug("</preHandle>"); return true; }
 * 
 * @Override public void afterCompletion(HttpServletRequest request,
 * HttpServletResponse response, Object handler, Exception ex) throws Exception
 * { logger.debug("<afterCompletion>");
 * 
 * tl.remove(); logger.debug("</afterCompletion>"); }
 * 
 * static public String getUniqueId() { return tl.get(); } }
 */



package co.mannit.commonservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomRequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(CustomRequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        logger.debug("<preHandle>");
        String url = request.getRequestURI();
        logger.debug("<preHandle>url {}", url);

        String uniqueId = request.getHeader("xxxid");

        if (uniqueId != null) {
            RequestContextHolder.set(uniqueId);
        } else {
            if (url.contains("/swagger-ui") || url.contains("/v3/api-docs")
                || url.contains("/swagger-resources") || url.contains("/webjars/")) {
                return true;
            }
            if (url.endsWith("web") || url.contains("W")) {
                return true;
            }

            if (!url.endsWith("setup")) {
                throw new ServiceCommonException("Required Parameter Missing");
            }
        }

        logger.debug("</preHandle>");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        logger.debug("<afterCompletion>");
        RequestContextHolder.clear();
        logger.debug("</afterCompletion>");
    }

    public static String getUniqueId() {
        return RequestContextHolder.get();
    }
}
