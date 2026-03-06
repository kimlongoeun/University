package unicom.paragoniu.university.Controller;

import unicom.paragoniu.university.model.Student;
import unicom.paragoniu.university.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── All students list ──────────────────────────────────────────────────

    @GetMapping("/students")
    public String allStudents(Authentication authentication, Model model) {
        Student currentStudent = resolveCurrentStudent(authentication);
        model.addAttribute("currentStudent", currentStudent);
        model.addAttribute("students", studentService.findAll());
        return "students"; // → templates/students.html  (create when ready)
    }

    // ── My profile ─────────────────────────────────────────────────────────

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Student currentStudent = resolveCurrentStudent(authentication);
        model.addAttribute("currentStudent", currentStudent);
        return "profile"; // → templates/profile.html  (create when ready)
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private Student resolveCurrentStudent(Authentication authentication) {
        return studentService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));
    }
}