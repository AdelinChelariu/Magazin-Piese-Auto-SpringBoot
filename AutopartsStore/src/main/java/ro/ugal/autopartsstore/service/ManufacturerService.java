package ro.ugal.autopartsstore.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ugal.autopartsstore.audit.Audit;
import ro.ugal.autopartsstore.domain.Manufacturer;
import ro.ugal.autopartsstore.dto.manufacturer.ManufacturerCreateRequest;
import ro.ugal.autopartsstore.dto.manufacturer.ManufacturerUpdateRequest;
import ro.ugal.autopartsstore.repository.ManufacturerRepository;

import java.util.List;

@Service
public class ManufacturerService {

    private final ManufacturerRepository repo;

    public ManufacturerService(ManufacturerRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ManufacturerRepository.ManufacturerRow> listRows() {
        return repo.findRows();
    }

    @Transactional(readOnly = true)
    public ManufacturerRepository.ManufacturerRow getRow(Long id) {
        return repo.findRowById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manufacturer not found: " + id));
    }

    @Audit(action = "CREATE_MANUFACTURER", entity = "Manufacturer")
    @Transactional
    public ManufacturerRepository.ManufacturerRow create(ManufacturerCreateRequest req) {
        Manufacturer m = new Manufacturer();
        m.setName(req.name.trim());
        m.setCountry(req.country == null ? null : req.country.trim());
        m.setWebsite(req.website == null ? null : req.website.trim());

        try {
            repo.save(m);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Manufacturer already exists or invalid data.");
        }

        return getRow(m.getId());
    }

    @Audit(action = "UPDATE_MANUFACTURER", entity = "Manufacturer")
    @Transactional
    public ManufacturerRepository.ManufacturerRow update(Long id, ManufacturerUpdateRequest req) {
        Manufacturer m = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Manufacturer not found: " + id));

        m.setName(req.name.trim());
        m.setCountry(req.country == null ? null : req.country.trim());
        m.setWebsite(req.website == null ? null : req.website.trim());

        try {
            repo.save(m);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Manufacturer already exists or invalid data.");
        }

        return getRow(m.getId());
    }

    @Audit(action = "DELETE_MANUFACTURER", entity = "Manufacturer")
    @Transactional
    public Long delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Manufacturer not found: " + id);
        }
        repo.deleteById(id);
        return id;
    }
}
