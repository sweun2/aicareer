package co.unlearning.aicareer.domain;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class MainController {
    @GetMapping("")
    public void exRedirect3(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("https://aicareer.co.kr");
    }
}
