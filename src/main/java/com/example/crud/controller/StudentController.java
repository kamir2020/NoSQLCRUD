package com.example.crud.controller;

import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository repo;

    public StudentController(StudentRepository repo) {
        this.repo = repo;
    }

    // LIST ALL
    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = repo.findAll();
        model.addAttribute("students", students);
        return "students"; // templates/students.html
    }

    // SHOW FORM - ADD NEW
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Student student = new Student();
        student.setEnrolled(true);
        student.setScholarship(false);
        student.setJoinAt(LocalDateTime.now());

        model.addAttribute("student", student);
        model.addAttribute("courseInput", "");
        model.addAttribute("isNew", true);
        return "student-form"; // templates/student-form.html
    }

    // SHOW FORM - EDIT
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Optional<Student> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/students";
        }

        Student student = opt.get();
        model.addAttribute("student", student);

        String courseInput = "";
        if (student.getCourse() != null) {
            courseInput = String.join(", ", student.getCourse());
        }
        model.addAttribute("courseInput", courseInput);
        model.addAttribute("isNew", false);

        return "student-form";
    }

    // SAVE (ADD or UPDATE)
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute("student") Student student,
                              @RequestParam(name = "courseInput", required = false) String courseInput) {

        // 🔑 If id is empty string, treat it as null so MongoDB can auto-generate _id
        if (student.getId() != null && student.getId().isBlank()) {
            student.setId(null);
        }

        // Convert comma-separated courses to List<String>
        if (courseInput != null && !courseInput.isBlank()) {
            List<String> courses = Arrays.stream(courseInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            student.setCourse(courses);
        }

        // If joinAt is null, set now
        if (student.getJoinAt() == null) {
            student.setJoinAt(LocalDateTime.now());
        }

        repo.save(student);
        return "redirect:/students";
    }

    // DELETE with id (normal case)
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable String id) {
        if (id != null && !id.isBlank()) {
            repo.deleteById(id);
        }
        return "redirect:/students";
    }

    // OPTIONAL: if user goes to /students/delete without id → just redirect
    @GetMapping("/delete")
    public String deleteWithoutId() {
        return "redirect:/students";
    }

    // OPTIONAL: Insert sample Amina
    @GetMapping("/sample")
    @ResponseBody
    public String insertSampleAmina() {
        Student s = new Student();
        s.setId("692ea180c4d357509bd16a83");
        s.setName("Amina");
        s.setAge(22);
        s.setCourse(List.of("Databases", "AI"));
        s.setGpa(3.6);
        s.setEnrolled(true);
        s.setJoinAt(LocalDateTime.of(2025, 10, 25, 0, 0));
        s.setScholarship(true);
        s.setEmail("amina@gmail.com");
        repo.save(s);
        return "Sample Amina inserted!";
    }
}
