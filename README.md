# E-Commerce Order Management System (Dropwizard)

This project is a **REST API backend** for an e-commerce ordering system built with **Dropwizard**.  
It supports **customer ordering**, **inventory management**, **promo code discounts**, and **admin controls** with secure authentication and authorization.


This service provides:
- **User registration & login**
- **Product catalog browsing**
- **Placing and canceling orders**
- **Applying promo codes with usage limits**
- **Admin controls for products and orders**
- **Role-based access (CUSTOMER / ADMIN)**
- **JWT token-based authentication**


##  **Authentication**

- All login endpoints return a **JWT token**.
- All **user actions** require `Bearer <token>` header.
- Admin APIs require valid token **and ADMIN role**.






