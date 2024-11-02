package com.example.manulife.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.manulife.entities.User;
import com.example.manulife.repositories.UserRepository;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public ByteArrayInputStream generateUserReport() throws JRException {
        List<User> users = userRepository.findAll();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);

        // Load the Jasper template
        InputStream reportStream = getClass().getResourceAsStream("/templates/report_users.jrxml"); 
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Parameters for the report (if any)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Manulife");

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report to PDF in a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
