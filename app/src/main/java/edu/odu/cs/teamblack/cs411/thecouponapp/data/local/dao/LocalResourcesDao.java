package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.LocalResource;

@Dao
public interface LocalResourcesDao {

    @Insert
    void insertLocalResource(LocalResource localResource);

    @Update
    void updateLocalResource(LocalResource localResource);

    @Delete
    void deleteLocalResource(LocalResource localResource);

    @Query("SELECT * FROM local_resources")
    LocalResource[] getAllLocalResources();

    @Query("SELECT * FROM local_resources WHERE id = :uuid")
    LocalResource getLocalResourceByUuid(String uuid);

    // Additional queries as needed
}
