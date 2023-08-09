import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.thymeleaf.context.Context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ServletContext servletContext = getServletContext();
        String templatesPath = servletContext.getRealPath("/") + "WEB-INF/classes/templates/";
        FileTemplateResolver resolver = new FileTemplateResolver();

        resolver.setPrefix(templatesPath);
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);

        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");

        List gotTime = getTime(req, resp);
        if (gotTime != null) {
            Context simpleContext = new Context(req.getLocale(), Map.of("task", gotTime.get(0), "time", gotTime.get(1)));
            engine.process("test", simpleContext, resp.getWriter());
            resp.getWriter().close();
        }else {
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().write("Invalid or missing timezone parameter");
            resp.getWriter().close();
        }
    }


    protected List getTime(HttpServletRequest req, HttpServletResponse resp)  {
        List data = new ArrayList();

        String timeZone = "UTC";
        String task = "Current time (UTC): ";

        if (req.getParameterMap().containsKey("timezone")) {
            timeZone = req.getParameter("timezone").replace(" ", "+");
            task = "Current time in time zone " + timeZone + ": ";
            resp.addCookie(new Cookie("lastTimezone", timeZone));
        }else if (getLastTimezone(req) != null){ // lastTimezone
            timeZone = getLastTimezone(req);
            task = "Current time in time zone " + timeZone + ": ";
        }

        String time = ZonedDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss"))
                + " " + timeZone;

        data.add(task);
        data.add(time);

        return data;
    }

    private String getLastTimezone(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}


