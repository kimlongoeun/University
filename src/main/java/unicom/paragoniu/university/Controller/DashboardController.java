package unicom.paragoniu.university.Controller;

import unicom.paragoniu.university.model.Student;
import unicom.paragoniu.university.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final StudentService studentService;

    public DashboardController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /dashboard
     *
     * Supplies the Thymeleaf template with:
     *   - currentStudent  → logged-in student object (used in sidebar & profile panel)
     *   - students        → list of all students    (used in "Recent Students" table)
     *   - totalStudents   → count                   (used in stat card)
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        // Resolve the currently logged-in student from their email (Spring Security username)
        String email = authentication.getName();
        Student currentStudent = studentService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));

        // Fetch all students for the recent-students table (limit to 10 for display)
        List<Student> allStudents = studentService.findAll();
        List<Student> recentStudents = allStudents.stream()
                .limit(10)
                .toList();

        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("students", recentStudents);
        model.addAttribute("totalStudents", studentService.countAll());

        return "dashboard"; // → templates/dashboard.html
    }
}