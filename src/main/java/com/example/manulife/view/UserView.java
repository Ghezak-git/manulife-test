package com.example.manulife.view;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.manulife.entities.User;
import com.example.manulife.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class UserView extends VerticalLayout {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final Grid<User> grid = new Grid<>(User.class);

    private final TextField fullNameField = new TextField("Full Name");
    private final TextField emailField = new TextField("Email");
    private final PasswordField passwordField = new PasswordField("Password");  // Changed to PasswordField
    private final Select<String> statusSelect = new Select<>();

    private User selectedUser;

    @Autowired
    public UserView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        grid.setColumns("id", "fullName", "email", "status", "createdAt", "updatedAt");
        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));
        refreshGrid();

        // Status Select with options
        statusSelect.setLabel("Status");
        statusSelect.setItems("Active", "No Active");
        statusSelect.setPlaceholder("Select status");

        // Form Layout
        fullNameField.setPlaceholder("Enter full name");
        emailField.setPlaceholder("Enter email");
        passwordField.setPlaceholder("Enter password");

        Button addButton = new Button("Add User", event -> addUser());
        Button updateButton = new Button("Update User", event -> updateUser());
        Button deleteButton = new Button("Delete User", event -> deleteUser());
        Button reportButton = new Button("Generate Report", event -> generateReport());

        // Layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, updateButton, deleteButton, reportButton);
        add(fullNameField, emailField, passwordField, statusSelect, buttonLayout, grid);
    }

    private void refreshGrid() {
        grid.setItems(userService.findAll());
    }

    private void populateForm(User user) {
        if (user != null) {
            selectedUser = user;
            fullNameField.setValue(user.getFullName());
            emailField.setValue(user.getEmail());
            passwordField.setValue(""); // Password can't be displayed
            statusSelect.setValue(user.getStatus());
        } else {
            selectedUser = null;
            fullNameField.clear();
            emailField.clear();
            passwordField.clear();
            statusSelect.clear();
        }
    }

    private void addUser() {
        if (fullNameField.isEmpty() || emailField.isEmpty() || passwordField.isEmpty() || statusSelect.isEmpty()) {
            Notification.show("Please fill all fields");
            return;
        }

        User user = new User();
        user.setFullName(fullNameField.getValue());
        user.setEmail(emailField.getValue());
        user.setPassword(passwordEncoder.encode(passwordField.getValue()));  // Encodes the password
        user.setStatus(statusSelect.getValue());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userService.save(user);
        Notification.show("User added successfully");
        refreshGrid();
        clearForm();
    }

    private void updateUser() {
        if (selectedUser == null) {
            Notification.show("No user selected for update");
            return;
        }

        selectedUser.setFullName(fullNameField.getValue());
        selectedUser.setEmail(emailField.getValue());
        if (!passwordField.isEmpty()) {
            selectedUser.setPassword(passwordEncoder.encode(passwordField.getValue()));
        }
        selectedUser.setStatus(statusSelect.getValue());
        selectedUser.setUpdatedAt(LocalDateTime.now());

        userService.save(selectedUser);
        Notification.show("User updated successfully");
        refreshGrid();
        clearForm();
    }

    private void deleteUser() {
        if (selectedUser == null) {
            Notification.show("No user selected for deletion");
            return;
        }

        userService.delete(selectedUser);
        Notification.show("User deleted successfully");
        refreshGrid();
        clearForm();
    }

    private void generateReport() {
        try {
            ByteArrayInputStream reportStream = userService.generateUserReport();
    
            // Create a StreamResource for the PDF file
            StreamResource resource = new StreamResource("user_report.pdf", () -> reportStream);
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);
            
            StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
            String resourceUrl = registration.getResourceUri().toString();
            getUI().ifPresent(ui -> 
                ui.getPage().executeJs(
                    "const link = document.createElement('a');" +
                    "link.href = $0;" +
                    "link.download = 'user_report.pdf';" +  
                    "document.body.appendChild(link);" +
                    "link.click();" +
                    "document.body.removeChild(link);", 
                    resourceUrl
                )
            );
            
            Notification.show("Report generated successfully");
        } catch (Exception e) {
            Notification.show("Error generating report: " + e.getMessage(), 50000, Notification.Position.MIDDLE);
        }
    }

    private void clearForm() {
        fullNameField.clear();
        emailField.clear();
        passwordField.clear();
        statusSelect.clear();
        selectedUser = null;
    }
}
