package stakeholders;

import json.DataExtraction;
import json.JsonReader;
import oracles.CollateralOracle;
import oracles.EmergencyOracle;
import oracles.Oracle;
import oracles.VaultManagerOracle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class User {

    // User attributes
    private final String userID;
    private ArrayList<Vault> vaults;
    private HashMap<String, Double> collaterals;
    private double bsktTokens;
    private double bsktHoldings;
    private HashMap<String,Double> feesOwed;
    private double desiredBasket;
    private double desiredPrice;
    private HashMap<String, Double> colatWanted;

    // Constructor
    public User(String userID, ArrayList<Vault> vaults, HashMap<String,Double> collaterals, double bsktHoldings, double bsktTokens,
                HashMap<String,Double> feesOwed, double desiredBasket, double desiredPrice, HashMap<String,Double> colatWanted) throws IOException {
        this.userID = userID;
        this.vaults = vaults;
        this.collaterals = collaterals;
        this.bsktHoldings = bsktHoldings;
        this.bsktTokens = bsktTokens;
        this.feesOwed = feesOwed;
        this.desiredBasket = desiredBasket;
        this.desiredPrice = desiredPrice;
        this.colatWanted = colatWanted;
    }

    // Getters and Setters
    public String getUserID() { return this.userID; }

    public ArrayList<Vault> getVaults() { return this.vaults; }
    public void setVaults(ArrayList<Vault> vaults) { this.vaults = vaults; }
    public void addVault(Vault vault) { this.vaults.add(vault); }

    public ArrayList<Vault> appendVault(Vault v) {
        this.vaults.add(v);
        return this.vaults;
    }

    public HashMap<String,Double> getCollaterals() { return this.collaterals; }
    public void setCollaterals(HashMap<String,Double> collaterals) { this.collaterals = collaterals; }
    // Add collaterals without duplicates
    public HashMap<String,Double> addCollaterals(String collateralType, double amount) {
        if(!this.collaterals.containsKey(collateralType)) {
            this.collaterals.put(collateralType, amount);
        }
        else {
            this.collaterals.replace(collateralType, amount);
        }
        return this.collaterals;
    }

    public double getBsktHoldings() { return this.bsktHoldings; }
    public void setBsktHoldings(double bsktHoldings) {this.bsktHoldings = bsktHoldings;}

    public double getBsktTokens() { return this.bsktTokens; }
    public void setBsktTokens(double bsktTokens) { this.bsktTokens = bsktTokens; }

    public HashMap<String,Double> getFeesOwed() { return this.feesOwed; }
    public void setFeesOwed(HashMap<String,Double> feesOwed) { this.feesOwed = feesOwed;}
    // Add fees without conflict
    public void addFees(String feeType, double amount) {
        if(!this.feesOwed.containsKey(feeType)) {
            this.feesOwed.put(feeType, amount);
        }
        else {
            this.feesOwed.replace(feeType, amount);
        }
    }

    public double getDesiredBasket() { return this.desiredBasket; }
    public void setDesiredBasket(double desiredBasket) { this.desiredBasket = desiredBasket; }

    public double getDesiredPrice() { return  this.desiredPrice; }
    public void setDesiredPrice(double desiredPrice) { this.desiredPrice = desiredPrice; }

    public HashMap<String,Double> getColatWanted() { return this.colatWanted; }
    public void setColatWanted(HashMap<String,Double> colatWanted) { this.colatWanted = colatWanted; }
    public HashMap<String,Double> addCollateralWanted(String collateralType, double amount) {
        if(!this.colatWanted.containsKey(collateralType)) {
            this.colatWanted.put(collateralType, amount);
        }
        else {
            this.colatWanted.replace(collateralType, amount);
        }
        return this.colatWanted;
    }


    // Variables
    public ArrayList<User> buyerList = new ArrayList<>();
    public ArrayList<User> sellerList = new ArrayList<>();
    public static String[] collateralTypes = {"A-XRP", "ETH", "LINK", "W-BTC", "USDT", "P-LTC"};
    public static double targeting = 10;
    public static int salesCount = 0;


    // Methods
    private static int findUserByID(ArrayList<User> users, String userID) {

        for(int i = 0; i < users.size(); i ++) {
            if(users.get(i).getUserID().equals(userID)) return i;
        }
        System.exit(0);
        return 0;
    }


    public static ArrayList<User> getInitialUsers(double basketPrice, ArrayList<Vault> vaults) throws IOException {
        String usersDataPath = "/home/samir/Documents/Year4/Dissertation/BasketSimulation/Data/User-Data/Users_Initial.json";
        JSONObject fullJson = JsonReader.readJsonFromFile(usersDataPath);
        ArrayList<User> users = new ArrayList<User>();

        for(String key: fullJson.keySet()) {
            User currUser;
            String currUserID;
            double currBasketHoldings;
            double currBasketTokens;
            double currDesiredBasket;
            ArrayList<Vault> currVaults = new ArrayList<Vault>();
            HashMap<String,Double> currColatWanted = new HashMap<String,Double>();
            HashMap<String,Double> currCollaterals = new HashMap<String,Double>();
            HashMap<String, Double> currFeesOwed = new HashMap<String, Double>();

            JSONArray result = fullJson.getJSONArray(key);
            JSONObject value = result.getJSONObject(0);
            currUserID = key;

            for(Vault vault : vaults) {
                if(vault.getOwnerID().equals(key)) currVaults.add(vault);
            }
            currBasketHoldings = value.getDouble("Basket Holdings");
            currBasketTokens = currBasketHoldings/basketPrice;
            currCollaterals.put("W-BTC", value.getDouble("W-BTC Holdings"));
            currCollaterals.put("ETH", value.getDouble("ETH Holdings"));
            currCollaterals.put("P-LTC", value.getDouble("P-LTC Holdings"));
            currCollaterals.put("USDT", value.getDouble("USDT Holdings"));
            currCollaterals.put("LINK", value.getDouble("LINK Holdings"));
            currCollaterals.put("A-XRP", value.getDouble("A-XRP Holdings"));

            currFeesOwed.put("Stability Fee", 0.0);
            currFeesOwed.put("Liquidation Fee", 0.0);

            currDesiredBasket = 0.0;

            currColatWanted.put("W-BTC", 0.0);
            currColatWanted.put("ETH", 0.0);
            currColatWanted.put("P-LTC", 0.0);
            currColatWanted.put("USDT", 0.0);
            currColatWanted.put("LINK", 0.0);
            currColatWanted.put("A-XRP", 0.0);

            currUser = new User(currUserID, currVaults, currCollaterals, currBasketHoldings, currBasketTokens, currFeesOwed, currDesiredBasket, 0.0, currColatWanted);

            users.add(currUser);

        }

        return users;
    }


    public void generateStabilityFees(ArrayList<CollateralOracle> oracles) {
        String type;
        double amount;
        double multiplier = 0.0;
        Oracle oracle;
        for(Vault v : getVaults()) {
            type = v.getCollateralType();
            for( CollateralOracle o : oracles) {
                if(o.getCollateralType().equals(type)) multiplier = o.getStabilityFee();
            }
            amount = v.getCollateralAmount();
            addFees("Stability Fee", amount*multiplier/(100*365));
        }
    }


    public static double[] generateUserWants(ArrayList<User> userList, ArrayList<User> sellers, ArrayList<User> buyers, double userSeed, double basketPrice,
                                             double collateralSeed, ArrayList<CollateralOracle> colatOracles, VaultManagerOracle vaultManagerOracle) {
        sellers.clear();
        buyers.clear();

        double[] tradeInfo = new double[5];

        int buyerNum = 0;
        int sellerNum = 0;
        double basketSale = 0;
        double basketBuy = 0;

        for(User u : userList) {
            Random ran = new Random();
            u.generateStabilityFees(colatOracles);

            Random rn = new Random();
            int status = rn.nextInt(50) + 1;
            int vaultDescision = rn.nextInt(3);
            HashMap<String,Double> userColats = new HashMap<>();

            if(u.getBsktHoldings() <= 0 && (status == 2 || status == 8 || status == 43 || status == 44 || status == 27 || status == 28)) {
                u.setDesiredBasket(u.getDesiredBasket()/2 + collateralSeed * (0 + Math.random() * (1)));
                u.setDesiredPrice((.03 + targeting - Math.abs((targeting * ran.nextGaussian()/ThreadLocalRandom.current().nextInt(150, 300 + 1)))));
                buyers.add(u);
                buyerNum++;
                basketBuy += u.getDesiredBasket();
            }

            if(u.bsktHoldings > 0 &&(status == 17 || status == 12 || status == 27 || status == 28)) {
                u.setDesiredBasket(u.getDesiredBasket()/2 + (collateralSeed * (0 + Math.random() * (1))));
                u.setDesiredPrice((.03 + targeting - Math.abs((targeting * ran.nextGaussian()/ThreadLocalRandom.current().nextInt(150, 300 + 1)))));
                if(vaultDescision == 2) {
                    userColats = u.getCollaterals();
                    for(String colat : DataExtraction.shuffleArray(CollateralOracle.collateralTypes)) {
                        if(userColats.get(colat) > u.getDesiredBasket() * 1.5) {
                            Vault.openVault(u, u.getUserID(), u.getDesiredBasket(), u.getDesiredBasket()/basketPrice, colat, u.getDesiredBasket() * 1.5, vaultManagerOracle);
                        }
                    }
                }
                else {
                    buyers.add(u);
                    buyerNum++;
                    basketBuy += u.getDesiredBasket();
                }
            }

            if(u.bsktHoldings > 0 && (status == 13 || status == 37 || status == 36 || status == 35)) {
                String colat = CollateralOracle.collateralTypes[rn.nextInt(6)];
                if(!u.getVaults().isEmpty()) {
                    Vault.closeVault(vaultManagerOracle, u, u.getVaults().get(0), basketPrice);
                }
                else {
                    u.addCollateralWanted(colat, u.getDesiredBasket() + (userSeed * (0 + Math.random() * (2))));
                    u.setDesiredPrice(.03 + targeting - Math.abs((targeting * ran.nextGaussian()/ThreadLocalRandom.current().nextInt(150, 300 + 1))));
                    sellers.add(u);
                    sellerNum++;
                    basketSale += u.getColatWanted().get(colat);
                }
            }

            if(u.bsktHoldings <= 0 && (status == 5 || status == 41 || status == 42 || status == 46) ) {
                String colat = CollateralOracle.collateralTypes[rn.nextInt(6)];
                u.addCollateralWanted(colat, userSeed * (0 + Math.random() * (2)));
                u.setDesiredPrice((.03 + targeting - Math.abs((targeting * ran.nextGaussian()/ThreadLocalRandom.current().nextInt(150, 300 + 1)))));
                sellers.add(u);
                sellerNum++;
                basketSale += u.getColatWanted().get(colat);
            }

        }

        tradeInfo[0] = buyerNum;
        tradeInfo[1] = basketBuy;
        tradeInfo[2] = sellerNum;
        tradeInfo[3] = basketSale;
        tradeInfo[4] = 0;

        return tradeInfo;
    }


    public static void generateNewUsers(ArrayList<User> userBase, double userSeed, double collateralSeed, double basketPrice, VaultManagerOracle vaultManagerOracle) throws IOException {
        String[] collaterals = collateralTypes;
        int userBaseSize = userBase.size();
        Random rn = new Random();
        int newUsers = (int)Math.log(rn.nextInt(userBaseSize/10) + userBaseSize/30);
        int selector;
        User user;

        Random userSeedRandom = new Random();

        String userID;
        ArrayList<Vault> userVaults = new ArrayList<>();
        HashMap<String,Double> userCollaterals = new HashMap<>();
        double userBsktTokens;
        double userBsktMinted;
        HashMap<String, Double> feesOwed = new HashMap<String, Double>();
        feesOwed.put("Stability Fee", 0.0);
        feesOwed.put("Liquidation Fee", 0.0);
        double desiredBasket = 0.0;
        HashMap<String, Double> collateralsWanted = new HashMap<>();
        collateralsWanted.put("W-BTC", 0.0);
        collateralsWanted.put("ETH", 0.0);
        collateralsWanted.put("P-LTC", 0.0);
        collateralsWanted.put("USDT", 0.0);
        collateralsWanted.put("LINK", 0.0);
        collateralsWanted.put("A-XRP", 0.0);


        for(int i = 0; i < newUsers; i++) {
            userID = DataExtraction.generateUserID();

            userBsktMinted = userSeedRandom.nextGaussian() * (userSeed/5) + userSeed;
            userBsktTokens = userBsktMinted/basketPrice;

            for(String colat : collaterals) {
                if(userSeedRandom.nextInt(2) == 1) userCollaterals.put(colat, userSeedRandom.nextGaussian() * collateralSeed/5 + collateralSeed);
                else userCollaterals.put(colat, 0.0);
            }

            rn = new Random();
            selector = rn.nextInt(4);

            user = new User(userID, userVaults, userCollaterals, userBsktMinted, userBsktTokens, feesOwed, desiredBasket, 0.0, collateralsWanted);

            if(selector == 3 || selector == 2) {
                Vault.openVault(user, userID,  userBsktMinted, userBsktTokens, collaterals[userSeedRandom.nextInt(5)], userBsktMinted * 1.5, vaultManagerOracle);
            }

            userBase.add(user);
        }

    }


    public static void buyBasket(User buyer, User seller, double payment, double basketPrice, String collateralType) {
        double basketTokens = buyer.getDesiredBasket()/basketPrice;
        buyer.setBsktHoldings(buyer.getBsktHoldings() + basketTokens * basketPrice);
        buyer.setBsktTokens(buyer.getBsktTokens() + basketTokens);
        buyer.setDesiredBasket(buyer.getDesiredBasket() - payment);
        buyer.addCollaterals(collateralType, buyer.getCollaterals().get(collateralType) - payment);

        seller.setBsktHoldings(seller.getBsktHoldings() - basketTokens * basketPrice);
        seller.setBsktTokens(seller.getBsktTokens() - basketTokens);
        seller.addCollateralWanted(collateralType, seller.getColatWanted().get(collateralType) - payment);
        seller.addCollaterals(collateralType, seller.getCollaterals().get(collateralType) + payment);

    }


    public static void generateUserCollaterals(ArrayList<User> userBase, double collateralSeed) {
        String[] collaterals = collateralTypes;
        HashMap<String,Double> userCollaterals;
        Random userSeedRandom = new Random();

        for(User u : userBase) {
            for(String colat : collaterals) {
                if(userSeedRandom.nextInt(5) == 2) u.addCollaterals(colat, u.getCollaterals().get(colat) + userSeedRandom.nextGaussian() * collateralSeed/5 + collateralSeed);
                else u.addCollaterals(colat, 0.0);
            }
        }

    }


    public void payStabilityFee(double basketPrice) {
        double stabilityFee = getFeesOwed().get("Stability Fee");
        setBsktHoldings(getBsktHoldings() - stabilityFee);
        setBsktTokens(getBsktTokens() - stabilityFee/basketPrice);
        addFees("Stability Fee", getFeesOwed().get("Stability Fee") - stabilityFee);
     }


    public void payLiquidationFee(double basketPrice) {
        double liquidationFee = getFeesOwed().get("Liquidation Fee");
        setBsktHoldings(getBsktHoldings() - liquidationFee);
        setBsktTokens(getBsktTokens() - liquidationFee/basketPrice);
        addFees("Liquidation Fee", getFeesOwed().get("Liquidation Fee") - liquidationFee);
    }


    public static double marketTrades(double basketPrice, ArrayList<User> userBase, ArrayList<User> buyers, ArrayList<User> sellers, EmergencyOracle emergencyOracle) {
        ArrayList<Double> basketSales = new ArrayList<>();
        double totalSales = 0;

        String buyerID;
        String sellerID;
        String colat = collateralTypes[ThreadLocalRandom.current().nextInt(0, 5 + 1)];

        for(User s: sellers) {
            sellerID = s.userID;
            for(User b: buyers) {
                for(String collateralType: collateralTypes) {
                    if(s.getColatWanted().get(collateralType) >= b.getCollaterals().get(collateralType)) {
                        colat = collateralType;
                        break;
                    }
                }
                if(b.getDesiredPrice() >= s.getDesiredPrice() && b.getDesiredBasket() >= s.getColatWanted().get(colat)) {
                    buyerID = b.getUserID();
                    salesCount++;
                    buyBasket(userBase.get(findUserByID(userBase, buyerID)), userBase.get(findUserByID(userBase, sellerID)), (b.getDesiredBasket()/basketPrice)*b.getDesiredPrice(), basketPrice, colat);
                    basketSales.add(b.getDesiredPrice());
                }
                break;
            }
        }

        for(double value : basketSales) totalSales += value;
        emergencyOracle.setSales(salesCount);
        if(!basketSales.isEmpty()) return totalSales/basketSales.size();
        else return basketPrice;
    }

}
