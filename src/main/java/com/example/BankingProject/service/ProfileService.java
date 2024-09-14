package com.example.BankingProject.service;

import com.example.BankingProject.dto.BankAccountCreationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    public Optional<BankAccountCreationDto> getProfileById(int id) {
        String sql = "SELECT * FROM Customers WHERE CustomerId = ?";
        try {
            BankAccountCreationDto profile = jdbcTemplate.queryForObject(sql, new Object[]{id}, (resultSet, rowNum) -> {
                BankAccountCreationDto bankAccountCreationDto = new BankAccountCreationDto();
                bankAccountCreationDto.setId(resultSet.getInt("CustomerId"));
                bankAccountCreationDto.setUserName(resultSet.getString("UserName"));
                bankAccountCreationDto.setFirstName(resultSet.getString("FirstName"));
                bankAccountCreationDto.setLastName(resultSet.getString("LastName"));
                bankAccountCreationDto.setEmail(resultSet.getString("Email"));
                bankAccountCreationDto.setPhoneNumber(resultSet.getString("Phone"));
                bankAccountCreationDto.setAddress(resultSet.getString("Address"));
                bankAccountCreationDto.setCity(resultSet.getString("City"));
                bankAccountCreationDto.setState(resultSet.getString("State"));
                bankAccountCreationDto.setZipCode(resultSet.getString("ZipCode"));
                return bankAccountCreationDto;
            });
            logger.info("Profile fetched successfully!..");
            assert profile != null;
            return Optional.of(profile);
        } catch (Exception ex) {
            logger.error("Error occurs while fetching the profile", ex);
            return Optional.empty();
        }
    }
}
