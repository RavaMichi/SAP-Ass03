package service.application;

import java.util.Date;

/** Outbound port
 * Calculator for the rental fee
 */
public interface FeeCalculator {
    int minimumAmountForConnection();
    int calculateFee(Date startDate, Date endDate);
}
