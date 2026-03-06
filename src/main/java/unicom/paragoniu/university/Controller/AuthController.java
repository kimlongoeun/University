package unicom.paragoniu.university.Controller;

import unicom.paragoniu.university.dto.StudentDTO;
import unicom.paragoniu.university.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── Home page ──────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home() {
        return "home";   // → templates/home.html
    }

    // ── Login ──────────────────────────────────────────────────────────────
    // Spring Security handles POST /login automatically.
    // We only need to serve the GET for the form.

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // → templates/login.html
    }

    // ── Register ───────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("studentDTO", new StudentDTO());
        return "register"; // → templates/register.html
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Bean-validation errors (blank fields, bad email, short password)
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2. Business logic errors (duplicate email, etc.)
        try {
            studentService.register(studentDTO);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }

        // 3. Success → redirect to login with a flash message
        redirectAttributes.addFlashAttribute("successMessage",
                "Account created! Please sign in.");
        return "redirect:/login";
    }
}