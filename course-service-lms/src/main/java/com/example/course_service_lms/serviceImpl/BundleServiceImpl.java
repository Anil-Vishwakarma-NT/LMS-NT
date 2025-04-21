package com.example.course_service_lms.serviceImpl;

import com.example.course_service_lms.constants.BundleConstants;
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

import static com.example.course_service_lms.constants.BundleConstants.*;

@Service
@Slf4j
public class BundleServiceImpl implements BundleService {
    @Autowired
    private BundleRepository bundleRepository;


    @Override
    public Bundle createBundle(BundleDTO bundleDTO) {
        try {
            log.info("Attempting to create a new bundle: {}", bundleDTO.getBundleName());

            // Check for duplicate bundle name
            if (bundleRepository.existsByBundleName(bundleDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, bundleDTO.getBundleName()));
            }
            // Convert DTO to Entity
            Bundle bundle = new Bundle();
            bundle.setBundleName(bundleDTO.getBundleName());

            // Save bundle entity
            Bundle savedBundle = bundleRepository.save(bundle);
            log.info("Bundle '{}' created successfully with ID: {}", savedBundle.getBundleName(), savedBundle.getBundleId());

            // Convert Entity to DTO before returning
            return savedBundle;
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR,e);
        }
    }



    @Override
    public List<Bundle> getAllBundles() {
        log.info("Fetching all bundles");

        List<Bundle> bundles = bundleRepository.findAll();

        if (bundles.isEmpty()) {
            log.warn("No bundles found in the system");
            throw new ResourceNotFoundException(NO_BUNDLES_FOUND);
        }

        log.info("Successfully retrieved {} bundles", bundles.size());
        return bundles;
    }

    @Override
    public Optional<Bundle> getBundleById(Long bundleId) {
        try {

        log.info("Fetching bundle with ID: {}", bundleId);

        Optional<Bundle> optionalBundle = bundleRepository.findById(bundleId);

        if (!optionalBundle.isPresent()) {
            log.error("Bundle with ID {} not found", bundleId);
            throw new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
        }

        log.info("Successfully retrieved bundle: {}", optionalBundle.get().getBundleName());
        return optionalBundle;
        }
        catch(ResourceNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }

    }

    @Override
    public BundleDTO updateBundle(Long bundleId, BundleDTO bundleDTO) {
        try {
            log.info("Attempting to update bundle with ID: {}", bundleId);
            // Check if the bundle exists
            Bundle existingBundle = bundleRepository.findById(bundleId).orElseThrow(() -> {
                log.error("Bundle with ID {} not found", bundleId);
                return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, bundleId));
            });

            // Validate if the updated name is unique (excluding the same bundle)
            if (bundleRepository.existsByBundleName(bundleDTO.getBundleName()) && !existingBundle.getBundleName().equalsIgnoreCase(bundleDTO.getBundleName())) {
                log.error("Bundle with name '{}' already exists", bundleDTO.getBundleName());
                throw new ResourceAlreadyExistsException(String.format(BUNDLE_ALREADY_EXISTS, bundleDTO.getBundleName()));
            }
            // Update the bundle entity
            existingBundle.setBundleName(bundleDTO.getBundleName());
            Bundle updatedBundle = bundleRepository.save(existingBundle);
            log.info("Successfully updated bundle with ID: {}", updatedBundle.getBundleId());

            // Convert to DTO before returning
            return new BundleDTO(updatedBundle.getBundleName());
        }
        catch(ResourceNotFoundException e){
            throw e;
        }
        catch(ResourceAlreadyExistsException e){
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }
    }

    @Override
    public void deleteBundle(Long id) {
        try {
            log.info("Attempting to delete bundle with ID: {}", id);

            // Check if the bundle exists
            Bundle existingBundle = bundleRepository.findById(id).orElseThrow(() -> {
                log.error("Bundle with ID {} not found", id);
                return new ResourceNotFoundException(String.format(BUNDLE_NOT_FOUND_BY_ID, id));
            });
            // Delete the bundle
            bundleRepository.delete(existingBundle);
            log.info("Successfully deleted bundle with ID: {}", id);
        }
        catch(ResourceNotFoundException e){
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(GENERAL_ERROR);
        }

    }

    @Override
    public boolean existsByBundleId(Long id) {
        return bundleRepository.existsById(id);
    }

    @Override
    public long countBundles() {
        return bundleRepository.count();
    }

}
