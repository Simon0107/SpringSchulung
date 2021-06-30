package de.sme.repo;

import de.sme.model.NewsUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NewsUserRepositoryImpl implements NewsUserRepository {
    private Map<String, NewsUser> map = new ConcurrentHashMap<>();

    public NewsUserRepositoryImpl(PasswordEncoder passwordEncoder) {
        map.put("buck", new NewsUser("buck", passwordEncoder.encode("buck"), "Buck", "Rogers",
                LocalDate.now()));
    }
    @Override
    public NewsUser findByUsername(String username) {
        return map.get(username);
    }

    @Override
    public NewsUser save(NewsUser user) {
        map.put(user.getUsername(), user);
        return user;
    }

    @Override
    public long count() {
        return map.size();
    }
}
