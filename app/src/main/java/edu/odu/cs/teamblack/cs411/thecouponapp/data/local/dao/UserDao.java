package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.User;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users")
    User[] getAllUsers();

    @Query("SELECT * FROM users WHERE id = :uuid")
    User getUserByUuid(String uuid);

    // Additional queries as needed
}
