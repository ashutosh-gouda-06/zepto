# Zepto

A quick-commerce backend modelled on dark stores: users near one or more stores can browse what's in stock and place orders. An order is fulfilled from the closest store if it has everything, otherwise it is split across nearby stores with one delivery partner per store.

Built with Spring Boot 4.1 and Java 25. All data is in memory and seeded on startup, so stock and orders reset on every restart.

## Prerequisites

- JDK 25
- Maven 3.9+

## Running

```bash
mvn spring-boot:run
```

The app listens on port **8082** (set in `src/main/resources/application.properties`). To use another port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

Run the tests with:

```bash
mvn test
```

## Seed data

Three dark stores are created at startup (`config/DataInitializer`). Coordinates are on a plain x/y grid in km.

| Store      | Location | Stock                          |
|------------|----------|--------------------------------|
| DarkStoreA | (0, 0)   | Apple ×5, Banana ×2            |
| DarkStoreB | (4, 1)   | Apple ×3, Chocolate ×10        |
| DarkStoreC | (2, 3)   | Banana ×5, T-Shirt ×7          |

Product catalogue (`factory/ProductFactory`):

| SKU | Name      | Price (₹) |
|-----|-----------|-----------|
| 101 | Apple     | 20        |
| 102 | Banana    | 10        |
| 103 | Chocolate | 50        |
| 201 | T-Shirt   | 500       |
| 202 | Jeans     | 1000      |

Any other SKU is created as `Item<sku>` at ₹100.

Only stores within **5 km** of the user are considered for browsing and ordering.

## API

Base URL: `http://localhost:8082`

### List nearby products

`GET /api/products?x={x}&y={y}`

Returns the distinct products in stock at any store within 5 km of `(x, y)`.

```bash
curl 'http://localhost:8082/api/products?x=1&y=1'
```

```json
[
  { "sku": 101, "name": "Apple",     "price": 20.0 },
  { "sku": 102, "name": "Banana",    "price": 10.0 },
  { "sku": 201, "name": "T-Shirt",   "price": 500.0 },
  { "sku": 103, "name": "Chocolate", "price": 50.0 }
]
```

| Status | When                                |
|--------|-------------------------------------|
| 200    | Always (empty list if no store is in range) |
| 400    | `x` or `y` missing                  |

### Place an order

`POST /api/orders`

```bash
curl -X POST http://localhost:8082/api/orders \
  -H 'Content-Type: application/json' \
  -d '{
        "userName": "Aditya",
        "x": 1.0,
        "y": 1.0,
        "items": [
          { "sku": 101, "quantity": 4 },
          { "sku": 102, "quantity": 3 },
          { "sku": 103, "quantity": 2 }
        ]
      }'
```

```json
{
  "orderId": 1,
  "userName": "Aditya",
  "items": [
    { "sku": 101, "name": "Apple",     "quantity": 4, "price": 20.0 },
    { "sku": 102, "name": "Banana",    "quantity": 2, "price": 10.0 },
    { "sku": 102, "name": "Banana",    "quantity": 1, "price": 10.0 },
    { "sku": 103, "name": "Chocolate", "quantity": 2, "price": 50.0 }
  ],
  "totalAmount": 210.0,
  "deliveryPartners": ["Partner1", "Partner2", "Partner3"]
}
```

How it's fulfilled:

1. Stores within 5 km are sorted by distance from the user.
2. If the closest store has every item in the requested quantity, the whole order ships from there with one delivery partner.
3. Otherwise the order is split: each store, nearest first, supplies whatever it can of the remaining items, and every store that supplies something gets its own delivery partner. That's why the example above has Banana twice: 2 from DarkStoreA and 1 from DarkStoreC.
4. Anything no nearby store can supply is left out of the order (logged as "Could not fulfill"), and `totalAmount` covers only what was supplied.

| Status | When                                              |
|--------|---------------------------------------------------|
| 201    | Order placed                                      |
| 400    | `items` is empty, or any `quantity` is 0 or less  |
| 422    | No dark store within 5 km of the user             |

### List orders

`GET /api/orders`

Returns every order placed since startup, in the same shape as the place-order response.

```bash
curl http://localhost:8082/api/orders
```

### Errors

Errors use Spring Boot's default error body:

```json
{
  "timestamp": "2026-10-17T18:19:02.702Z",
  "status": 422,
  "error": "Unprocessable Content",
  "path": "/api/orders"
}
```

## Postman

`postman/zepto.postman_collection.json` has every endpoint above, with test assertions. Import it into Postman, or run it from the command line with Newman:

```bash
npx newman run postman/zepto.postman_collection.json
```

The `baseUrl` collection variable defaults to `http://localhost:8082`. Run the collection top to bottom against a **freshly started** app: placing orders uses up the in-memory stock, and the expected totals assume the seed data.

## Project layout

```
src/main/java/org/example
├── ZeptoApplication.java      Spring Boot entry point
├── catalog/                   CatalogService: nearby product listing
├── config/                    DataInitializer: seeds stores and stock
├── factory/                   ProductFactory: SKU -> product
├── inventory/                 InventoryStore interface + in-memory implementation
├── model/                     Product, User, Cart, Order, DeliveryPartner, Pair
├── order/                     OrderManager: order placement and splitting
├── replenish/                 Stock replenishment strategies (threshold, weekly)
├── store/                     DarkStore, DarkStoreManager
└── web/                       REST controllers and request/response DTOs
```
