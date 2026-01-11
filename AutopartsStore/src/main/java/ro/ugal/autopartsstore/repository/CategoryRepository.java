package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.ugal.autopartsstore.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // (PĂSTRĂM pentru dropdown-urile existente)
    @Query("select c.id as id, c.name as name from Category c order by c.name asc")
    List<CategoryOption> findOptions();

    interface CategoryOption {
        Long getId();
        String getName();
    }

    // Pentru pagina de management (listare cu parent)
    @Query("""
           select c.id as id,
                  c.name as name,
                  p.id as parentId,
                  p.name as parentName
           from Category c
           left join c.parent p
           order by c.name asc
           """)
    List<CategoryRow> findRows();

    @Query("""
           select c.id as id,
                  c.name as name,
                  p.id as parentId,
                  p.name as parentName
           from Category c
           left join c.parent p
           where c.id = :id
           """)
    Optional<CategoryRow> findRowById(Long id);

    interface CategoryRow {
        Long getId();
        String getName();
        Long getParentId();
        String getParentName();
    }
}
