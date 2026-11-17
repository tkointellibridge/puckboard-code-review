# Order Checkout Service

A small checkout module that prices a cart and places an order.

## Overview

Given a user, a cart, and an optional coupon code, the service validates the cart's line items, computes the price (subtotal, coupon discount, tax, shipping), persists the order, and sends a confirmation email. The pricing logic lives in a separate discounts helper so coupon math is kept in one place.

## Usage

```ts
import { checkout } from "./checkout";

const order = await checkout(
  "user_123",
  "cart_456",
  "SAVE10",
  false, // isGift
  true,  // notify
  false, // express
);

console.log(order);
```

## API

```ts
checkout(
  userId: string,
  cartId: string,
  couponCode: string,
  isGift: boolean,
  notify: boolean,
  express: boolean,
): Promise<any>
```

Loads the user and cart, walks each line item to build the subtotal, applies the coupon discount, adds tax, then adds shipping when the subtotal is under the free-shipping threshold. It persists the order via `db.orders.create` and, when `notify` is set and the user has an email, sends a confirmation through `sendEmail`.

```ts
calculateDiscount(
  subtotal: number,
  code: string,
  isGift: boolean,
  express: boolean,
): number
```

Returns the discount amount for a coupon code. Supported codes:

- `SAVE10` — 10% of the subtotal
- `SAVE20` — 20% of the subtotal
- `HALFOFF` — 50% of the subtotal

Any other code returns `0`.

Pricing order: subtotal -> discount -> tax -> shipping.

## Configuration

The pricing parameters are set in `checkout.ts`:

- Tax rate: `0.08` applied to the discounted subtotal.
- Free-shipping threshold: subtotals of `5000` and above ship free.
- Shipping cost below the threshold: `1500` for express, `500` otherwise.

The `checkout` function depends on a `db` data layer (`db.users`, `db.carts`, `db.orders`) and a `sendEmail` function, both imported from sibling modules.
