package com.sda.sapiens.webapp.servlet;

import com.sda.sapiens.webapp.dao.StudentDao;
import com.sda.sapiens.webapp.model.Student;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@WebServlet("/student/form")
public class StudentFormServlet extends HttpServlet {

    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/student-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ponieważ podczas dodawania nowego rekordu to pole będzie równe null, to musimy rozważyć 2 przypadki
        String editedId = req.getParameter("edited_student_id");
        Long editedStudentId = null;
        if(editedId != null && !editedId.isEmpty()){ // jeśli jest podane i nie jest puste
            log.info("Edytujemy studenta o identyfikatorze: " + editedId);
            editedStudentId = Long.parseLong(editedId);
        }

        String studentFirstName = req.getParameter("student-first-name");
        String studentLastName = req.getParameter("student-last-name");
        String studentIndex = req.getParameter("student-indeks");
        String studentBirthDate = req.getParameter("student-birth-date");

        Date birthDate;
        try {
            birthDate = Date.valueOf(studentBirthDate);
        } catch (IllegalArgumentException iae) {
            resp.sendRedirect(req.getContextPath() + "/student/form?error=" + iae.getMessage());
            return;
        }

        // korzystam z adnotacji @Builder w klasie Student (patrz pod @Data)
        Student student = Student.builder()
                .id(editedStudentId)
                .firstName(studentFirstName)
                .lastName(studentLastName)
                .indeks(studentIndex)
                .birthDate(birthDate)
                .build();

        studentDao.saveOrUpdate(student);

        resp.sendRedirect(req.getContextPath() + "/student");
    }
}
