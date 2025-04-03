package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.dto.BundleDTO;
import com.example.course_service_lms.entity.Bundle;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.BundleRepository;
import com.example.course_service_lms.service.BundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {


    private BundleRepository bundleRepository;


    @Override
    public BundleDTO createBundle(BundleDTO bundleDTO) {
        try {
            log.info("Attempting to create a new bundle: {}", bundleDTO.getBundleName());

            // Check for duplicate bundle name
            if (bundleRepository.existsByBundleName(bundleDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleDTO.getBundleName());
                throw new ResourceAlreadyExistsException("A bundle with the name '" + bundleDTO.getBundleName() + "' already exists.");
            }
            // Convert DTO to Entity
            Bundle bundle = new Bundle();
            bundle.setBundleName(bundleDTO.getBundleName());

            // Save bundle entity
            Bundle savedBundle = bundleRepository.save(bundle);
            log.info("Bundle '{}' created successfully with ID: {}", savedBundle.getBundleName(), savedBundle.getBundleId());

            // Convert Entity to DTO before returning
            return new BundleDTO(savedBundle.getBundleName());
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }
    }



    @Override
    public List<Bundle> getAllBundles() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Bundle> getBundleById(Long bundleId) {
        try {

        log.info("Fetching bundle with ID: {}", bundleId);

        Optional<Bundle> optionalBundle = bundleRepository.findById(bundleId);

        if (optionalBundle.isPresent()) {
            log.error("Bundle with ID {} not found", bundleId);
            throw new ResourceNotFoundException("Bundle with ID " + bundleId + " not found");
        }

        log.info("Successfully retrieved bundle: {}", optionalBundle.get().getBundleName());
        return optionalBundle;
        }
        catch(ResourceNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }

    }

    @Override
    public BundleDTO updateBundle(Long id, BundleDTO bundleDTO) {
        return null;
    }

    @Override
    public void deleteBundle(Long id) {

    }
}
