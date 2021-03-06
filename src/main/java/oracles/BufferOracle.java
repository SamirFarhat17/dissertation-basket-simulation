package oracles;

import stakeholders.User;

import java.util.ArrayList;
import java.util.HashMap;

public class BufferOracle extends Oracle {

    // Attributes of buffer oracle
    private double bufferHoldings;
    private double totalDebtCeiling;

    // Constructors
    public BufferOracle(String bufferOracle, String oracleStatus, double bufferHoldings, double totalDebtCeiling) {
        this.oracleID = bufferOracle;
        this.oracleStatus = oracleStatus;
        this.bufferHoldings = bufferHoldings;
        this.totalDebtCeiling = totalDebtCeiling;
    }

    // Getters and Setters
    public double getBufferHoldings() { return this.bufferHoldings; }
    public void setBufferHoldings(double bufferHoldings) { this.bufferHoldings = bufferHoldings; }

    public double getTotalDebtCeiling() { return this.totalDebtCeiling; }
    public void setTotalDebtCeiling(double totalDebtCeiling) { this.totalDebtCeiling = totalDebtCeiling; }


    // Methods
    public void updateOracle(String date, double totalDebtCeilings) {
        setTotalDebtCeiling(totalDebtCeiling);
    }
}
