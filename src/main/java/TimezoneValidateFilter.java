import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(value = "/time", servletNames = "TimeServlet")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req,
                            HttpServletResponse resp,
                            FilterChain chain) throws IOException, ServletException {


    String timeZoneParam = req.getParameter("timezone");

    if(timeZoneParam != null){
        if (!isTimezoneTrue(timeZoneParam)) {
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().write("Invalid or missing timezone parameter");
            resp.getWriter().close();
        }else {
            chain.doFilter(req, resp);
        }
    } else {
            chain.doFilter(req, resp);
    }
}


    private static boolean isTimezoneTrue(String timezone) {
        timezone = timezone.replace("UTC", "").replace(" ","+");
        if (timezone.matches("[+-]\\d{1,2}")) {
            int a = Integer.parseInt(timezone);
            return a >= -18 && a <= 18;
        }
        return false;
    }
}




