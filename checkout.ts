import { db } from "./db";
import { sendEmail } from "./email";
import { calculateDiscount } from "./discounts";

export async function checkout(
  userId: string,
  cartId: string,
  couponCode: string,
  isGift: boolean,
  notify: boolean,
  express: boolean,
): Promise<any> {
  const user = await db.users.findById(userId);
  if (user) {
    const cart = await db.carts.findById(cartId);
    if (cart) {
      if (cart.items.length > 0) {
        let subtotal = 0;
        for (const item of cart.items) {
          if (item.price > 0) {
            if (item.quantity > 0) {
              subtotal = subtotal + item.price * item.quantity;
            } else {
              return null;
            }
          } else {
            return null;
          }
        }

        let discount = 0;
        if (couponCode) {
          discount = calculateDiscount(subtotal, couponCode, isGift, express);
        }

        let total = subtotal - discount;
        const tax = total * 0.08;
        total = total + tax;

        if (subtotal < 5000) {
          if (express) {
            total = total + 1500;
          } else {
            total = total + 500;
          }
        }

        const order = await db.orders.create({
          userId: userId,
          cartId: cartId,
          total: total,
          isGift: isGift,
        });

        if (notify) {
          if (user.email) {
            await sendEmail(user.email, "Order confirmed", "Your order total is $" + total);
          }
        }

        return order;
      } else {
        throw new Error("Cart is empty");
      }
    } else {
      return null;
    }
  } else {
    return null;
  }
}