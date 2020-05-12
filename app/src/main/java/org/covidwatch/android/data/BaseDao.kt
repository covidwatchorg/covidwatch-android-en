package org.covidwatch.android.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert
    suspend fun insert(obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert
    suspend fun insert(vararg obj: T)

    /**
     * Insert a list of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert
    suspend fun insert(obj: List<T>)

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    suspend fun update(obj: T)

    /**
     * Update objects in the database.
     *
     * @param obj the list of objects to be updated
     */
    @Update
    suspend fun update(obj: List<T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    suspend fun delete(obj: T)

    /**
     * Delete an list of objects from the database
     *
     * @param obj the list to be deleted
     */
    @Delete
    suspend fun delete(obj: List<T>)
}