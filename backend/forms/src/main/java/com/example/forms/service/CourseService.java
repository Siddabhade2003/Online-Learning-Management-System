package com.example.forms.service;

import com.example.forms.entity.Course;
import com.example.forms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Directory where images will be saved
    private final String uploadDirectory = "/Users/siddharthdabhade/admin/images/";

    public Course createCourse(String title, String description, MultipartFile image) throws IOException {
        Course course = new Course(title, description, saveImage(image));
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, String title, String description, MultipartFile image) throws IOException {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            // Update title and description if provided
            if (title != null && !title.isEmpty()) {
                course.setTitle(title);
            }
            if (description != null && !description.isEmpty()) {
                course.setDescription(description);
            }
            // Update image URL if a new image is provided
            if (image != null && !image.isEmpty()) {
                course.setImageUrl(saveImage(image));
            }
            return courseRepository.save(course);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
    }


    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Method to save image file and return its URL
    public String saveImage(MultipartFile image) throws IOException {
        String fileName = image.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory + fileName);
        Files.write(filePath, image.getBytes());
        return fileName; // Return the file name as the URL path to access the image
    }
    public byte[] getImageData(Long id) throws IOException {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            String fileName = course.getImageUrl();
            Path imagePath = Paths.get(uploadDirectory + fileName);
            return Files.readAllBytes(imagePath);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
    }
}
