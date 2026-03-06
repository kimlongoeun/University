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

    // ── Spring Security ────────────────────────────────────────────────────

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

    // ── Register (used by AuthController + AdminController) ────────────────

    public Student register(StudentDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setMajor(dto.getMajor());
        student.setPassword(passwordEncoder.encode(
                dto.getPassword() != null && !dto.getPassword().isBlank()
                        ? dto.getPassword()
                        : "Temp@" + System.currentTimeMillis()));
        student.setStudentId(generateStudentId());
        student.setStatus(Student.Status.ACTIVE);
        return studentRepository.save(student);
    }

    // ── Update profile ─────────────────────────────────────────────────────

    public Student updateProfile(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        if (!student.getEmail().equals(dto.getEmail()) &&
                studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("That email is already in use.");
        }
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setMajor(dto.getMajor());
        return studentRepository.save(student);
    }

    // ── Update status (admin only) ─────────────────────────────────────────

    public Student updateStatus(Long id, Student.Status status) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        student.setStatus(status);
        return studentRepository.save(student);
    }

    // ── Delete ─────────────────────────────────────────────────────────────

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    // ── Queries ────────────────────────────────────────────────────────────

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
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
}