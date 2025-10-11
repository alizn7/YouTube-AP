package model;

public enum SubscriptionPlan {
    BRONZE(30, 5),
    SILVER(60, 9),
    GOLD(180, 14);

    private final int days;
    private final double price;

    SubscriptionPlan(int days, double price) {
        this.days = days;
        this.price = price;
    }

    public int getDays() {
        return days;
    }

    public double getPrice() {
        return price;
    }
}