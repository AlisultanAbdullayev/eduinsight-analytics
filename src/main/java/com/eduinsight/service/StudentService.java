package com.eduinsight.service;

import com.eduinsight.model.Student;
import com.eduinsight.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepo;

    public List<Student> findByCampus(String campus) {
        return studentRepo.findByCampus(campus);
    }

    public List<Student> findByCampuses(List<String> campuses) {
        return campuses.isEmpty() ? List.of() : studentRepo.findByCampusIn(campuses);
    }
}
