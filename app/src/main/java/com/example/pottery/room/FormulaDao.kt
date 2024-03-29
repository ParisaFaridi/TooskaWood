package com.example.pottery.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFormula(formula: Formula)

    @Insert
    fun addItem(vararg item: Item)

    @Query("SELECT Count(formulaName) FROM Formula WHERE formulaName = :name")
    fun isFormulaNew(name:String):Int

    @Query("SELECT * FROM Formula")
    fun getAll():LiveData<List<Formula>?>?

    @Query("SELECT * FROM Formula")
    fun getAllFormulas():List<Formula>

    @Query("SELECT * FROM Formula WHERE formulaName = :formulaName")
    fun getFormulaWithItems(formulaName : String ):LiveData<FormulaWithItems>?

    @Query("SELECT * FROM Formula WHERE formulaName LIKE '%' || :searchedQuery || '%' ORDER BY formulaName DESC")
    fun search(searchedQuery:String):LiveData<List<Formula>?>?

    @Update
    fun update(formula: Formula)

    @Delete
    fun delete(formula: Formula)

    @Query("DELETE FROM Formula")
    fun deleteAll()

    @Query("SELECT Count(id) FROM Item WHERE formulaName = :formulaName AND code = :itemCode")
    fun itemIsRepeated(itemCode: String,formulaName:String):Int

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("DELETE FROM Item WHERE formulaName = :formulaName")
    fun deleteItems(formulaName: String)
}