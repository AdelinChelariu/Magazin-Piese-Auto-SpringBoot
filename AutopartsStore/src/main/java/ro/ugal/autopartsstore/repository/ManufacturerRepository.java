package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.ugal.autopartsstore.domain.Manufacturer;

import java.util.List;
import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    // pentru dropdown (Products)
    @Query("select m.id as id, m.name as name from Manufacturer m order by m.name asc")
    List<ManufacturerOption> findOptions();

    interface ManufacturerOption {
        Long getId();
        String getName();
    }

    // pentru management (tabel)
    @Query("""
           select m.id as id,
                  m.name as name,
                  m.country as country,
                  m.website as website
           from Manufacturer m
           order by m.name asc
           """)
    List<ManufacturerRow> findRows();

    @Query("""
           select m.id as id,
                  m.name as name,
                  m.country as country,
                  m.website as website
           from Manufacturer m
           where m.id = :id
           """)
    Optional<ManufacturerRow> findRowById(Long id);

    boolean existsByName(String name);

    interface ManufacturerRow {
        Long getId();
        String getName();
        String getCountry();
        String getWebsite();
    }
}
