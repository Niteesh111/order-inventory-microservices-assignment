# Order & Inventory Microservices

**Developer : Niteesh**

## Overview
The project consists of two Spring Boot microservices:

1. **Inventory Service** – Manages inventory details.
2. **Order Service** – Places customer orders by checking and updating inventory through REST communication.
## Tech Stack

| Component     | Technology                      |
|---------------|---------------------------------|
| Language      | Java 17                         |
| Framework     | Spring Boot                     |
| Database      | H2 In-Memory DB                 |
| Communication | RestTemplate                    |
| Testing       | JUnit , Mockito, SpringBootTest |
| Build Tool    | Gradle                          |

## API Endpoints
1. **Inventory Service**(Port: 8082)

   |  Method | Endpoint  | Description  |
       |---|---|---|
   |  GET |  /inventory/{productId} |  Returns sorted batches by expiry date |
   | GET  |  /inventory/quantity/{productId} |  Returns available quantity |
   |  POST | /inventory/update  | Updates inventory based on order  |

    * **Get inventory batches sorted by expiry date**

        GET : http://localhost:8082/inventory/{productId}

        Response

            [
                {
                    "productId": 101,
                    "batchId": 1,
                    "quantity": 10,
                    "expiryDate": "2025-12-01"
                }
            ]

   * **Update inventory after order**

       POST : http://localhost:8082/inventory/update

       Request Body

           {
               "productId": 101,
               "quantity": 5,
               "updatedTime": "2025-11-22T10:10:00Z"
           }

2. **Order Service**(Port: 8081)

   | Method  | Endpoint  |  Description |
       |---|---|---|
   |  POST |  /order/create |  Places an order and updates inventory |

* **Place an Order**

    POST :  http://localhost:8081/order/create

    Request

        {
            "customerId": "KOR-25101",
            "order":{
                "productId": 100,
                "quantity": 10
            }
        }

    Response 

        {
            "orderId": 501,
            "productId": 101,
            "quantity": 5,
            "orderStatus": "Order placed successfully"
        }

## Database
DB details:

**url**: jdbc:h2:mem:inventorydb;DB_CLOSE_DELAY=-1;MODE=MYSQL

**username**: koerber

**password**: Leave it blank

| Service  | H2 Console  |
|---|---|
|  Inventory Service |  http://localhost:8082/h2-console |
|  Order Service | http://localhost:8081/h2-console  |

Execute the INSERT queries in the **Inventory** table before executing an order

* **File Name** : Inventory.sql
* **Path from project repo root**      : inventory-service/src/main/resources/Inventory.sql








