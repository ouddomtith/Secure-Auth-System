package org.application.secureauthsystem.repository;

import org.application.secureauthsystem.model.entity.PushSubscription;
import org.application.secureauthsystem.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription,Long> {
    List<PushSubscription> findByUser(User user);
    Optional<PushSubscription> findByEndpoint(String endpoint);
    List<PushSubscription> findAll();
}
