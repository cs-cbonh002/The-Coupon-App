package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.UserProfile;

@Dao
public interface UserProfileDao {
    @Insert
    void insertUserProfile(UserProfile userProfile);

    @Update
    void updateUserProfile(UserProfile userProfile);

    @Delete
    void deleteUserProfile(UserProfile userProfile);

    @Query("SELECT * FROM user_profile WHERE id = :id")
    UserProfile getUserProfileById(int id);

    // Additional queries as needed
}
