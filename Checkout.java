package checkout;

public class Checkout {

    private final Database db;
    private final Mailer mailer;

    public Checkout(Database db, Mailer mailer) {
        this.db = db;
        this.mailer = mailer;
    }

    public Order checkout(
        String userId,
        String cartId,
        String couponCode,
        boolean isGift,
        boolean notify,
        boolean express
    ) {
        User user = db.users().findById(userId);
        if (user != null) {
            Cart cart = db.carts().findById(cartId);
            if (cart != null) {
                if (cart.getItems().size() > 0) {
                    double subtotal = 0;
                    for (Item item : cart.getItems()) {
                        if (item.getPrice() > 0) {
                            if (item.getQuantity() > 0) {
                                subtotal = subtotal + item.getPrice() * item.getQuantity();
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }

                    double discount = 0;
                    if (couponCode != null) {
                        discount = Discounts.calculateDiscount(subtotal, couponCode, isGift, express);
                    }

                    double total = subtotal - discount;
                    double tax = total * 0.08;
                    total = total + tax;

                    if (subtotal < 5000) {
                        if (express) {
                            total = total + 1500;
                        } else {
                            total = total + 500;
                        }
                    }

                    Order order = db.orders().create(userId, cartId, total, isGift);

                    if (notify) {
                        if (user.getEmail() != null) {
                            mailer.send(user.getEmail(), "Order confirmed", "Your order total is $" + total);
                        }
                    }

                    return order;
                } else {
                    throw new RuntimeException("Cart is empty");
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}