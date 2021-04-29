package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK

    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }


    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListGroup(@PathVariable Integer groupId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(groupId, pageable);
        return studentPage;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            return student;
        }
        return new Student();
    }


    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto) {
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Address address = new Address(studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());

        student.setAddress(addressRepository.save(address));
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());

        if (!optionalGroup.isPresent()) {
            return "Groups not found!";
        }
        student.setGroup(optionalGroup.get());
        List<Integer> subjects_ids = studentDto.getSubjects_ids();
        List<Subject> subjectList = new ArrayList<>();

        for (Integer subjects_id : subjects_ids) {
            Optional<Subject> byId = subjectRepository.findById(subjects_id);
            if (!byId.isPresent()) {
                return "Subject not found!";
            }
            subjectList.add(byId.get());
        }

        student.setSubjects(subjectList);
        studentRepository.save(student);
        return "Student added!";
    }

    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());

            Address address = student.getAddress();
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());

            if (!optionalGroup.isPresent()) {
                return "Groups not found!";
            }
            student.setGroup(optionalGroup.get());

            List<Integer> subjects_ids = studentDto.getSubjects_ids();
            List<Subject> subjectList = new ArrayList<>();

            for (Integer subjects_id : subjects_ids) {
                Optional<Subject> byId = subjectRepository.findById(subjects_id);
                if (!byId.isPresent()) {
                    return "Subject not found!";
                }
                subjectList.add(byId.get());
            }

            student.setSubjects(subjectList);
            addressRepository.save(address);

            studentRepository.save(student);
            return "Student editing!";
        }
        return "Student not found!";
    }

    @DeleteMapping("/{id}")
    public String deleteSudent(@PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepository.deleteById(id);
            return "Student deleted!";
        }
        return "Student not found!";
    }
}
