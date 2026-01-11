package ro.ugal.autopartsstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ugal.autopartsstore.domain.CustomerAddress;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
}
