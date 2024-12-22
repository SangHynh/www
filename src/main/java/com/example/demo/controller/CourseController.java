package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String course(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Course> coursePage = courseService.listCourse( currentPage -1 , pageSize, "id", "asc");

        model.addAttribute("coursePage", coursePage);

        int totalPages = coursePage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "/course/index";
    }

    // Hiển thị form thêm khóa học
    @GetMapping("/add")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "course/add"; // Trang form thêm khóa học
    }

    // Xử lý thêm khóa học
    @PostMapping("/add")
    public String addCourse(@RequestParam("name") String name, @RequestParam("description") String description) {
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        System.out.println(course);
        courseService.addCourse(course);
        return "redirect:/courses";
    }

}
