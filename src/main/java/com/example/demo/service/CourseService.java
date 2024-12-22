package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public void addCourse(Course course) {
        courseRepository.save(course);
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id).get();
    }

    public Page<Course> listCourse(int pageNo, int pageSize, String sortBy,
                                   String sortDirection){
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return courseRepository.findAll(pageable);
    }

    

}
