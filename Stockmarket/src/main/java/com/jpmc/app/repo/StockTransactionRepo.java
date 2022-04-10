package com.jpmc.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jpmc.app.dataobjects.StockTransaction;

import java.util.List;


@Repository
public interface StockTransactionRepo  extends JpaRepository<StockTransaction, String>{

    @Query(value = "select * from stock_transaction  where TIMESTAMPDIFF(MINUTE,`creation_date`,NOW()) < :duration and `id`=:stockId",nativeQuery = true)
	List<StockTransaction> findByStockIdAndCreatedDuration(@Param("stockId") String stockId, @Param("duration") int duration);

}
