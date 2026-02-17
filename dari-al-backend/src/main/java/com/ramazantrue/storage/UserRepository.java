package com.ramazantrue.storage;

import com.ramazantrue.domain.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByPhone(Connection c, String phone) throws SQLException;

    User create(Connection c, String phone, String name) throws SQLException;
}

