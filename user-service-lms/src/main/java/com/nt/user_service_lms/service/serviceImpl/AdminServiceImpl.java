package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.constants.UserConstants;
import com.nt.user_service_lms.converter.UserDTOConverter;
import com.nt.user_service_lms.dto.inDTO.BulkUserUploadInDTO;
import com.nt.user_service_lms.dto.inDTO.RegisterDto;
import com.nt.user_service_lms.dto.inDTO.UserInDTO;
import com.nt.user_service_lms.dto.outDTO.*;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.exception.InvalidRequestException;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceConflictException;
import com.nt.user_service_lms.exception.ResourceNotValidException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.RoleRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.AdminService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.nt.user_service_lms.constants.UserConstants.*;

/**
 * Service implementation for admin operations such as user registration, deletion, role management, and user retrieval.
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    /**
     * Repository for user operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for role operations.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Repository for enrollment operations.
     */
    @Autowired
    private EnrollmentRepository enrollmentRepository;


    /**
     * Feign client interface.
     */
    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    /**
     * Encoder for password encryption.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Converter for user DTOs.
     */
    @Autowired
    private UserDTOConverter userDTOConverter;




    /**
     * Registers a new user.
     *
     * @param registerDto the registration data
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> register(final RegisterDto registerDto) {
        log.info("Attempting to register user with email: {}", registerDto.getEmail());
        if (userRepository.findByEmailIgnoreCase(registerDto.getEmail()).isPresent()) {
            log.warn("Registration failed - user with email {} already exists", registerDto.getEmail());
            throw new ResourceConflictException(UserConstants.USER_ALREADY_EXISTS);
        }
        if (userRepository.findByUserNameIgnoreCase(registerDto.getUserName()).isPresent()) {
            log.warn("Registration failed - username {} already exists", registerDto.getUserName());
            throw new ResourceConflictException(UserConstants.USERNAME_ALREADY_EXISTS);
        }
        if (!roleRepository.findById(registerDto.getRoleId()).isPresent() || registerDto.getRoleId() == 1) {
            log.warn("Registration failed - role with ID {} does not exist", registerDto.getRoleId());
            throw new ResourceNotFoundException(UserConstants.INVALID_ROLE + " : " + registerDto.getRoleId());
        }
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setUserName(registerDto.getUserName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoleId(registerDto.getRoleId());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setManagerId(UserConstants.getAdminId());
        userRepository.save(user);
        log.info("User registered successfully with email: {}", registerDto.getEmail());
        MessageOutDTO messageOutDto = new MessageOutDTO(UserConstants.USER_REGISTRATION_SUCCESS);
        return StandardResponseOutDTO.success(messageOutDto, "User Registration Successfully");
    }

    /**
     * Deletes an employee or manager (soft delete only).
     *
     * @param id the user ID
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> employeeDeletion(final long id) {
        log.info("Attempting to delete user with ID: {}", id);
        if (id != UserConstants.getAdminId()) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User with ID {} not found", id);
                        return new ResourceNotFoundException(USER_NOT_FOUND);
                    });
            Role role = roleRepository.findById(user.getRoleId())
                    .orElseThrow(() -> {
                        log.error("Role with ID {} not found for user ID {}", user.getRoleId(), id);
                        return new IllegalStateException(UserConstants.INVALID_USER_ROLE);
                    });
            String roleName = role.getName();
            if ("employee".equalsIgnoreCase(roleName)) {
                user.setActive(false);
                userRepository.save(user);
                log.info("Employee with ID {} deleted successfully", id);
            } else if ("manager".equalsIgnoreCase(roleName)) {
                log.info("Changing manager for the deleted manager with ID: {}", id);
                List<User> subordinates = userRepository.findByManagerId(user.getUserId());
                if (!subordinates.isEmpty()) {
                    for (User u : subordinates) {
                        u.setManagerId(UserConstants.getAdminId());
                    }
                    userRepository.saveAll(subordinates);
                }
                user.setActive(false);
                userRepository.save(user);
                log.info("Manager with ID {} deleted successfully", id);
            } else {
                log.error("Invalid role for user with ID {}: {}", id, roleName);
                throw new IllegalStateException(UserConstants.INVALID_USER_ROLE);
            }
        } else {
            throw new InvalidRequestException(INVALID_REQUEST);
        }
        MessageOutDTO messageOutDto = new MessageOutDTO(UserConstants.USER_DELETION_MESSAGE);
        return StandardResponseOutDTO.success(messageOutDto, null);
    }

    /**
     * Fetches all active users.
     *
     * @return a list of UserOutDTO
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getAllActiveUsers() {
        log.info("Fetching all users");
        try {
            List<User> employees = userRepository.findAll();
            if (employees.isEmpty()) {
                log.warn("No employees found");
                return StandardResponseOutDTO.success(Collections.emptyList(), "No user found");
            }
            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                if (user.isActive() && (user.getUserId() != UserConstants.getAdminId())) {
                    Optional<User> optionalmanager = userRepository.findById(user.getManagerId());

                    if (!optionalmanager.isPresent()) {
                        throw new ResourceNotFoundException(USER_NOT_FOUND + "Manager not found");
                    }
                   User manager = optionalmanager.get();
                    String managerName = manager.getFirstName() + " " + manager.getLastName();
                    Role role = roleRepository.findById(user.getRoleId()).orElseThrow(
                            () -> {
                                log.error("Role with ID {} not found", user.getRoleId());
                                throw new ResourceNotFoundException("Role not found for the Id");
                            }
                    );
                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName, role.getName());
                    userDtos.add(userDto);
                }
            }
            log.info("Successfully fetched {} users", userDtos.size());
            return StandardResponseOutDTO.success(userDtos, "User fetched Successfully");
        } catch (Exception e) {
            log.error("Error fetching users", e);
            throw new RuntimeException(UserConstants.DATABASE_ERROR, e);
        }
    }

    /**
     * Fetches all inactive users.
     *
     * @return list of UserOutDTO
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getAllInactiveUsers() {
        log.info("Fetching all inactive users");
        try {
            List<User> employees = userRepository.findAll();
            if (employees.isEmpty()) {
                log.warn("No employees found");
                return StandardResponseOutDTO.success(Collections.emptyList(), "User does not exist");
            }
            List<UserOutDTO> userDtos = new ArrayList<>();
            for (User user : employees) {
                if (!user.isActive() && (user.getUserId() != UserConstants.getAdminId())) {
                    User manager = userRepository.findById(user.getManagerId())
                            .orElseThrow(() -> {
                                log.error("Manager with ID {} not found", user.getManagerId());
                                throw new ResourceNotFoundException(USER_NOT_FOUND);
                            });
                    String managerName = manager.getFirstName() + " " + manager.getLastName();
                    Role role = roleRepository.findById(user.getRoleId()).orElseThrow(
                            () -> {
                                log.error("Role with ID {} not found", user.getManagerId());
                                throw new ResourceNotFoundException(USER_NOT_FOUND);
                            }
                    );
                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName, role.getName());
                    userDtos.add(userDto);
                }
            }
            log.info("Successfully fetched {} inactive users", userDtos.size());
            return StandardResponseOutDTO.success(userDtos, "User fetched Successfully");
        } catch (Exception e) {
            log.error("Error fetching users", e);
            throw new RuntimeException(UserConstants.DATABASE_ERROR, e);
        }
    }

    /**
     * Changes a user's role.
     *
     * @param userId      the user ID
     * @param newRoleName the new role name
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> changeUserRole(final long userId, final String newRoleName) {
        log.info("Attempting to change role for user with ID: {} to role: {}", userId, newRoleName);
        try {
            if (userId != UserConstants.getAdminId()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("User with ID {} not found", userId);
                            throw new ResourceNotFoundException(USER_NOT_FOUND);
                        });
                Role role = roleRepository.findByName(newRoleName)
                        .orElseThrow(() -> {
                            log.error("Invalid role provided: {}", newRoleName);
                            throw new IllegalArgumentException(UserConstants.INVALID_USER_ROLE);
                        });
                user.setRoleId(role.getRoleId());
                user.setUpdatedAt(new Date());
                userRepository.save(user);
                log.info("Successfully changed role for user with ID: {} to {}", userId, newRoleName);
                MessageOutDTO messageOutDto = new MessageOutDTO(UserConstants.UPDATED);
                return StandardResponseOutDTO.success(messageOutDto, UserConstants.UPDATED);
            } else {
                throw new InvalidRequestException(INVALID_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error changing role for user with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }

    /**
     * Fetches employees under a manager.
     *
     * @param userId the manager's user ID
     * @return a list of UserOutDTO
     */
    @Override
    public StandardResponseOutDTO<List<UserOutDTO>> getManagerEmployee(final long userId) {
        log.info("Fetching employees for manager with ID: {}", userId);
        try {
            if (userId != UserConstants.getAdminId()) {
                User manager = userRepository.findById(userId).orElseThrow(() -> {
                    log.error("Manager with ID {} not found", userId);
                    throw new ResourceNotFoundException(USER_NOT_FOUND);
                });
                String managerName = manager.getFirstName() + manager.getLastName();
                List<User> users = userRepository.findByManagerId(userId);
                if (users.isEmpty()) {
                    log.warn("No employees found");
                    return StandardResponseOutDTO.success(Collections.emptyList(), USER_NOT_FOUND);
                }
                List<UserOutDTO> response = new ArrayList<>();
                for (User user : users) {
                    UserOutDTO userDto = userDTOConverter.userToOutDto(user, managerName, "employee");
                    response.add(userDto);
                }
                log.info("Successfully fetched {} employees for manager with ID: {}", response.size(), userId);
                return StandardResponseOutDTO.success(response, "Successfully fetched employee for Manager");
            } else {
                throw new InvalidRequestException(INVALID_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error fetching employees for manager with ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }

    @Override
    public StandardResponseOutDTO<AdminDashboardStatsOutDTO> getAdminStats() {
        log.info("Fetching admin dashboard statistics");
        try {
            AdminDashboardStatsOutDTO adminDashboardStatsOutDTO = userRepository.getAdminDashboardStats();
            return StandardResponseOutDTO.success(adminDashboardStatsOutDTO, "Dashboard data fetched.");
        } catch(Exception e) {
            log.error("Unexpected error : ",e);
            throw new RuntimeException("Unexpected error occurred");
        }
    }

    /**
     * Updates user details.
     *
     * @param registerDto the user input DTO
     * @param userId      the user ID
     * @return a message response
     */
    @Override
    public MessageOutDTO updateUserDetails(final UserInDTO registerDto, final long userId) {
        log.info("updating user information");
        try {
            if (userId != UserConstants.getAdminId()) {
                User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    throw new ResourceNotFoundException(USER_NOT_FOUND);
                });
                if (!registerDto.getFirstName().isEmpty()) {
                    user.setFirstName(registerDto.getFirstName());
                }
                if (!registerDto.getLastName().isEmpty()) {
                    user.setLastName(registerDto.getLastName());
                }
                if (!registerDto.getUserName().isEmpty()) {
                    user.setUserName(registerDto.getUserName());
                }
                if (!registerDto.getEmail().isEmpty()) {
                    user.setEmail(registerDto.getEmail());
                }
                if (registerDto.getRole() != null) {
                    Optional<Role> role = roleRepository.findByName(registerDto.getRole());
                    user.setRoleId(role.get().getRoleId());
                }
                userRepository.save(user);
                return new MessageOutDTO(USER_UPDATED_SUCCESSFULLY);
            } else {
                throw new InvalidRequestException(INVALID_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error updating user details for user ID: {}", userId, e);
            throw new RuntimeException(UserConstants.ERROR, e);
        }
    }




    /**
     * Deletes bundle.
     *
     * @param bundleId the user input DTO
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> deleteBundle(final long bundleId) {
        try {
            log.info("fetching all the enrollments with bundle Id {}", bundleId);
            List<Enrollment> enrollments = enrollmentRepository.findByBundleId(bundleId);
            for (Enrollment enrol : enrollments) {
                enrol.setActive(false);
                enrollmentRepository.save(enrol);
            }
            log.info("Deleting bundle from courseBundle and Bundles");
            courseMicroserviceClient.deleteBundle(bundleId);
            MessageOutDTO message = new MessageOutDTO("Bundle Deleted");
            return StandardResponseOutDTO.success(message, "Bundle Deleted");
        } catch (RuntimeException e) {
            log.warn("Error occurred while deleting bundle");
            throw new RuntimeException(e);
        }
    }



    /**
     * Remove course from the bundle.
     *
     * @param bundleId
     * @param courseId
     * @return a message response
     */
    @Override
    public StandardResponseOutDTO<MessageOutDTO> removeCourseFromBundle(final Long bundleId, final Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByBundleIdAndCourseId(bundleId, courseId);
        for (Enrollment enrol : enrollments) {
            enrol.setActive(false);
            enrollmentRepository.save(enrol);
        }

       StandardResponseOutDTO<MessageOutDTO> message =  courseMicroserviceClient.removeCourseFromBundle(bundleId,
               courseId).getBody();



        return message;
    }


    @Override
    public StandardResponseOutDTO<BulkUploadResponseOutDTO> bulkUploadUsers(MultipartFile file) {
        log.info("Starting bulk upload process for file: {}", file.getOriginalFilename());

        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);

        List<BulkUserUploadInDTO> users;
        try {
            users = parseFile(file, fileExtension);
        } catch (Exception e) {
            log.error("Error parsing file: {}", e.getMessage());
            throw new InvalidRequestException("Error parsing file: " + e.getMessage());
        }

        // Batch validation to minimize DB hits
        validateUsersInBatch(users);

        BulkUploadResponseOutDTO result = processUsersInBatch(users);

        return StandardResponseOutDTO.success(result, "Bulk upload completed");
    }

    private List<BulkUserUploadInDTO> parseFile(MultipartFile file, String extension) throws IOException {
        List<BulkUserUploadInDTO> users = new ArrayList<>();

        switch (extension.toLowerCase()) {
            case "csv":
            case "txt":
                users = parseCsvFile(file);
                break;
            case "xlsx":
            case "xls":
                users = parseExcelFile(file);
                break;
            default:
                throw new InvalidRequestException("Unsupported file format. Only CSV, TXT, XLS, XLSX are supported");
        }

        return users;
    }

    private List<BulkUserUploadInDTO> parseCsvFile(MultipartFile file) throws IOException {
        List<BulkUserUploadInDTO> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowNumber = 0;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header row
                }

                String[] columns = line.split(",");
                if (columns.length != 5) {
                    log.warn("Invalid number of columns in row {}: expected 5, got {}", rowNumber, columns.length);
                    continue;
                }

                BulkUserUploadInDTO user = BulkUserUploadInDTO.builder()
                        .firstName(columns[0].trim())
                        .lastName(columns[1].trim())
                        .email(columns[2].trim())
                        .employeeNumber(columns[3].trim())
                        .role(columns[4].trim())
                        .rowNumber(rowNumber)
                        .build();

                users.add(user);
            }
        }

        return users;
    }

    private List<BulkUserUploadInDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<BulkUserUploadInDTO> users = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                if (row.getPhysicalNumberOfCells() < 5) {
                    log.warn("Row {} has insufficient columns", i + 1);
                    continue;
                }

                BulkUserUploadInDTO user = BulkUserUploadInDTO.builder()
                        .firstName(getCellValueAsString(row.getCell(0)))
                        .lastName(getCellValueAsString(row.getCell(1)))
                        .email(getCellValueAsString(row.getCell(2)))
                        .employeeNumber(getCellValueAsString(row.getCell(3)))
                        .role(getCellValueAsString(row.getCell(4)))
                        .rowNumber(i + 1)
                        .build();

                users.add(user);
            }
        }

        return users;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private void validateUsersInBatch(List<BulkUserUploadInDTO> users) {
        if (users.isEmpty()) {
            throw new InvalidRequestException("No valid user records found in file");
        }

        // Extract all emails and employee numbers for batch validation
        Set<String> emails = users.stream().map(BulkUserUploadInDTO::getEmail).collect(Collectors.toSet());
        Set<String> employeeNumbers = users.stream().map(BulkUserUploadInDTO::getEmployeeNumber).collect(Collectors.toSet());

        // Single DB query to check existing emails
        List<String> existingEmails = userRepository.findExistingEmails(new ArrayList<>(emails));

        // Single DB query to check existing employee numbers (assuming you have this method)
        List<String> existingEmployeeNumbers = userRepository.findExistingEmployeeNumbers(new ArrayList<>(employeeNumbers));

        if (!existingEmails.isEmpty()) {
            throw new ResourceConflictException("Following emails already exist: " + String.join(", ", existingEmails));
        }

        if (!existingEmployeeNumbers.isEmpty()) {
            throw new ResourceConflictException("Following employee numbers already exist: " + String.join(", ", existingEmployeeNumbers));
        }
    }

    private BulkUploadResponseOutDTO processUsersInBatch(List<BulkUserUploadInDTO> users) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalCount = users.size();

        // Get role mappings once to avoid repeated DB calls
        Map<String, Long> roleMap = getRoleMapping();

        for (BulkUserUploadInDTO bulkUser : users) {
            try {
                // Generate password: FirstName@12345 (first letter capital, rest lowercase)
                String password = generatePassword(bulkUser.getFirstName());

                // Convert to RegisterDto
                RegisterDto registerDto = RegisterDto.builder()
                        .firstName(bulkUser.getFirstName())
                        .lastName(bulkUser.getLastName())
                        .userName(bulkUser.getEmployeeNumber()) // Using employee number as username
                        .email(bulkUser.getEmail())
                        .password(password)
                        .roleId(roleMap.get(bulkUser.getRole().toLowerCase()))
                        .build();

                // Validate the DTO
                validateRegisterDto(registerDto);

                // Use existing register method
                register(registerDto);
                successCount++;

            } catch (Exception e) {
                String error = String.format("Row %d (%s %s): %s",
                        bulkUser.getRowNumber(),
                        bulkUser.getFirstName(),
                        bulkUser.getLastName(),
                        e.getMessage());
                errors.add(error);
                log.warn("Failed to process user at row {}: {}", bulkUser.getRowNumber(), e.getMessage());
            }
        }

        return BulkUploadResponseOutDTO.builder()
                .totalRecords(totalCount)
                .successfulUploads(successCount)
                .failedUploads(totalCount - successCount)
                .errors(errors)
                .message(String.format("Processed %d users: %d successful, %d failed",
                        totalCount, successCount, totalCount - successCount))
                .build();
    }

    private String generatePassword(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidRequestException("First name cannot be empty");
        }

        String cleanedName = firstName.trim();
        String password = cleanedName.substring(0, 1).toUpperCase() +
                cleanedName.substring(1).toLowerCase() + "@12345";
        return password;
    }

    private Map<String, Long> getRoleMapping() {
        Map<String, Long> roleMap = new HashMap<>();
        List<Role> roles = roleRepository.findAll();

        for (Role role : roles) {
            roleMap.put(role.getName().toLowerCase(), role.getRoleId());
        }

        return roleMap;
    }

    private void validateRegisterDto(RegisterDto registerDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(registerDto);

        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            throw new ResourceNotValidException("Validation failed: " + String.join(", ", errorMessages));
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidRequestException("Invalid file name");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Resource generateTemplate(String format) throws IOException {
        String[] headers = {"First Name", "Last Name", "Email", "Employee Number", "Role"};
        String[] sampleData = {"John", "Doe", "john.doe@nucleusteq.com", "EMP001", "employee"};

        if ("csv".equalsIgnoreCase(format) || "txt".equalsIgnoreCase(format)) {
            return generateCsvTemplate(headers, sampleData);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            return generateExcelTemplate(headers, sampleData);
        } else {
            throw new InvalidRequestException("Unsupported template format");
        }
    }

    private Resource generateCsvTemplate(String[] headers, String[] sampleData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        // Write headers
        writer.println(String.join(",", headers));
        // Write sample data
        writer.println(String.join(",", sampleData));

        writer.flush();
        writer.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private Resource generateExcelTemplate(String[] headers, String[] sampleData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Template");

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Create sample data row
        Row dataRow = sheet.createRow(1);
        for (int i = 0; i < sampleData.length; i++) {
            Cell cell = dataRow.createCell(i);
            cell.setCellValue(sampleData[i]);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

}
