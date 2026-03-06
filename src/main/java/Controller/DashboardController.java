@Controller
public class DashboardController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();
        Student student = studentService.findByEmail(email);
        model.addAttribute("student", student);
        return "dashboard";
    }
}