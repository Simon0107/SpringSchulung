package de.sme.repo;

import de.sme.entity.NewsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsUserRepository extends JpaRepository<NewsUser, String> {
    NewsUser findByUsername(String username);
    NewsUser save(NewsUser user);
    long count();

}
