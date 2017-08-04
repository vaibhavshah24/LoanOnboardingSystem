package com.finance.respository;

import com.finance.enums.UserRole;
import com.finance.enums.Zone;
import com.finance.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by vashah on 2017-07-26.
 */
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsernameAndPasswordAndUserRole(String username, String password, UserRole userRole);
    User findFirstByUserRoleAndZoneOrderByLastRecordAssignedOnAsc(UserRole userRole, Zone zone);
    User findFirstByUserRoleAndZoneAndUserIdNotOrderByLastRecordAssignedOnAsc(UserRole userRole, Zone zone, String userId);
    List<User> findByUserRoleAndZone(UserRole userRole, Zone zone);
    List<User> findByUserRoleAndZoneAndUserIdNot(UserRole userRole, Zone zone, String userId);
}
