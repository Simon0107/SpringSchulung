package de.sme.repo;

import de.sme.model.NewsUser;

public interface NewsUserRepository {
    NewsUser findByUsername(String username);
    NewsUser save(NewsUser user);
    long count();
}
