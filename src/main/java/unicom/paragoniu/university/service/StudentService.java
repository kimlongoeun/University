package unicom.paragoniu.university.service;

import unicom.paragoniu.university.dto.StudentDTO;
import unicom.paragoniu.university.model.Student;
import unicom.paragoniu.university.repository.StudentRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Spring Security: load by email (used as username) ──────────────────

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No student found with email: " + email));

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .roles("STUDENT")
                .build();
    }

    // ── Registration ───────────────────────────────────────────────────────

    public Student register(StudentDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }

        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setMajor(dto.getMajor());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setStudentId(generateStudentId());
        student.setStatus(Student.Status.ACTIVE);

        return studentRepository.save(student);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String generateStudentId() {
        int year = java.time.Year.now().getValue();
        long count = studentRepository.count() + 1;
        String candidate;
        do {
            candidate = String.format("STU-%d-%03d", year, count++);
        } while (studentRepository.existsByStudentId(candidate));
        return candidate;
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public long countAll() {
        return studentRepository.count();
    }
}