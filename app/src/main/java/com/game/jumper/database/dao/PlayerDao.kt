package com.game.jumper.database.dao

/*************************************************************************
    \file   PlayerDao.kt
    \author Chua Yip Xuan, 2001488
    \date   Feb 24, 2023
    \brief  This file consist of an interface for PlayerDao
 *************************************************************************/

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.game.jumper.database.entity.Player

/*************************************************************************
 *   /brief  An interface for PlayerDao
 *************************************************************************/
@Dao
interface PlayerDao {
    /*************************************************************************
     *   /brief  This function inserts a PLayer entity into the database
     *************************************************************************/
    @Insert
    fun insert(player: Player)

    /*************************************************************************
     *   /brief  This function gets the PLayer entity by the username
     *************************************************************************/
    @Query("SELECT * FROM player_table WHERE user_name = :username")
    fun findByUsername(username: String): Player?
}
