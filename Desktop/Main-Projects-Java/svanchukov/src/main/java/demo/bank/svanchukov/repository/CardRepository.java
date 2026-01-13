package demo.bank.svanchukov.repository;

import demo.bank.svanchukov.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);


}
