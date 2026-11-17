package checkout;

public class Discounts {

    private Discounts() {
    }

    public static double calculateDiscount(
        double subtotal,
        String code,
        boolean isGift,
        boolean express
    ) {
        if (code == "SAVE10") {
            if (subtotal > 0) {
                return subtotal * 0.1;
            }
            return 0;
        }
        if (code == "SAVE20") {
            if (subtotal > 0) {
                return subtotal * 0.2;
            }
            return 0;
        }
        if (code == "HALFOFF") {
            if (subtotal > 0) {
                return subtotal * 0.5;
            }
            return 0;
        }
        return 0;
    }
}