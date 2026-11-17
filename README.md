# Order Checkout Service

A small checkout module that prices a cart and places an order.

## Overview

Given a user, a cart, and an optional coupon code, the service validates the cart's line items, computes the price (subtotal, coupon discount, tax, shipping), persists the order, and sends a confirmation email. The coupon math lives in a separate `Discounts` helper so it is kept in one place.

## Usage

```java
Checkout checkout = new Checkout(db, mailer);

Order order = checkout.checkout(
    "user_123",
    "cart_456",
    "SAVE10",
    false, // isGift
    true,  // notify
    false  // express
);

System.out.println(order);
```

## API

```java
public Checkout(Database db, Mailer mailer)

public Order checkout(
    String userId,
    String cartId,
    String couponCode,
    boolean isGift,
    boolean notify,
    boolean express
)
```

`Checkout` is constructed with a `Database` and a `Mailer`. The `checkout` method loads the user and cart, walks each line item to build the subtotal, applies the coupon discount, adds tax, then adds shipping when the subtotal is under the free-shipping threshold. It persists the order via `db.orders().create(...)` and, when `notify` is set and the user has an email, sends a confirmation through `mailer.send(...)`.

```java
public static double Discounts.calculateDiscount(
    double subtotal,
    String code,
    boolean isGift,
    boolean express
)
```

Returns the discount amount for a coupon code. Supported codes:

- `SAVE10` — 10% of the subtotal
- `SAVE20` — 20% of the subtotal
- `HALFOFF` — 50% of the subtotal

Any other code returns `0`.

Pricing order: subtotal -> discount -> tax -> shipping.

## Configuration

The pricing parameters are set in `Checkout.java`:

- Tax rate: `0.08` applied to the discounted subtotal.
- Free-shipping threshold: subtotals of `5000` and above ship free.
- Shipping cost below the threshold: `1500` for express, `500` otherwise.

`Checkout` depends on the `Database` (`db.users()`, `db.carts()`, `db.orders()`) and `Mailer` collaborators passed into its constructor.
