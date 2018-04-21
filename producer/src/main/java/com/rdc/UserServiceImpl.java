package com.rdc;

/**
 * @author SD
 */
public class UserServiceImpl implements UserService {

    @Override
    public String getUser(Integer id) {
        return "User" + id;
    }
}
