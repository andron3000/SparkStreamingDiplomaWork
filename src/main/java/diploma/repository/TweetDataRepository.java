package diploma.repository;

import diploma.model.TweetData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetDataRepository extends JpaRepository<TweetData, Long> {
}