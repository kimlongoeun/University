package unicom.paragoniu.university.controller;

import unicom.paragoniu.university.dto.StudentDTO;
import unicom.paragoniu.university.model.Student;
import unicom.paragoniu.university.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/profile/edit")
    public String editProfileSubmit(@ModelAttribute StudentDTO dto,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        Student current = studentService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        try {
            studentService.updateProfile(current.getId(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }
}